package cn.coderap.goods.config;

import cn.coderap.util.IdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdConfig {

    @Value("${workerId}")
    private Integer workerId;
    @Value("${datacenterId}")
    private Integer datacenterId;

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(workerId,datacenterId);
    }
}
