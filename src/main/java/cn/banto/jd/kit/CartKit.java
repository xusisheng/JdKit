package cn.banto.jd.kit;

import cn.banto.jd.exception.JdException;
import cn.banto.jd.model.CartGoods;
import cn.banto.jd.utils.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 购物车工具包
 * @author BANTO
 */
public class CartKit {

    private Logger logger = LoggerFactory.getLogger(CartKit.class);

    private HttpRequest httpRequest;

    public CartKit(HttpRequest httpRequest){
        this.httpRequest = httpRequest;
    }

    /**
     * 添加到购物车
     * @param shopUrl
     * @return
     */
    public boolean add(String shopUrl){
        String content = httpRequest.get(shopUrl);

        Document document = Jsoup.parse(content);
        Elements element = document.select("#InitCartUrl");
        if(element.size() > 0){
            String toCartUrl = "https:"+ element.attr("href");
            content = httpRequest.get(toCartUrl);

            return content.indexOf("商品已成功加入购物车") != -1;
        }

        return false;
    }

    /**
     * 选择或者取消选中的商品
     * @param goods
     * @return
     */
    public boolean selectOrCancel(CartGoods goods){
        String url = "https://cart.jd.com/"+ (goods.isSeleced() ? "cancelItem" : "selectItem") +".action";
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("pid", goods.getPid());
        param.put("ptype", goods.getPtype());
        param.put("packId", goods.getPackId());
        param.put("targetId", goods.getTargetId());
        param.put("promoID", goods.getTargetId());
        param.put("t", goods.getT());
        if("1".equals(goods.getManFanZeng()) || "4".equals(goods.getPtype())){
            param.put("venderId", goods.getVenderId());
            param.put("manFanZeng", "1");
        } else {
            param.put("locationId", "1-72-2819");
        }

        String content = httpRequest.post(url, param, "https://cart.jd.com/cart.action");
        JSONObject json = JSON.parseObject(content);

        return json.containsKey("aedg");
    }

    /**
     * 获取所有商品
     */
    public List<CartGoods> getGoodsList(){
        ArrayList<CartGoods> goodsList = new ArrayList<CartGoods>();

        String url = "https://cart.jd.com/cart.action";
        String content = httpRequest.get(url);
        Document document = Jsoup.parse(content);
        String t = document.select(".container").attr("t");
        //获取所有商品
        Elements elements = document.select(".item-item");
        for (Element element : elements) {
            Elements inputElements = element.select(".cart-checkbox > input");
            Elements infoElements = element.select(".goods-item");
            boolean isSelected = element.is(".item-selected");

            if(inputElements.size() > 0 && infoElements.size() > 0){
                //获取商品基本信息
                Element hrefElement = infoElements.get(0).child(0).child(0);
                Element imageElement = hrefElement.child(0);
                String name = imageElement.attr("alt");
                String thumb = "https:"+ imageElement.attr("src");
                String goodsUrl = "https:"+ hrefElement.attr("href");
                String count = element.select(".itxt").val();
                //获取提交订单时的必要数据
                Element input = inputElements.get(0);
                String manFanZeng = input.attr("manFanZeng");
                String[] data = input.val().split("_");
                String targetId = data.length == 3 ? data[2] : "0";
                String outSkus = document.select("#outSkus").val();
                String venderId = "";
                if("1".equals(manFanZeng) || "4".equals(data[1])){
                    venderId = input.parents().select(".cart-tbody").attr("id");
                    venderId = venderId.substring(venderId.lastIndexOf('_')+1);
                }

                CartGoods goods = new CartGoods();
                goods.setName(name);
                goods.setUrl(goodsUrl);
                goods.setThumb(thumb);
                goods.setCount(Integer.parseInt(count));
                goods.setSeleced(isSelected);
                goods.setManFanZeng(manFanZeng);
                goods.setPid(data[0]);
                goods.setPtype(data[1]);
                goods.setTargetId(targetId);
                goods.setOutSkus(outSkus);
                goods.setVenderId(venderId);
                goods.setPackId("0");
                goods.setT(t);
                goodsList.add(goods);
            }
        }

        return goodsList;
    }

    /**
     * 下单
     */
    public void toBalance(){
        String url = "https://cart.jd.com/gotoOrder.action";
        String content = httpRequest.get(url, "https://cart.jd.com/cart.action");
        if(content.indexOf("订单结算页") == -1){
            logger.error("前往结算页失败: {}", content);
            throw new JdException("未能前往结算页");
        }

        System.out.println(content);
    }

}
