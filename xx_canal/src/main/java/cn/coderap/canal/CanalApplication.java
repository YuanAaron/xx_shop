package cn.coderap.canal;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCanalClient
@SpringBootApplication
public class CanalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class, args);
    }
}
