package com.healthy.gym.account.controller.accountController.unitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.account.component.token.TokenManager;
import com.healthy.gym.account.configuration.tests.TestCountry;
import com.healthy.gym.account.controller.AccountController;
import com.healthy.gym.account.exception.IdenticalOldAndNewPasswordException;
import com.healthy.gym.account.exception.OldPasswordDoesNotMatchException;
import com.healthy.gym.account.service.AccountService;
import com.healthy.gym.account.shared.UserDTO;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.validation.BindException;

import java.net.URI;
import java.util.*;

import static com.healthy.gym.account.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.account.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class WhenChangePasswordTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenManager tokenManager;

    @MockBean
    private AccountService accountService;

    private String userToken;
    private String userId;
    private ObjectMapper objectMapper;
    private Map<String, String> requestMap;

    private Date setTokenExpirationTime() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = tokenManager.getExpirationTimeInMillis();
        return new Date(currentTime + expirationTime);
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        userId = UUID.randomUUID().toString();
        userToken = tokenManager.getTokenPrefix() + " " + Jwts.builder()
                .setSubject(userId)
                .claim("roles", List.of("ROLE_USER"))
                .setExpiration(setTokenExpirationTime())
                .signWith(
                        tokenManager.getSignatureAlgorithm(),
                        tokenManager.getSigningKey()
                )
                .compact();
        requestMap = new HashMap<>();
        requestMap.put("oldPassword", "test1234");
        requestMap.put("newPassword", "test12345");
        requestMap.put("matchingNewPassword", "test12345");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldRejectRequestWhenIdDoesNotMatchUserIdInToken(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        String invalidId = UUID.randomUUID().toString();

        URI uri = new URI("/changePassword/" + invalidId);

        String requestBody = objectMapper.writeValueAsString(requestMap);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("exception.access.denied");
        when(accountService.changePassword(invalidId, "test1234", "test12345"))
                .thenReturn(new UserDTO());

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
    void shouldChangePasswordAndReturnProperMessage(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/changePassword/" + userId);

        String requestBody = objectMapper.writeValueAsString(requestMap);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("password.change.success");
        when(accountService.changePassword(userId, "test1234", "test12345"))
                .thenReturn(new UserDTO());

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(is(expectedMessage)));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowExceptionWhenUserNotFound(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/changePassword/" + userId);
        String requestBody = objectMapper.writeValueAsString(requestMap);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("exception.account.not.found");
        doThrow(UsernameNotFoundException.class).when(accountService)
                .changePassword(userId, "test1234", "test12345");

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
    void shouldThrowExceptionWhenOldPasswordIdenticalToNewPassword(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/changePassword/" + userId);

        requestMap.put("oldPassword", "test12345");
        String requestBody = objectMapper.writeValueAsString(requestMap);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("password.exception.old.identical.with.new.password");
        doThrow(IdenticalOldAndNewPasswordException.class).when(accountService)
                .changePassword(userId, "test12345", "test12345");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getCause())
                                .isInstanceOf(IdenticalOldAndNewPasswordException.class)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowExceptionWhenOldPasswordDoesNotMatchNewPassword(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/changePassword/" + userId);
        String requestBody = objectMapper.writeValueAsString(requestMap);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("password.exception.old.password.does.not.match");
        doThrow(OldPasswordDoesNotMatchException.class).when(accountService)
                .changePassword(userId, "test1234", "test12345");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getCause())
                                .isInstanceOf(OldPasswordDoesNotMatchException.class)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowExceptionWhenInvalidDataProvided(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/changePassword/" + userId);

        requestMap.put("oldPassword", "test123");
        String requestBody = objectMapper.writeValueAsString(requestMap);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("field.password.failure");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString(expectedMessage)))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getCause())
                                .isInstanceOf(BindException.class)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowExceptionWhenErrorOccurs(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        URI uri = new URI("/changePassword/" + userId);
        String requestBody = objectMapper.writeValueAsString(requestMap);

        RequestBuilder request = MockMvcRequestBuilders
                .put(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("request.failure");
        doThrow(IllegalStateException.class).when(accountService)
                .changePassword(userId, "test1234", "test12345");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(result.getResolvedException().getCause())
                                .isInstanceOf(IllegalStateException.class)
                );
    }
}