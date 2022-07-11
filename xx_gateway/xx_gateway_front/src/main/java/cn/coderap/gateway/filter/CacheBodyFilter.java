package cn.coderap.gateway.filter;

import io.netty.buffer.UnpooledByteBufAllocator;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 对于Spring Cloud Gateway中的POST请求的body获取比较复杂，因为
 * request body只能读取一次,如果读取以后不封装回去,则会造成后面的服务无法读取body数据
 */
@Component
public class CacheBodyFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String method = serverHttpRequest.getMethodValue();
        if(!("GET".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method))) {
            ServerRequest serverRequest = new DefaultServerRequest(exchange);
            Mono<String> bodyToMono = serverRequest.bodyToMono(String.class);
            return bodyToMono.flatMap(body -> {
                exchange.getAttributes().put("cachedRequestBody", body);
                ServerHttpRequest newRequest = new ServerHttpRequestDecorator(serverHttpRequest) {
                    @Override
                    public HttpHeaders getHeaders() {
                        HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.putAll(super.getHeaders());
                        httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                        return httpHeaders;
                    }

                    @Override
                    public Flux<DataBuffer> getBody() {
                        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
                        DataBuffer bodyDataBuffer = nettyDataBufferFactory.wrap(body.getBytes());
                        return Flux.just(bodyDataBuffer);
                    }
                };
                return chain.filter(exchange.mutate().request(newRequest).build());
            });
        }
        return chain.filter(exchange);
    }

    /**
     * 数据越小，优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return -99;
    }
}
