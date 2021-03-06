package com.healthy.gym.auth.component;

import com.healthy.gym.auth.configuration.tests.TestCountry;
import com.healthy.gym.auth.data.document.RegistrationTokenDocument;
import com.healthy.gym.auth.data.document.ResetPasswordTokenDocument;
import com.healthy.gym.auth.data.document.UserDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.healthy.gym.auth.configuration.tests.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.auth.configuration.tests.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MailMessageManagerTest {

    @Autowired
    private MailMessageManager mailMessageManager;

    @Nested
    class WhenGetConfirmRegistrationMessageSubjectIsCalled {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldContainsConfirmationMessage(TestCountry country) {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);
            LocaleContextHolder.setLocale(testedLocale);

            String messageSubject = messages.get("mail.registration.confirmation.subject");

            String actualSubject = mailMessageManager.getConfirmRegistrationMessageSubject();
            assertThat(actualSubject).contains(messageSubject);
        }
    }

    @Nested
    class WhenGetConfirmRegistrationMessageTextIsCalled {

        private String token;
        private RegistrationTokenDocument registrationToken;

        @BeforeEach
        void setUp() {
            token = UUID.randomUUID().toString();
            registrationToken = new RegistrationTokenDocument(token, new UserDocument());
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldContainsConfirmationMessage(TestCountry country) {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);
            LocaleContextHolder.setLocale(testedLocale);

            String confirmationMessage = messages.get("mail.registration.confirmation.message");

            String actualMessage = mailMessageManager.getConfirmRegistrationMessageText(registrationToken);
            assertThat(actualMessage).contains(confirmationMessage);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldContainsExpiresAt(TestCountry country) {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);
            LocaleContextHolder.setLocale(testedLocale);

            String linkExpiresAt = messages.get("mail.registration.confirmation.expiration");

            String actualMessage = mailMessageManager.getConfirmRegistrationMessageText(registrationToken);
            assertThat(actualMessage).contains(linkExpiresAt);
        }

        @Test
        void shouldContainsConfirmationUrlWithToken() {
            String baseUrl = "http://localhost:3000/AGH-Praca-inzynierska-front-end";
            String confirmationUrl = "/confirmRegistration?token=";
            String expectedUrl = baseUrl + confirmationUrl + token;

            String actualMessage = mailMessageManager.getConfirmRegistrationMessageText(registrationToken);
            assertThat(actualMessage).contains(expectedUrl);
        }

        @Test
        void shouldThrowExceptionWhenInvalidRegistrationTokenProvided() {
            assertThatThrownBy(
                    () -> mailMessageManager.getConfirmRegistrationMessageText(new RegistrationTokenDocument())
            ).isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class WhenGetResetPasswordMessageSubjectIsCalled {

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldContainsConfirmationMessage(TestCountry country) {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);
            LocaleContextHolder.setLocale(testedLocale);

            String messageSubject = messages.get("mail.reset.password.subject");

            String actualSubject = mailMessageManager.getResetPasswordMessageSubject();
            assertThat(actualSubject).contains(messageSubject);
        }
    }

    @Nested
    class WhenGetResetPasswordMessageTextIsCalled {

        private String token;
        private ResetPasswordTokenDocument resetPasswordToken;

        @BeforeEach
        void setUp() {
            token = UUID.randomUUID().toString();
            resetPasswordToken = new ResetPasswordTokenDocument(token, new UserDocument());
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldContainsConfirmationMessage(TestCountry country) {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);
            LocaleContextHolder.setLocale(testedLocale);

            String confirmationMessage = messages.get("mail.reset.password.message");

            String actualMessage = mailMessageManager.getResetPasswordMessageText(resetPasswordToken);
            assertThat(actualMessage).contains(confirmationMessage);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldContainsExpiresAt(TestCountry country) {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);
            LocaleContextHolder.setLocale(testedLocale);

            String linkExpiresAt = messages.get("mail.reset.password.expiration");

            String actualMessage = mailMessageManager.getResetPasswordMessageText(resetPasswordToken);
            assertThat(actualMessage).contains(linkExpiresAt);
        }

        @Test
        void shouldContainsResetPasswordUrlWithToken() {
            String baseUrl = "http://localhost:3000/AGH-Praca-inzynierska-front-end";
            String confirmationUrl = "/confirmNewPassword?token=";
            String expectedUrl = baseUrl + confirmationUrl + token;

            String actualMessage = mailMessageManager.getResetPasswordMessageText(resetPasswordToken);
            assertThat(actualMessage).contains(expectedUrl);
        }


        @Test
        void shouldThrowExceptionWhenInvalidResetPasswordTokenProvided() {
            assertThatThrownBy(
                    () -> mailMessageManager.getResetPasswordMessageText(new ResetPasswordTokenDocument())
            ).isInstanceOf(IllegalStateException.class);
        }
    }

}