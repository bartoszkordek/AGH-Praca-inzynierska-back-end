package com.healthy.gym.user.service;

import com.healthy.gym.user.data.entity.RegistrationToken;
import com.healthy.gym.user.data.entity.ResetPasswordToken;
import com.healthy.gym.user.data.entity.UserEntity;
import com.healthy.gym.user.data.repository.RegistrationTokenDAO;
import com.healthy.gym.user.data.repository.ResetPasswordTokenDAO;
import com.healthy.gym.user.exceptions.token.ExpiredTokenException;
import com.healthy.gym.user.exceptions.token.InvalidTokenException;
import com.healthy.gym.user.shared.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@SpringBootTest
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private RegistrationTokenDAO registrationTokenDAO;

    @MockBean
    private ResetPasswordTokenDAO resetPasswordTokenDAO;

    private String token;
    private UserEntity janKowalskiEntity;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        janKowalskiEntity = new UserEntity(
                "Jan",
                "Kowalski",
                "jan.kowalski@test.com",
                "666 777 888",
                bCryptPasswordEncoder.encode("password1234"),
                UUID.randomUUID().toString(),
                true
        );

        userDTO = new UserDTO(
                janKowalskiEntity.getUserId(),
                janKowalskiEntity.getName(),
                janKowalskiEntity.getSurname(),
                janKowalskiEntity.getEmail(),
                janKowalskiEntity.getPhoneNumber(),
                "password1234",
                janKowalskiEntity.getEncryptedPassword()
        );

        token = UUID.randomUUID().toString();
    }

    @Nested
    class WhenCreateResetPasswordTokenIsCalled {

        @Test
        void shouldThrowExceptionWhenNullEntityProvided() {
            assertThatThrownBy(
                    () -> tokenService.createResetPasswordToken(null)
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldThrowExceptionWhenInvalidEntityProvided() {
            assertThatThrownBy(
                    () -> tokenService.createResetPasswordToken(new UserEntity())
            ).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void shouldRestPasswordTokenWhenValidEntityProvided() {
            janKowalskiEntity.setId(1L);
            when(resetPasswordTokenDAO.save(any())).thenReturn(new ResetPasswordToken(token, janKowalskiEntity));

            ResetPasswordToken resetPasswordToken = tokenService.createResetPasswordToken(janKowalskiEntity);

            assertThat(resetPasswordToken.getToken()).isEqualTo(token);
            assertThat(resetPasswordToken.getUserEntity()).isEqualTo(janKowalskiEntity);
        }
    }

    @Nested
    class WhenCreateRegistrationTokenIsCalled {
        private RegistrationToken registrationToken;

        @BeforeEach
        void setUp() {
            when(registrationTokenDAO.save(any(RegistrationToken.class)))
                    .thenReturn(new RegistrationToken(token, janKowalskiEntity));

            registrationToken = tokenService.createRegistrationToken(userDTO, token);
        }

        @Test
        void shouldReturnRegistrationTokenWithProperToken() {
            assertThat(registrationToken.getToken()).isEqualTo(token);
        }

        @Test
        void shouldReturnRegistrationTokenWithProperUser() {
            assertThat(registrationToken.getUserEntity()).isEqualTo(janKowalskiEntity);
        }
    }

    @Nested
    class WhenVerifyResetPasswordTokenIsCalled {

        @Test
        void name() {
            assertTrue(true);
        }
    }

    @Nested
    class WhenVerifyRegistrationTokenIsCalled {

        @Test
        void shouldThrowInvalidTokenExceptionWhenProvidedInvalidToken() {
            when(registrationTokenDAO.findByToken(anyString())).thenReturn(null);
            assertThatThrownBy(
                    () -> tokenService.verifyRegistrationToken(anyString())
            ).isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void shouldThrowExpiredTokenExceptionWhenProvidedExpiredToken() {
            RegistrationToken expiredToken = new RegistrationToken();
            expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));

            when(registrationTokenDAO.findByToken(anyString())).thenReturn(expiredToken);
            assertThatThrownBy(
                    () -> tokenService.verifyRegistrationToken(anyString())
            ).isInstanceOf(ExpiredTokenException.class);
        }

        @Test
        void shouldThrowIllegalStateExceptionWhenNoUserIsAssociatedWithRegisterToken() {
            RegistrationToken registrationToken = spy(RegistrationToken.class);
            registrationToken.setExpiryDate(LocalDateTime.now().plusHours(2));

            when(registrationTokenDAO.findByToken(anyString())).thenReturn(registrationToken);
            when(registrationToken.getUserEntity()).thenReturn(null);

            assertThatThrownBy(
                    () -> tokenService.verifyRegistrationToken(anyString())
            ).isInstanceOf(IllegalStateException.class);
        }
    }

}