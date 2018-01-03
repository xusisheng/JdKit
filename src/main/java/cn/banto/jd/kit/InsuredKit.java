package cn.banto.jd.kit;

import cn.banto.jd.model.InsuredGoods;
import cn.banto.jd.utils.HttpRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 价格保护工具包
 * @author BANTO
 */
public class InsuredKit extends AbstractKit {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public InsuredKit(HttpRequest httpRequest) {
        super(httpRequest);
    }


    public List<InsuredGoods> getGoodsList() {
        ArrayList<InsuredGoods> goodsList = new ArrayList<InsuredGoods>();
        try {
            String url = "https://sitepp-fm.jd.com/";
            String content = httpRequest.get(url);
            Document document = Jsoup.parse(content);
            Elements elements = document.select("#datas > tbody");
            for (Element element : elements) {
                Date buyTime = sdf.parse(element.select(".dealtime").text());
                Elements orderGoods = element.select(".tr-bd");
                for (Element goodsInfo : orderGoods) {
                    String thumb = "https:"+ goodsInfo.select(".img-box > img").attr("src");
                    String name = goodsInfo.select(".p-name > a").text();
                    String goodsUrl = "https:"+ goodsInfo.select(".p-name > a").attr("href");
                    String count = goodsInfo.select(".goods-number").text().trim().substring(1);
                    String price = goodsInfo.select(".goods-repair > strong").text().substring(1);
                    Element elem = goodsInfo.select(".ajaxFecthState").first();
                    String orderId = elem.attr("orderId");
                    String skuid = elem.attr("skuid");
                    String sequence = elem.attr("sequence");

                    InsuredGoods insuredGoods = new InsuredGoods();
                    insuredGoods.setBuyTime(buyTime);
                    insuredGoods.setName(name);
                    insuredGoods.setThumb(thumb);
                    insuredGoods.setUrl(goodsUrl);
                    insuredGoods.setCount(Integer.parseInt(count));
                    insuredGoods.setPrice(Float.parseFloat(price));
                    insuredGoods.setOrderId(Long.parseLong(orderId));
                    insuredGoods.setSkuid(Long.parseLong(skuid));
                    insuredGoods.setSequence(Integer.parseInt(sequence));
                    goodsList.add(insuredGoods);
                }
            }
        } catch (Exception e) {

        }

        return goodsList;
    }


}
