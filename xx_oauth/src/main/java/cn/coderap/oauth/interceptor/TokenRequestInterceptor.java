package cn.coderap.oauth.interceptor;

import cn.coderap.oauth.util.AdminToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * feign请求调用前进行拦截
 */
@Configuration
public class TokenRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        //创建管理员令牌
        String token = AdminToken.create();
        //放入Feign请求头中
        template.header("Authorization","bearer " + token);
    }
}
