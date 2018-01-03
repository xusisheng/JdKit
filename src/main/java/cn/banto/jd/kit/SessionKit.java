package cn.banto.jd.kit;

import cn.banto.jd.exception.JdException;
import cn.banto.jd.utils.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 会话工具包
 * @author BANTO
 */
public class SessionKit extends AbstractKit {

    private Logger logger = LoggerFactory.getLogger(SessionKit.class);
    private final static String LOGIN_REFERER = "https://passport.jd.com/new/login.aspx?ReturnUrl=https%3A%2F%2Fwww.jd.com%2F";
    private Ocr ocr;
    private String ocrId;

    public SessionKit(HttpRequest httpRequest, Ocr ocr) {
        super(httpRequest);
        this.ocr = ocr;
    }

    /**
     * 检查是否登录
     * @return
     */
    public boolean isLogin(){
        String url = "https://home.jd.com/";
        String content = httpRequest.get(url, null);

        return content.indexOf("欢迎登录") == -1;
    }

    /**
     * 使用账号和密码登录
     * @param username
     * @param password
     * @throws Exception
     */
    public void login(String username, String password){
        login(username, password, "", "");
    }

    /**
     * 使用账号和密码登录
     * eid和fp经过测试可以为空，但建议加上。
     * eid和fp在同一浏览器一般是不会变的，所以可以手动抓包获得
     * @param username
     * @param password
     * @param eid
     * @param fp
     */
    public void login(String username, String password, String eid, String fp) {
        //获取登录必要参数
        Map params = getLoginParam(username, password, eid, fp);
        //请求登录
        String url = "https://passport.jd.com/uc/loginService?uuid=" + params.get("uuid");
        String content = httpRequest.post(url, params, LOGIN_REFERER);
        JSONObject json = JSON.parseObject(JsonUtil.jsonpToJson(content));
        //检查登录状态
        if (!json.containsKey("success")) {
            if (json.containsKey("emptyAuthcode")) {
                ocr.feedback(ocrId);
                throw new JdException(json.getString("emptyAuthcode"));
            } else if (json.containsKey("pwd")) {
                throw new JdException(json.getString("pwd"));
            } else {
                throw new JdException(content);
            }
        }
    }



    /**
     * 通过二维码的方式登录
     * @param file
     */
    public void qrcodeLogin(File file){
        try {
            String url = "https://qr.m.jd.com/show?appid=133&size=147";
            InputStream in = httpRequest.download(url);
            //将二维码写入文件
            if(! file.exists()){
                file.createNewFile();
            }
            StreamUtil.inputstreamToFile(in, file);
            String token = httpRequest.getCookie("wlfstk_smdl");
            logger.debug("已写入到{}, 请即使扫描。", file.getAbsolutePath());
            logger.debug("qrcode token = {}", token);
            while (! qrCodeCheck(token)) {
                Thread.sleep(3000);
            }
        } catch (IOException e ) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查登录二维码是否有效
     * @param token
     * @return
     */
    private boolean qrCodeCheck(String token){
        String url = "https://qr.m.jd.com/check?callback=jQuery392929&appid=133&token=" + token;
        String content = httpRequest.get(url, LOGIN_REFERER);
        logger.debug("二维码状态: {}" + content);

        JSONObject json = JSON.parseObject(JsonUtil.jsonpToJson(content));
        switch (json.getInteger("code")) {
            case 200:
                String ticket = json.getString("ticket");
                qrCodeTicketValidation(ticket);
                return true;
            //break;
            case 201:
                //未扫描，不处理
                break;
            case 202:
                break;
            default:
                throw new JdException(json.getString("msg"));
                //break;
        }

        return false;
    }

    /**
     * 检查二维码登录获得的ticket是否有效
     * @param ticket
     * @return
     */
    private boolean qrCodeTicketValidation(String ticket){
        String url = "https://passport.jd.com/uc/qrCodeTicketValidation?t=" + ticket;
        String content = httpRequest.get(url, LOGIN_REFERER);
        logger.debug("validaion = {}", content);

        JSONObject json = JSON.parseObject(JsonUtil.jsonpToJson(content));
        if(json.containsKey("returnCode") && json.getInteger("returnCode") == 0){
            return true;
        }

        return false;
    }

    /**
     * 获取login时需要的表单参数
     * @return
     */
    private Map getLoginParam(String loginName, String password, String eid, String fp) {
        HashMap<String, String> params = new HashMap<String, String>(12);

        String url = "https://passport.jd.com/new/login.aspx";
        String content = httpRequest.get(url, null);

        Document document = Jsoup.parse(content);
        //获取表单数据
        Elements elements = document.select("#formlogin input");
        for (Element element : elements) {
            String key = element.attr("name");
            String value = element.val();
            params.put(key, value);
        }
        //检查是否需要验证码
        if (isNeedLoginVerify(loginName)) {
            String verifyCodeUrl = "https:" + document.select("#JD_Verification1").attr("src2").replace("&amp;", "&");
            params.put("authcode", getVerifyCode(verifyCodeUrl));
        }

        try {
            //rsa加密密码
            byte[] result = RSA.encryptByPublicKey(password, params.get("pubKey"));
            password = (new BASE64Encoder()).encode(result).replaceAll("\\s+", "");

            params.put("loginname", loginName);
            params.put("nloginpwd", password);
            params.put("eid", eid);
            params.put("fp", fp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return params;
    }

    /**
     * 获取验证码识别结果
     * @param verifyCodeUrl
     * @return
     */
    private String getVerifyCode(String verifyCodeUrl){
        String referer = "https://passport.jd.com/uc/login?ltype=logout";
        InputStream in = httpRequest.download(verifyCodeUrl, referer);
        Ocr.Result result = ocr.ocr(in);
        if(result != null){
            ocrId = result.getId();
            return result.getResult();
        }

        return "";
    }

    /**
     * 检查登录是否需要验证码
     * @param loginName
     * @return
     * @throws Exception
     */
    private boolean isNeedLoginVerify(String loginName) {
        String url = "https://passport.jd.com/uc/showAuthCode?version=2015";
        HashMap<String, String> params = new HashMap<String, String>(1);
        params.put("loginName", loginName);

        String content = httpRequest.post(url, params);
        JSONObject json = JSON.parseObject(JsonUtil.jsonpToJson(content));

        return json.containsKey("verifycode") && json.getBoolean("verifycode");
    }
}
