package com.healthy.gym.user.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfiguration {

    private final Environment environment;
    private final RedisProperties redisProperties;

    @Autowired
    public RedisConfiguration(Environment environment) {
        this.environment = environment;
        this.redisProperties = new RedisProperties();
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        var redisStandaloneConfiguration = new RedisStandaloneConfiguration();

        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setDatabase(getRedisDatabase());
        redisStandaloneConfiguration.setPassword(getRedisPassword());

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    private int getRedisDatabase() {
        String database = environment.getRequiredProperty("spring.redis.database");
        return Integer.parseInt(database);
    }

    private String getRedisPassword() {
        return environment.getRequiredProperty("spring.redis.password");
    }
}
