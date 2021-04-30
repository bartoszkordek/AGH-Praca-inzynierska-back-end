package com.healthy.gym.user.component.token;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(
        inheritProperties = false,
        properties = {
                "token.secret=testSecretToken",
                "token.expiration-time=360001",
                "authorization.token.header.name=Auth",
                "authorization.token.header.prefix=Bear"
        }
)
class TokenManagerTest {

    @Autowired
    private TokenManager tokenManager;

    @Test
    void shouldReturnProperSigningKey() {
        assertThat(tokenManager.getSigningKey()).isEqualTo("testSecretToken");
    }

    @Test
    void shouldReturnProperTokenPrefix() {
        assertThat(tokenManager.getTokenPrefix()).isEqualTo("Bear");
    }

    @Test
    void shouldReturnProperAuthorizationHeader() {
        assertThat(tokenManager.getHttpHeaderName()).isEqualTo("Auth");
    }

    @Test
    void shouldReturnProperExpirationTime() {
        assertThat(tokenManager.getExpirationTimeInMillis()).isEqualTo(360001);
    }

}