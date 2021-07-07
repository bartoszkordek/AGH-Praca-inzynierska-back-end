package com.healthy.gym.account.controller.photoController.unitTest;

import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.configuration.tests.TestRoleTokenFactory;
import com.healthy.gym.account.controller.PhotoController;
import com.healthy.gym.account.exception.UserAvatarNotFoundException;
import com.healthy.gym.account.service.AccountService;
import com.healthy.gym.account.service.PhotoService;
import com.healthy.gym.account.shared.ImageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.account.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PhotoController.class)
class WhenDeleteAvatarTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private AccountService accountService; // do NOT remove - necessary to load application context

    @MockBean
    private PhotoService photoService;

    private String userToken;
    private String userId;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);
        String adminId = UUID.randomUUID().toString();
        adminToken = tokenFactory.getAdminToken(adminId);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldReturnAvatar(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/photos/" + userId + "/avatar");

        String expectedMessage = messages.get("avatar.removed");
        when(photoService.removeAvatar(userId)).thenReturn(new ImageDTO());

        RequestBuilder request = MockMvcRequestBuilders
                .delete(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(is(expectedMessage)))
                .andExpect(jsonPath("$.avatar.data").doesNotExist())
                .andExpect(jsonPath("$.avatar.format").doesNotExist())
                .andExpect(jsonPath("$.avatar").isEmpty());
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldThrowWhenAvatarNotFound(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/photos/" + userId + "/avatar");

        String expectedMessage = messages.get("avatar.not.found.exception");
        doThrow(UserAvatarNotFoundException.class).when(photoService).removeAvatar(userId);

        RequestBuilder request = MockMvcRequestBuilders
                .delete(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getCause())
                                .isInstanceOf(UserAvatarNotFoundException.class)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldThrowWhenUsernameNotFound(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/photos/" + userId + "/avatar");

        String expectedMessage = messages.get("exception.account.not.found");
        doThrow(UsernameNotFoundException.class).when(photoService).removeAvatar(userId);

        RequestBuilder request = MockMvcRequestBuilders
                .delete(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getCause())
                                .isInstanceOf(UsernameNotFoundException.class)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldAcceptRequestAndShouldThrowWhenInternalErrorHappens(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/photos/" + userId + "/avatar");

        String expectedMessage = messages.get("request.failure");
        doThrow(IllegalStateException.class).when(photoService).removeAvatar(userId);

        RequestBuilder request = MockMvcRequestBuilders
                .delete(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getCause())
                                .isInstanceOf(IllegalStateException.class)
                );
    }

    @Nested
    class ShouldRejectRequest {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidId = UUID.randomUUID().toString();
            URI uri = new URI("/photos/" + invalidId + "/avatar");

            RequestBuilder request = MockMvcRequestBuilders
                    .delete(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .contentType(MediaType.APPLICATION_JSON_VALUE);

            String expectedMessage = messages.get("exception.access.denied");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value(is(expectedMessage)))
                    .andExpect(jsonPath("$.error").value(is("Forbidden")))
                    .andExpect(jsonPath("$.status").value(403))
                    .andExpect(jsonPath("$.timestamp").exists());
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenIdDoesNotMatchUserIdInToken(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String invalidId = UUID.randomUUID().toString();
            URI uri = new URI("/photos/" + invalidId + "/avatar");

            RequestBuilder request = MockMvcRequestBuilders
                    .delete(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .header("Authorization", userToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE);

            String expectedMessage = messages.get("exception.access.denied");

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value(is(expectedMessage)))
                    .andExpect(jsonPath("$.error").value(is("Forbidden")))
                    .andExpect(jsonPath("$.status").value(403))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }
}