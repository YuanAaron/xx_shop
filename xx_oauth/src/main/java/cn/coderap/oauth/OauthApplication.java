package cn.coderap.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.coderap.user.feign")
public class OauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class,args);
    }


    @Bean(name = "restTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
