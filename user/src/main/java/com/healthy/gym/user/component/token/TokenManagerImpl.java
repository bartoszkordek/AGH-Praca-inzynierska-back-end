package com.healthy.gym.user.component.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TokenManagerImpl implements TokenManager {

    private final Environment environment;

    @Autowired
    public TokenManagerImpl(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String getSigningKey() {
        return environment.getRequiredProperty("token.secret");
    }

    @Override
    public long getExpirationTimeInMillis() {
        String expirationTime = environment.getRequiredProperty("token.expiration-time");
        return Long.parseLong(expirationTime);
    }

    @Override
    public String getHttpHeaderName() {
        return environment.getRequiredProperty("authorization.token.header.name");
    }

    @Override
    public String getTokenPrefix() {
        return environment.getRequiredProperty("authorization.token.header.prefix");
    }
}
