package cn.coderap.seata.config;

import cn.coderap.seata.filter.SeataRMRequestFilter;
import cn.coderap.seata.interceptor.SeataRestInterceptor;
import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import io.seata.spring.annotation.GlobalTransactionScanner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 *  * 创建数据源
 *  * 定义全局事务管理器扫描对象
 *  * 给所有RestTemplate添加头信息防止微服务之间调用问题
 */
@Configuration
public class SeataConfig {

    public static final String SEATA_XID = "Seata-Xid";

    /***
     * 创建代理数据库
     */
    @Primary
    @Bean
    public DataSource dataSourceProxy(DataSource dataSource){
        return new DataSourceProxy(dataSource);
    }

    /***
     * 创建普通数据库的简单写法
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource(){
        return new DruidDataSource();
    }
//
//    /***
//     * 创建普通数据库
//     * @param environment
//     * @return
//     */
//    @Bean
//    public DataSource druidDataSource(Environment environment){
//        DruidDataSource dataSource = new DruidDataSource();
//        dataSource.setUrl(environment.getProperty("spring.datasource.url"));
//        try {
//            dataSource.setDriver(DriverManager.getDriver(environment.getProperty("spring.datasource.url")));
//        } catch (SQLException e) {
//            throw new RuntimeException("can't recognize dataSource Driver");
//        }
//        dataSource.setUsername(environment.getProperty("spring.datasource.username"));
//        dataSource.setPassword(environment.getProperty("spring.datasource.password"));
//        return dataSource;
//    }

    /***
     * 全局事务扫描器
     * 用来解析带有@GlobalTransactional注解的方法，然后采用AOP的机制控制事务
     * @param environment
     * @return
     */
    @Bean
    public GlobalTransactionScanner globalTransactionScanner(Environment environment){
        String applicationName = environment.getProperty("spring.application.name");
        String groupName = environment.getProperty("seata.group.name");
        if(applicationName == null){
            return new GlobalTransactionScanner(groupName == null ? "my_tx_group" : groupName);
        }else{
            return new GlobalTransactionScanner(applicationName, groupName == null ? "my_tx_group" : groupName);
        }
    }

    /***
     * 每次微服务和微服务之间相互调用
     * 要想控制全局事务，每次TM都会请求TC生成一个XID，每次执行下一个事务，也就是调用其他微服务的时候都需要将该XID传递过去
     * 所以我们可以每次请求的时候，都获取头中的XID，并将XID传递到下一个微服务
     * @param restTemplates
     * @return
     */
    @ConditionalOnBean({RestTemplate.class})
    @Bean
    public Object addSeataInterceptor(Collection<RestTemplate> restTemplates){
        restTemplates.stream().forEach(restTemplate -> {
            List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
            if(interceptors != null){
                interceptors.add(seataRestInterceptor());
            }
        });
        return new Object();
    }

    @Bean
    public SeataRMRequestFilter seataRMRequestFilter(){
        return new SeataRMRequestFilter();
    }

    @Bean
    public SeataRestInterceptor seataRestInterceptor(){
        return new SeataRestInterceptor();
    }
}
