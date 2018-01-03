package cn.banto.jd.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

/**
 * 验证码识别类
 * @author BANTO
 */
public class Ocr {

    private Logger logger = LoggerFactory.getLogger(Ocr.class);
    private String username;
    private String password;
    private HttpRequest httpRequest;
    private final static String SOFT_ID = "50982";
    private final static String SOFT_KEY = "c18176729745411d9f4f34f3479cce9b";
    private final static String OCR_URL = "http://api.ysdm.net/create.json";
    private final static String FEEDBACK_URL = "http://api.ysdm.net/reporterror.json";

    public Ocr(String username, String password){
        this.username = username;
        this.password = password;
        this.httpRequest = new HttpRequest();
    }

    /**
     * 识别验证码
     * @param image
     * @return
     */
    public Result ocr(File image){
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("username", username);
        params.put("password", password);
        params.put("typeid", "3040");
        params.put("softid", SOFT_ID);
        params.put("softkey", SOFT_KEY);
        params.put("image", image);
        String content = httpRequest.upload(OCR_URL, params);

        logger.debug("OCR识别结果: {}", content);
        JSONObject jsonObject = JSON.parseObject(content);
        if (jsonObject.containsKey("Result") && jsonObject.containsKey("Id")) {
            Result result = new Result();
            result.setResult(jsonObject.getString("Result"));
            result.setId(jsonObject.getString("Id"));

            return result;
        }

        return null;
    }

    /**
     * 识别验证码
     * @param in
     * @return
     */
    public Result ocr(InputStream in){
        File image = null;
        try {
            image = File.createTempFile("ocr_tmp_", ".png");
            StreamUtil.inputstreamToFile(in, image);
            return ocr(image);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (image != null) {
                image.deleteOnExit();
            }
        }

        return null;
    }

    /**
     * 识别错误反馈
     * @param id
     * @return
     */
    public void feedback(String id){
        logger.debug("反馈错误答题: {}", id);
        if(id != null) {
            HashMap<String, String> params = new HashMap<String, String>(5);
            params.put("username", username);
            params.put("password", password);
            params.put("softid", SOFT_ID);
            params.put("softkey", SOFT_KEY);
            params.put("id", id);
            httpRequest.post(FEEDBACK_URL, params);
        }
    }


    public static class Result {
        private String id;
        private String result;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
}
