package com.healthy.gym.trainings.controller.individual.training.employee.integration.tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.trainings.configuration.TestCountry;
import com.healthy.gym.trainings.configuration.TestRoleTokenFactory;
import com.healthy.gym.trainings.data.document.IndividualTrainingDocument;
import com.healthy.gym.trainings.data.document.LocationDocument;
import com.healthy.gym.trainings.data.document.TrainingTypeDocument;
import com.healthy.gym.trainings.data.document.UserDocument;
import com.healthy.gym.trainings.dto.IndividualTrainingDTO;
import com.healthy.gym.trainings.test.utils.TestDocumentUtilComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.healthy.gym.trainings.configuration.Messages.getMessagesAccordingToLocale;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@Tag("integration")
class GetAllIndividualTrainingRequestsIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:4.4.4-bionic"));
    @Container
    static GenericContainer<?> rabbitMQContainer =
            new GenericContainer<>(DockerImageName.parse("gza73/agh-praca-inzynierska-rabbitmq"))
                    .withExposedPorts(5672);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestDocumentUtilComponent testUtil;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @Autowired
    private TestRestTemplate restTemplate;

    private URI uri;
    private String employeeToken;

    @LocalServerPort
    private Integer port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        employeeToken = tokenFactory.getEmployeeToken();

        List<String[]> data = List.of(
                new String[]{"2021-01-01T10:10", "2021-01-01T11:00", "false"},
                new String[]{"2021-01-02T10:10", "2021-01-02T11:00", "true"},
                new String[]{"2021-01-03T10:10", "2021-01-03T11:00", "false"},
                new String[]{"2021-01-04T10:10", "2021-01-04T11:00", "true"},
                new String[]{"2021-01-05T10:10", "2021-01-05T11:00", "true"},

                new String[]{"2021-01-06T10:10", "2021-01-06T11:00", "false"},
                new String[]{"2021-01-06T20:10", "2021-01-06T21:00", "true"},
                new String[]{"2021-01-07T10:10", "2021-01-07T11:00", "true"},
                new String[]{"2021-01-08T10:10", "2021-01-08T11:00", "false"},
                new String[]{"2021-01-09T10:10", "2021-01-09T11:00", "true"},

                new String[]{"2021-01-10T10:10", "2021-01-10T11:00", "true"},
                new String[]{"2021-01-10T12:10", "2021-01-10T12:00", "true"}
        );

        data.forEach(
                strings -> testUtil
                        .saveAndGetTestIndividualTraining(
                                strings[0],
                                strings[1],
                                Boolean.parseBoolean(strings[2])
                        )
        );
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(IndividualTrainingDocument.class);
        mongoTemplate.dropCollection(LocationDocument.class);
        mongoTemplate.dropCollection(TrainingTypeDocument.class);
        mongoTemplate.dropCollection(UserDocument.class);
    }

    private ResponseEntity<JsonNode> performAuthRequest(URI uri) {
        var request = getAuthRequest();
        return performRequest(uri, request);
    }

    private ResponseEntity<JsonNode> performRequest(URI uri, HttpEntity<Object> request) {
        return restTemplate.exchange(uri, HttpMethod.GET, request, JsonNode.class);
    }

    private HttpEntity<Object> getAuthRequest() {
        var headers = getHeadersWithAuth();
        return new HttpEntity<>(null, headers);
    }

    private HttpHeaders getHeadersWithAuth() {
        var headers = getHeaders();
        headers.set("Authorization", employeeToken);
        return headers;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", Locale.ENGLISH.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private URI getUri(String startDate, String endDate, String pageNumber, String pageSize)
            throws URISyntaxException {
        return new URI("http://localhost:" + port + "/individual/employee"
                + "?startDate=" + startDate
                + "&endDate=" + endDate
                + "&pageNumber=" + pageNumber
                + "&pageSize=" + pageSize
        );
    }

    @ParameterizedTest
    @CsvSource({
            "2021-01-01,2021-01-09,0,5,5",
            "2021-01-01,2021-01-09,1,5,5",
            "2021-01-05,2021-01-06,0,20,3",
            "2021-01-05,2021-01-10,0,5,5",
            "2021-01-05,2021-01-10,1,5,3",
            "2021-01-01,2021-01-05,0,10,5"
    })
    void shouldAcceptIndividualTrainingRequest(
            String startDate,
            String endDate,
            String pageNumber,
            String pageSize,
            long expectedNumberOfTrainingsWithinDates
    ) throws Exception {
        uri = getUri(startDate, endDate, pageNumber, pageSize);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        assertThat(body.get("message")).isNull();

        ObjectMapper objectMapper = new ObjectMapper();
        List<IndividualTrainingDTO> individualTrainingDTOList = objectMapper
                .readValue(body.toString(), new TypeReference<>() {
                });

        assertThat(individualTrainingDTOList.size()).isEqualTo(expectedNumberOfTrainingsWithinDates);
    }

    @ParameterizedTest
    @CsvSource({
            "2021-02-01,2021-02-09,0,20",
            "2021-01-01,2021-01-09,2,5",
    })
    void shouldThrowNoIndividualTrainingFoundException(
            String startDate,
            String endDate,
            String pageNumber,
            String pageSize
    ) throws Exception {
        uri = getUri(startDate, endDate, pageNumber, pageSize);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.no.individual.training.found");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
    }

    @ParameterizedTest
    @CsvSource({
            "2021-02-10,2021-02-09,0,20",
            "2021-01-02,2021-01-01,2,5",
    })
    void shouldThrowStartDateAfterEndDateException(
            String startDate,
            String endDate,
            String pageNumber,
            String pageSize
    ) throws Exception {
        uri = getUri(startDate, endDate, pageNumber, pageSize);
        ResponseEntity<JsonNode> responseEntity = performAuthRequest(uri);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

        JsonNode body = responseEntity.getBody();
        assert body != null;
        Map<String, String> messages = getMessagesAccordingToLocale(TestCountry.ENGLAND);
        String expectedMessage = messages.get("exception.start.date.after.end.date");
        assertThat(body.get("message").textValue()).isEqualTo(expectedMessage);
    }
}
