package cn.banto.jd.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author BANTO
 */
public class JsonUtil {

    private final static Pattern JSONP_PATTERN = Pattern.compile("^[A-Za-z0-9]*\\(([\\s\\S]+)\\)[;]?$");

    /**
     * jsonp转为json
     * @param jsonp
     * @return
     */
    public static String jsonpToJson(String jsonp){
        Matcher matcher = JSONP_PATTERN.matcher(jsonp);
        if(matcher.find()){
            return matcher.group(1);
        }

        return jsonp;
    }
}
