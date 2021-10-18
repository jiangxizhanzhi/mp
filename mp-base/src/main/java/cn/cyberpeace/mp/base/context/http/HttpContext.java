package cn.cyberpeace.mp.base.context.http;

import cn.cyberpeace.mp.base.context.BaseSysContext;

/**
 * @Description
 * @Author maojun
 * @Date 2021/8/31
 */
public class HttpContext extends BaseSysContext implements IHttpContext {
    private String requestId;

    public HttpContext(String requestId) {
        super();
        this.requestId = requestId;
    }

    /**
     * 受限于请求中request的唯一性，不保证全局唯一
     * @return
     */
    @Override
    public String getCallChainId() {
        return this.requestId != null ? this.requestId : super.getCallChainId();
    }
}