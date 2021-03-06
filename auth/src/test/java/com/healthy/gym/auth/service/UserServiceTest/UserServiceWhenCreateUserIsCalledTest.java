package com.healthy.gym.auth.service.UserServiceTest;

import com.healthy.gym.auth.data.document.NotificationDocument;
import com.healthy.gym.auth.data.document.UserDocument;
import com.healthy.gym.auth.data.repository.mongo.UserDAO;
import com.healthy.gym.auth.data.repository.mongo.UserPrivacyDAO;
import com.healthy.gym.auth.enums.GymRole;
import com.healthy.gym.auth.service.NotificationService;
import com.healthy.gym.auth.service.UserService;
import com.healthy.gym.auth.service.UserServiceImpl;
import com.healthy.gym.auth.shared.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceWhenCreateUserIsCalledTest {

    private UserDTO savedUserDTO;
    private UserDTO andrzejNowakDTO;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    void setUp() {

        UserDAO userDAO = mock(UserDAO.class);
        UserPrivacyDAO userPrivacyDAO = mock(UserPrivacyDAO.class);
        NotificationService notificationService = mock(NotificationService.class);
        bCryptPasswordEncoder = new BCryptPasswordEncoder();

        UserService userService = new UserServiceImpl(
                userDAO,
                userPrivacyDAO,
                bCryptPasswordEncoder,
                null,
                null,
                notificationService
        );

        andrzejNowakDTO = new UserDTO(
                null,
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                "password4576",
                null
        );
        Set<GymRole> userRolesDTO = new HashSet<>();
        userRolesDTO.add(GymRole.USER);
        andrzejNowakDTO.setGymRoles(userRolesDTO);

        UserDocument andrzejNowak = new UserDocument(
                "Andrzej",
                "Nowak",
                "andrzej.nowak@test.com",
                "676 777 888",
                bCryptPasswordEncoder.encode("password4576"),
                UUID.randomUUID().toString()
        );
        Set<GymRole> userRoles = new HashSet<>();
        userRoles.add(GymRole.USER);
        andrzejNowak.setGymRoles(userRoles);

        when(userDAO.save(Mockito.any(UserDocument.class)))
                .thenReturn(andrzejNowak);
        when(userPrivacyDAO.save(any())).thenReturn(null);
        when(notificationService.createWelcomeNotification(any())).thenReturn(new NotificationDocument());

        savedUserDTO = userService.createUser(andrzejNowakDTO);
    }

    @Test
    void shouldReturnUserDTOWithUserID() {
        assertThat(savedUserDTO.getUserId())
                .isNotNull()
                .isNotEmpty()
                .isInstanceOf(String.class)
                .hasSize(36)
                .matches(uuid -> {
                    try {
                        UUID.fromString(uuid);
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                    return true;
                });
    }

    @Test
    void shouldReturnUserDTOWithProperName() {
        assertThat(savedUserDTO.getName()).isEqualTo(andrzejNowakDTO.getName());
    }

    @Test
    void shouldReturnUserDTOWithProperSurname() {
        assertThat(savedUserDTO.getSurname()).isEqualTo(andrzejNowakDTO.getSurname());
    }

    @Test
    void shouldReturnUserDTOWithProperEmail() {
        assertThat(savedUserDTO.getEmail()).isEqualTo(andrzejNowakDTO.getEmail());
    }

    @Test
    void shouldReturnUserDTOWithProperPhoneNumber() {
        assertThat(savedUserDTO.getPhoneNumber()).isEqualTo(andrzejNowakDTO.getPhoneNumber());
    }

    @Test
    void shouldNotReturnUserDTOWithPlainTextPassword() {
        assertThat(savedUserDTO.getPassword()).isNull();
    }

    @Test
    void shouldReturnUserDTOWithMatchingEncodedPassword() {
        assertThat(savedUserDTO.getEncryptedPassword())
                .isNotNull()
                .isNotEmpty()
                .isInstanceOf(String.class)
                .matches(password -> bCryptPasswordEncoder.matches(andrzejNowakDTO.getPassword(), password));
    }
}
