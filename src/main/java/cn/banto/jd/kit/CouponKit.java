package cn.banto.jd.kit;

import cn.banto.jd.exception.JdException;
import cn.banto.jd.model.Coupon;
import cn.banto.jd.model.CouponCatalog;
import cn.banto.jd.utils.HttpRequest;
import cn.banto.jd.utils.JsonUtil;
import cn.banto.jd.utils.Ocr;
import cn.banto.jd.utils.StreamUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static cn.banto.jd.utils.JsonUtil.jsonpToJson;

/**
 * 优惠码工具包
 * @author BANTO
 */
public class CouponKit extends AbstractKit {

    private Logger logger = LoggerFactory.getLogger(CouponKit.class);
    private final static String COUPON_REFERER = "https://a.jd.com/";
    private Ocr ocr;
    private String ocrId;

    public CouponKit(HttpRequest httpRequest, Ocr ocr) {
        super(httpRequest);
        this.ocr = ocr;
    }

    /**
     * 获取优惠券分类列表
     * @return
     */
    public List<CouponCatalog> getCouponCatalogList(){
        ArrayList<CouponCatalog> couponCatalogs = new ArrayList<CouponCatalog>();
        String url = "https://a.jd.com/indexAjax/getCatalogList.html?callback=";
        String content = httpRequest.get(url, COUPON_REFERER);

        JSONObject json = JSON.parseObject(jsonpToJson(content));
        //判断获取优惠券分类列表是否成功
        if(json.containsKey("success") && json.getBoolean("success")){
            JSONArray list = json.getJSONArray("catalogList");
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                JSONObject info = (JSONObject) iterator.next();

                CouponCatalog catalog = new CouponCatalog();
                catalog.setId(info.getInteger("categoryId"));
                catalog.setName(info.getString("categoryName"));
                couponCatalogs.add(catalog);
            }
        }

        return couponCatalogs;
    }

    /**
     * 获取指定分类的优惠码
     * @param catalogId
     * @return
     */
    public List<Coupon> getCouponList(int catalogId, int page, int pageSize){
        List<Coupon> coupons = new ArrayList<Coupon>();
        try{
            String url = "https://a.jd.com/indexAjax/getCouponListByCatalogId.html?callback=&catalogId="+ catalogId +"&page="+ page +"&pageSize="+ pageSize;
            String content = httpRequest.get(url, COUPON_REFERER);

            JSONObject json = JSON.parseObject(JsonUtil.jsonpToJson(content));
            //判断获取优惠券列表是否成功
            if(json.containsKey("success") && json.getBoolean("success")){
                JSONArray list = json.getJSONArray("couponList");
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    JSONObject info = (JSONObject) iterator.next();
                    Coupon coupon = info.toJavaObject(Coupon.class);
                    coupons.add(coupon);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return coupons;
    }

    /**
     * 领取优惠券
     * @param coupon
     */
    public void getCoupon(Coupon coupon){
        if(coupon.getSortType() == 0 || coupon.getSortType() == 1 || coupon.getSortType() == 2){
            getDefCoupon(coupon);
        } else if(coupon.getSortType() == 3){
            getSpecialCoupon(coupon, "white");
        } else if(coupon.getSortType() == 4){
            getSpecialCoupon(coupon, "home");
        } else {
            throw new JdException("优惠券类型不被支持");
        }
    }

    /**
     * 领取普通优惠券
     * @param coupon
     */
    public void getDefCoupon(Coupon coupon){
        String url = "https://a.jd.com/indexAjax/getCoupon.html?callback=&key="+ coupon.getRuleKey() +"&type="+ coupon.getSortType();
        String content = httpRequest.get(url, COUPON_REFERER);

        JSONObject json = JSON.parseObject(JsonUtil.jsonpToJson(content));
        if(! "999".equals(json.getString("code"))){
            throw new JdException(json.getString("message"));
        }
    }

    /**
     * 领取特殊优惠券
     * @param coupon
     */
    public void getSpecialCoupon(Coupon coupon, String type){
        String captcha = getSpecialCouponCode(coupon, type);
        String url = "https://a.jd.com/indexAjax/specialCouponRec.html?callback=&key="+ coupon.getRuleKey() +"&type="+ type +"&operation=2&captcha="+ captcha;
        String content = httpRequest.get(url, COUPON_REFERER);

        JSONObject json = JSON.parseObject(jsonpToJson(content));
        String resultCode = json.getString("resultCode");
        if(! "0000".equals(resultCode)){
            if("9999".equals(resultCode)){
                ocr.feedback(ocrId);
            }
            throw new JdException(json.getString("message"));
        }
    }

    /**
     * 获取特殊优惠券的验证码
     * @param coupon
     * @param type
     * @return
     */
    private String getSpecialCouponCode(Coupon coupon, String type){
        String url = "https://a.jd.com/indexAjax/specialCouponRec.html?callback=&key="+ coupon.getRuleKey() +"&type="+ type +"&operation=1&captcha=1";
        String content = httpRequest.get(url, COUPON_REFERER);

        JSONObject json = JSON.parseObject(jsonpToJson(content));
        if(! "0000".equals(json.getString("resultCode"))){
            throw new JdException(json.getString("message"));
        }
        //ocr识别验证码
        String captcha = json.getString("captcha");
        InputStream in = StreamUtil.base64ToInputStream(captcha);
        Ocr.Result result = ocr.ocr(in);
        if(result != null){
            ocrId = result.getId();
            return result.getResult();
        }

        return "";
    }
}
