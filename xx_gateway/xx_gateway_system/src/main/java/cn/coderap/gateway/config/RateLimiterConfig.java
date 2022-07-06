package cn.coderap.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 限流方式：
 *    IP限流：比如每个IP每秒只能发送一次请求，多出来的请求返回429错误
 *    用户限流：请求路径中必须要包含用户的唯一表示，userId参数
 *    接口限流：请求地址的uri作为限流的key
 */
@Configuration
public class RateLimiterConfig {

    /**
     * 通过IP限流
     * @return
     */
    @Bean
    public KeyResolver ipKeyResolver(){
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                return Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
            }
        };
    }

    /**
     * 通过用户限流
     */
    //@Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("userId"));
    }

    /**
     * 通过接口限流
     */
    //@Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }
}
