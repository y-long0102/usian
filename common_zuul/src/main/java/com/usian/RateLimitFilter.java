package com.usian;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.usian.utils.Result;
import com.usian.utils.JsonUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

@Component
public class RateLimitFilter extends ZuulFilter {
    // 创建令牌桶
    //RateLimiter.create(1)1: 是每秒生成令牌的数量
    // 数值越大代表处理请求量月多，数值越小代表处理请求量越少
    private static final RateLimiter RATE_LIMIT = RateLimiter.create(1);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 限流器的优先级应为最高
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return FilterConstants.SERVLET_DETECTION_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //是否能从令牌桶中获取到令牌
        if (!RATE_LIMIT.tryAcquire()) {
            RequestContext requestContext = RequestContext.getCurrentContext();
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseBody(JsonUtils.objectToJson(
                		Result.ok("访问太多频繁，请稍后再访问！！！")));
            requestContext.getResponse().setContentType(
                			"application/json; charset=utf-8");
        }
        return null;
    }
}