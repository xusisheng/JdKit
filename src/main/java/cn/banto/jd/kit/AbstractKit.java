package cn.banto.jd.kit;

import cn.banto.jd.utils.HttpRequest;

/**
 * 抽象工具包
 * @author BANTO
 */
public abstract class AbstractKit {

    protected HttpRequest httpRequest;

    public AbstractKit(HttpRequest httpRequest){
        this.httpRequest = httpRequest;
    }
}
