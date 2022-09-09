package cn.coderap.goods.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@EnableCaching //开启缓存功能
@Configuration
public class CacheConfig {

    /**
     * Spring-boot-starter-cache的自动配置：CacheAutoConfiguration -> org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration
     * -> 已经自动配置了缓存管理器RedisCacheManager -> determineConfiguration(java.lang.ClassLoader) -> CacheManager -> Cache(RedisCache，负责缓存的读写）
     *
     * 1、从org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration#determineConfiguration(java.lang.ClassLoader)可知，
     * 如果org.springframework.data.redis.cache.RedisCacheConfiguration存在，那么就使用该配置；
     * 如果不存在就使用默认配置org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig()，同时将application.yml中的配置整合进来。
     *
     * 2、CacheProperties类上只有@ConfigurationProperties(prefix = "spring.cache")注解，并没有注入到Spring容器中，如果想将其注入，可以在CacheConfig类上加上 @EnableConfigurationProperties(CacheProperties.class)。
     * 但其实在CacheAutoConfiguration中已经这样做了，因此这里没必要再做。如果是自己写的类，可以直接在CacheProperties类上加上@Configuration注解，因此我对@EnableConfigurationProperties(CacheProperties.class)
     * 理解就是相当于给CacheProperties加上@Configuration。
     */
    @Bean
    RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                //缓存中的value保存为json格式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));
        
        // 将application.yml中的所有配置整合进来
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixKeysWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }
}
