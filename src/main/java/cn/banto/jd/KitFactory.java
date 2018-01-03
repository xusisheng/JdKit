package cn.banto.jd;

import cn.banto.jd.kit.*;
import cn.banto.jd.utils.HttpRequest;
import cn.banto.jd.utils.Ocr;

/**
 * 京东助手
 * @author BANTO
 */
public class KitFactory {

    private Ocr ocr;
    private HttpRequest httpRequest;
    private AccountKit accountKit;
    private CouponKit couponKit;
    private SessionKit sessionKit;
    private CartKit cartKit;
    private InsuredKit insuredKit;

    public KitFactory(Ocr ocr){
        this.ocr = ocr;
        httpRequest = new HttpRequest();
    }

    /**
     * 获取账户工具包
     * @return
     */
    public AccountKit getAccountKit() {
        if(accountKit == null){
            accountKit = new AccountKit(httpRequest);
        }
        return accountKit;
    }

    /**
     * 获取优惠券工具包
     * @return
     */
    public CouponKit getCouponKit() {
        if(couponKit == null){
            couponKit = new CouponKit(httpRequest, ocr);
        }
        return couponKit;
    }

    /**
     * 获取会话工具包
     * @return
     */
    public SessionKit getSessionKit() {
        if(sessionKit == null){
            sessionKit = new SessionKit(httpRequest, ocr);
        }
        return sessionKit;
    }

    /**
     * 获取购物车工具包
     * @return
     */
    public CartKit getCartKit() {
        if(cartKit == null){
            cartKit = new CartKit(httpRequest);
        }
        return cartKit;
    }

    /**
     * 获取价格保护工具包
     * @return
     */
    public InsuredKit getInsuredKit() {
        if(insuredKit == null){
            insuredKit = new InsuredKit(httpRequest);
        }
        return insuredKit;
    }
}
