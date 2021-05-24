package com.healthy.gym.account.service.accountServiceTest.unitTest;

import com.healthy.gym.account.data.document.UserDocument;
import com.healthy.gym.account.data.repository.UserDAO;
import com.healthy.gym.account.exception.UserDataNotUpdatedException;
import com.healthy.gym.account.service.AccountService;
import com.healthy.gym.account.shared.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class WhenChangeUserDataTest {
    private UserDTO andrzejNowakDTO;
    private UserDocument andrzejNowak;
    private UserDocument andrzejNowakUpdated;
    private String userId;

    @Autowired
    private AccountService accountService;

    @MockBean
    private UserDAO userDAO;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        andrzejNowak = new UserDocument(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                bCryptPasswordEncoder.encode("password4576"),
                userId
        );

        String encryptedPasswordUpdated = bCryptPasswordEncoder.encode("password45768");

        andrzejNowakDTO = new UserDTO(
                userId,
                "Krzysztof",
                "Kowalski",
                "andrzej.nowak@test.pl",
                "676 777 999",
                null,
                encryptedPasswordUpdated
        );

        andrzejNowakUpdated = new UserDocument(
                "Krzysztof",
                "Kowalski",
                "andrzej.nowak@test.pl",
                "676 777 999",
                encryptedPasswordUpdated,
                userId
        );
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotFound() {
        when(userDAO.findByUserId(userId)).thenReturn(null);
        assertThatThrownBy(
                () -> accountService.changeUserData(andrzejNowakDTO)
        ).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenUpdatedUserDoesNotMatchProvidedUser() {
        when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
        UserDocument userNotUpdated = new UserDocument(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                bCryptPasswordEncoder.encode("password4576"),
                userId
        );
        when(userDAO.save(any())).thenReturn(userNotUpdated);
        assertThatThrownBy(
                () -> accountService.changeUserData(andrzejNowakDTO)
        ).isInstanceOf(UserDataNotUpdatedException.class);
    }

    @Nested
    class ShouldThrowExceptionWhenUpdatedUserDoesNotMatchProvidedUser {
        private UserDocument userNotUpdated;

        @BeforeEach
        void setUp() {
            when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
            userNotUpdated = new UserDocument(
                    "Krzysztof",
                    "Kowalski",
                    "andrzej.nowak@test.pl",
                    "676 777 999",
                    bCryptPasswordEncoder.encode("password4576"),
                    userId
            );
            when(userDAO.save(any())).thenReturn(userNotUpdated);
        }

        @Test
        void WhenNameHasNotBeenUpdated() {
            userNotUpdated.setName("Andrzej");
            assertThatThrownBy(
                    () -> accountService.changeUserData(andrzejNowakDTO)
            ).isInstanceOf(UserDataNotUpdatedException.class);
        }

        @Test
        void WhenSurnameHasNotBeenUpdated() {
            userNotUpdated.setSurname("Nowak");
            assertThatThrownBy(
                    () -> accountService.changeUserData(andrzejNowakDTO)
            ).isInstanceOf(UserDataNotUpdatedException.class);
        }

        @Test
        void WhenEmailHasNotBeenUpdated() {
            userNotUpdated.setEmail("andrzej.nowak@test.com");
            assertThatThrownBy(
                    () -> accountService.changeUserData(andrzejNowakDTO)
            ).isInstanceOf(UserDataNotUpdatedException.class);
        }

        @Test
        void WhenPhoneHasNotBeenUpdated() {
            userNotUpdated.setPhoneNumber("676 777 888");
            assertThatThrownBy(
                    () -> accountService.changeUserData(andrzejNowakDTO)
            ).isInstanceOf(UserDataNotUpdatedException.class);
        }
    }

    @Nested
    class ShouldUpdateUserData {
        private UserDTO user;

        @BeforeEach
        void setUp() throws UserDataNotUpdatedException {
            when(userDAO.findByUserId(userId)).thenReturn(andrzejNowak);
            when(userDAO.save(any())).thenReturn(andrzejNowakUpdated);
            user = accountService.changeUserData(andrzejNowakDTO);
        }

        @Test
        void shouldReturnProperUserId() {
            assertThat(user.getUserId()).isEqualTo(userId);
        }

        @Test
        void shouldReturnProperName() {
            assertThat(user.getName()).isEqualTo(andrzejNowakDTO.getName());
        }

        @Test
        void shouldReturnProperSurname() {
            assertThat(user.getSurname()).isEqualTo(andrzejNowakDTO.getSurname());
        }

        @Test
        void shouldReturnProperEmail() {
            assertThat(user.getEmail()).isEqualTo(andrzejNowakDTO.getEmail());
        }

        @Test
        void shouldReturnProperPhoneNumber() {
            assertThat(user.getPhoneNumber()).isEqualTo(andrzejNowakDTO.getPhoneNumber());
        }
    }
}