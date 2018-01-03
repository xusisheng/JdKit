package cn.banto.jd.kit;

import cn.banto.jd.model.Consignee;
import cn.banto.jd.utils.HttpRequest;
import cn.banto.jd.utils.JsonUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 账户工具包
 * @author BANTO
 */
public class AccountKit extends AbstractKit {

    private Logger logger = LoggerFactory.getLogger(AccountKit.class);

    public AccountKit(HttpRequest httpRequest){
        super(httpRequest);
    }

    /**
     * 收获地址列表
     * @return
     */
    public List<Consignee> getConsigneeList(){
        ArrayList<Consignee> consignees = new ArrayList<Consignee>();
        String referer = "https://cart.jd.com/cart?rd=0.3573418250588545";
        String url = "https://trade.jd.com/shopping/dynamic/consignee/getConsigneeList.action?charset=UTF-8&callback=jQuery4078434";
        String content = httpRequest.get(url, referer);

        JSONArray json = JSON.parseArray(JsonUtil.jsonpToJson(content));
        Iterator iterator = json.iterator();
        while (iterator.hasNext()) {
            JSONObject info = (JSONObject) iterator.next();
            Consignee consignee = JSON.toJavaObject(info, Consignee.class);
            consignees.add(consignee);
        }

        return consignees;
    }
}
