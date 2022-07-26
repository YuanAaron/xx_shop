package cn.coderap.entity;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

/**
 * 用于微服务之间的认证
 */
public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        //将用户请求中的所有请求头放入RequestTemplate的请求头中
        //1.获取到用户请求中所有的请求头
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        Enumeration<String> headerNames = requestAttributes.getRequest().getHeaderNames();
        //2.放入
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = requestAttributes.getRequest().getHeader(key);
            template.header(key, value);
        }
    }
}
