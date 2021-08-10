package com.healthy.gym.trainings.controller.individual.training.user.unit.tests;

import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.controller.individual.training.UserIndividualTrainingController;
import com.healthy.gym.trainings.dto.BasicUserInfoDTO;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.dto.ParticipantsDTO;
import com.healthy.gym.trainings.exception.notfound.NoIndividualTrainingFoundException;
import com.healthy.gym.trainings.exception.notfound.UserNotFoundException;
import com.healthy.gym.trainings.service.individual.training.UserIndividualTrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.healthy.gym.trainings.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserIndividualTrainingController.class)
class GetMyAllIndividualTrainingsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private UserIndividualTrainingService userIndividualTrainingService;

    private String adminToken;
    private String employeeToken;
    private String managerToken;
    private String userId;
    private String trainerToken;
    private String userToken;
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        adminToken = tokenFactory.getAdminToken();
        employeeToken = tokenFactory.getEmployeeToken();
        managerToken = tokenFactory.getManagerToken();
        trainerToken = tokenFactory.getTrainerToken();
        userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        uri = getUri(userId);
    }

    private URI getUri(String userId) throws URISyntaxException {
        return new URI("/individual/user/" + userId);
    }


    private RequestBuilder getValidRequest(String token, Locale locale) {
        return MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", locale.toString())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON);
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldGetMyAllIndividualTrainings(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        when(userIndividualTrainingService.getMyAllTrainings(userId)).thenReturn(List.of(getIndividualTrainingDTO()));
        RequestBuilder request = getValidRequest(userToken, testedLocale);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(
                        matchAll(
                                status().isOk(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                jsonPath("$.message").doesNotHaveJsonPath()
                        )
                )
                .andExpect(
                        matchAll(
                                jsonPath("$[0].id")
                                        .value(is("74fe07a5-fb18-4006-a721-1a312dc2d398")),
                                jsonPath("$[0].title").value(is("Test training title")),
                                jsonPath("$[0].startDate").value(is("2020-10-10T16:00")),
                                jsonPath("$[0].endDate").value(is("2020-10-10T16:30")),
                                jsonPath("$[0].allDay").value(is(false)),
                                jsonPath("$[0].location").value(is("Room no 2"))
                        )
                ).andExpect(
                        matchAll(
                                jsonPath("$[0].trainers[0].name").value(is("TestName")),
                                jsonPath("$[0].trainers[0].surname").value(is("TestSurname")),
                                jsonPath("$[0].trainers[0].avatar").value(is("testAvatarUrl"))
                        )
                ).andExpect(
                        matchAll(
                                jsonPath("$[0].participants.basicList").isArray(),
                                jsonPath("$[0].participants.basicList[0].userId")
                                        .value(is("20fe07a5-fb18-4006-a721-1a312dc2d370")),
                                jsonPath("$[0].participants.basicList[0].name")
                                        .value(is("TestUserName")),
                                jsonPath("$[0].participants.basicList[0].surname")
                                        .value(is("TestUserSurname")),
                                jsonPath("$[0].participants.basicList[0].avatar")
                                        .value(is("TestAvatarUserUrl")),
                                jsonPath("$[0].participants.reserveList").isEmpty()
                        )
                );
    }

    private IndividualTrainingDTO getIndividualTrainingDTO() {
        var training = new IndividualTrainingDTO(
                "74fe07a5-fb18-4006-a721-1a312dc2d398",
                "Test training title",
                "2020-10-10T16:00",
                "2020-10-10T16:30",
                false,
                "Room no 2",
                List.of(
                        new BasicUserInfoDTO(
                                "4c9aa156-a3dd-4b25-8004-60831f82d8ae",
                                "TestName",
                                "TestSurname",
                                "testAvatarUrl"
                        )
                )
        );
        var participants = new ParticipantsDTO();
        participants.setBasicList(List.of(getTestUser()));
        training.setParticipants(participants);
        return training;
    }

    private BasicUserInfoDTO getTestUser() {
        return new BasicUserInfoDTO(
                "20fe07a5-fb18-4006-a721-1a312dc2d370",
                "TestUserName",
                "TestUserSurname",
                "TestAvatarUserUrl"
        );
    }

    @Nested
    class ShouldAcceptRequestAndShouldThrow {

        private RequestBuilder request;
        private String expectedMessage;

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowNoIndividualTrainingFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(NoIndividualTrainingFoundException.class)
                    .when(userIndividualTrainingService)
                    .getMyAllTrainings(userId);

            request = getValidRequest(userToken, testedLocale);
            expectedMessage = messages.get("exception.no.individual.training.found");

            performRequestAndTestErrorResponse(status().isNotFound(), NoIndividualTrainingFoundException.class);
        }

        private void performRequestAndTestErrorResponse(
                ResultMatcher resultMatcher,
                Class<? extends Exception> expectedException
        ) throws Exception {
            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(resultMatcher)
                    .andExpect(status().reason(is(expectedMessage)))
                    .andExpect(result ->
                            assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                    .isInstanceOf(expectedException)
                    );
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowUserNotFoundException(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(UserNotFoundException.class)
                    .when(userIndividualTrainingService)
                    .getMyAllTrainings(userId);
            request = getValidRequest(employeeToken, testedLocale);
            expectedMessage = messages.get("exception.not.found.user.id");

            performRequestAndTestErrorResponse(status().isNotFound(), UserNotFoundException.class);
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void shouldThrowInternalServerError(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            doThrow(IllegalStateException.class)
                    .when(userIndividualTrainingService)
                    .getMyAllTrainings(userId);
            request = getValidRequest(adminToken, testedLocale);
            expectedMessage = messages.get("exception.internal.error");

            performRequestAndTestErrorResponse(status().isInternalServerError(), IllegalStateException.class);
        }
    }

    @Nested
    class ShouldRejectRequest {

        private RequestBuilder request;
        private String expectedMessage;

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenManagerTriesToPerformRequest(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            request = getValidRequest(managerToken, testedLocale);
            expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied();
        }

        private void performAndTestAccessDenied() throws Exception {
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
        void whenTrainerTriesToPerformRequest(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            request = getValidRequest(trainerToken, testedLocale);
            expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserTriesToPerformRequestInBehalfOfAnotherUser(TestCountry country) throws Exception {
            Map<String, String> messages = getMessagesAccordingToLocale(country);
            Locale testedLocale = convertEnumToLocale(country);

            String userId = UUID.randomUUID().toString();
            uri = getUri(userId);
            request = getValidRequest(userToken, testedLocale);
            expectedMessage = messages.get("exception.access.denied");

            performAndTestAccessDenied();
        }

        @ParameterizedTest
        @EnumSource(TestCountry.class)
        void whenUserIsNotLogIn(TestCountry country) throws Exception {
            Locale testedLocale = convertEnumToLocale(country);

            RequestBuilder request = MockMvcRequestBuilders
                    .get(uri)
                    .header("Accept-Language", testedLocale.toString())
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }
}