package com.lunar.common.redis.config;

import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.consts.Consts;
import com.lunar.common.redis.utils.RedisUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.lunar.common.core.consts.CacheConsts;
import com.lunar.common.core.consts.Consts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * redis配置
 *
 * @author szx
 */
@Slf4j
@Configuration
@EnableCaching
public class BaseRedisConfig extends CachingConfigurerSupport {

    @Value("${spring.redis.database:0}")
    protected Integer database;
    @Value("${spring.redis.host:127.0.0.1}")
    protected String host;
    @Value("${spring.redis.port:6379}")
    protected Integer port;
    @Value("${spring.redis.password:}")
    protected String password;
    @Value("${spring.redis.lettuce.pool.max-active:100}")
    protected Integer maxActive;
    @Value("${spring.redis.lettuce.pool.max-wait:1000}")
    protected Integer maxWait;
    @Value("${spring.redis.lettuce.pool.max-idle:50}")
    protected Integer maxIdle;
    @Value("${spring.redis.lettuce.pool.min-idle:0}")
    protected Integer minIdle;
    @Value("${spring.redis.lettuce.shutdown-timeout:0}")
    protected Integer timeout;

    /**
     * 缓存库配置
     */
    @Value("${spring.redis.database:0}")
    private Integer cacheDatabase;

//    @Autowired
//    private RedisTemplate redisTemplate;

    @Bean
    @SuppressWarnings({"InstantiationOfUtilityClass", "rawtypes"})
    public RedisUtil redisUtil(RedisTemplate redisTemplate) {
        RedisUtil.init(redisTemplate);
        return new RedisUtil();
    }

    @Bean(name = "redisTemplate")
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        return getTemplate(redisConnectionFactory());
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String redisHost = "redis://" + host + ":" + port;
        if (StringUtils.isBlank(password)) {
            config.useSingleServer().setAddress(redisHost).setDatabase(database);
        } else {
            config.useSingleServer().setAddress(redisHost).setPassword(password).setDatabase(database);
        }
        return Redisson.create(config);
    }

    /**
     * 在使用@Cacheable时，如果不指定key，则使用这个默认的key生成器生成的key
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    /**
     * 声明缓存管理器
     * <p>
     * 1. entryTtl: 定义默认的cache time-to-live. 2. disableCachingNullValues: 禁止缓存Null对象. 视需求而定. 3. computePrefixWith:
     * 此处定义了cache key的前缀, 避免公司不同项目之间的key名称冲突. 4. serializeKeysWith, serializeValuesWith: 定义key和value的序列化协议, 同时的hash
     * key和hash value也被定义.
     */
    @Override
    @Bean
    @Primary
    public CacheManager cacheManager() {
        // 缓存配置
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
            .computePrefixWith(cacheName -> Consts.APP_NAME.concat(":cache:").concat(cacheName).concat(":"))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer()))
            .entryTtl(Duration.ofDays(CacheConsts.CACHE_TIMEOUT_DAY));

        RedisCacheManager cacheManager = RedisCacheManager.builder(cacheRedisConnectionFactory())
            .cacheDefaults(cacheConfiguration).transactionAware().build();
        cacheManager.afterPropertiesSet();
        log.info("RedisCacheManager config success");

        return cacheManager;
    }

    /**
     * 缓存库
     */
    private RedisConnectionFactory cacheRedisConnectionFactory() {
        return connectionFactory(maxActive, maxIdle, minIdle, maxWait, host, password, timeout, port, cacheDatabase);
    }

    protected RedisConnectionFactory redisConnectionFactory() {
        return connectionFactory(maxActive, maxIdle, minIdle, maxWait, host, password, timeout, port, database);
    }

    @SuppressWarnings("rawtypes")
    protected RedisConnectionFactory connectionFactory(Integer maxActive,
                                                       Integer maxIdle,
                                                       Integer minIdle,
                                                       Integer maxWait,
                                                       String host,
                                                       String password,
                                                       Integer timeout,
                                                       Integer port,
                                                       Integer database) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setDatabase(database);
        if (StringUtils.isNoneBlank(password)) {
            redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        }

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWait(Duration.ofMillis(maxWait));
        LettuceClientConfiguration lettucePoolingConfig =
            LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .shutdownTimeout(Duration.ofMillis(timeout))
                .build();
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration,
                                                                                  lettucePoolingConfig);
        connectionFactory.afterPropertiesSet();

        return connectionFactory;
    }

    private RedisTemplate<String, Object> getTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // key string序列化
        template.setKeySerializer(RedisSerializer.string());
        // hash field string序列化
        template.setHashKeySerializer(RedisSerializer.string());

        // 默认jackson序列化
//        template.setValueSerializer(RedisSerializer.json());
        // value jackson序列化
        template.setValueSerializer(jackson2JsonRedisSerializer());
        // hash value jackson序列化
        template.setHashValueSerializer(jackson2JsonRedisSerializer());

        template.afterPropertiesSet();

        return template;
    }

    /**
     * 自定义value jackson序列化方式
     */
    protected RedisSerializer<Object> jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
            new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
                                           JsonTypeInfo.As.PROPERTY);
        // 反序列化时忽略多余字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer;
    }

}