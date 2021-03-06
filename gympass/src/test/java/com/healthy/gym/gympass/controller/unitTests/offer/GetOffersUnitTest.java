package com.healthy.gym.gympass.controller.unitTests.offer;

import com.healthy.gym.gympass.configuration.TestCountry;
import com.healthy.gym.gympass.configuration.TestRoleTokenFactory;
import com.healthy.gym.gympass.controller.offer.OfferController;
import com.healthy.gym.gympass.dto.GymPassDTO;
import com.healthy.gym.gympass.exception.NoOffersException;
import com.healthy.gym.gympass.service.OfferService;
import com.healthy.gym.gympass.shared.Description;
import com.healthy.gym.gympass.shared.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.ResultMatcher.matchAll;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static com.healthy.gym.gympass.configuration.LocaleConverter.convertEnumToLocale;
import static com.healthy.gym.gympass.configuration.Messages.getMessagesAccordingToLocale;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(OfferController.class)
@ActiveProfiles(value = "test")
class GetOffersUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRoleTokenFactory tokenFactory;

    @MockBean
    private OfferService offerService;

    private String managerToken;
    private String userToken;
    private URI uri;

    @BeforeEach
    void setUp() throws URISyntaxException {
        String userId = UUID.randomUUID().toString();
        userToken = tokenFactory.getUserToken(userId);

        String managerId = UUID.randomUUID().toString();
        managerToken = tokenFactory.getMangerToken(managerId);

        uri = new URI("/offer");
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldReturnAllOffers(TestCountry country) throws Exception {
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON);

        String gymPass1Id = UUID.randomUUID().toString();
        String gymPass2Id = UUID.randomUUID().toString();


        GymPassDTO gymPass1 = new GymPassDTO(
                gymPass1Id,
                "Standardowy",
                "Najpopularniejszy",
                new Price(139.99, "z??", "miesi??c"),
                true,
                new Description(
                        "Najlepszy wyb??r dla os??b regularnie ??wicz??cych",
                        List.of(
                                "dost??p do ka??dego sprz??tu fitness",
                                "grupowe i indywidualne zaj??cia fitness",
                                "dowolne godziny wej??cia",
                                "nieograniczony czas wej??cia",
                                "nieograniczona liczba wej????",
                                "wa??no???? 30 dni",
                                "dost??p do sauny"
                        )
                )
        );

        GymPassDTO gymPass2 = new GymPassDTO(
                gymPass2Id,
                "Wej??cie jednorazowe",
                null,
                new Price(19.99, "z??", "wej??cie"),
                false,
                new Description(
                        "Gdy potrzebujesz skorzysta?? jednorazowo z naszej si??owni",
                        List.of(
                                "dost??p do ka??dego sprz??tu fitness",
                                "dowolne godziny wej??cia",
                                "nieograniczony czas wej??cia",
                                "dost??p do sauny"
                        )
                )
        );

        List<GymPassDTO> returnedList = List.of(gymPass1, gymPass2);

        when(offerService.getGymPassOffers()).thenReturn(returnedList);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(matchAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").doesNotHaveJsonPath(),
                        jsonPath("$.[0].documentId").value(is(gymPass1Id)),
                        jsonPath("$.[0].title").value(is("Standardowy")),
                        jsonPath("$.[0].subheader").value(is("Najpopularniejszy")),
                        jsonPath("$.[0].price.amount").value(is(139.99)),
                        jsonPath("$.[0].price.currency").value(is("z??")),
                        jsonPath("$.[0].price.period").value(is("miesi??c")),
                        jsonPath("$.[0].description.synopsis")
                                .value(is("Najlepszy wyb??r dla os??b regularnie ??wicz??cych")),

                        jsonPath("$.[0].description.features").isArray(),

                        jsonPath("$.[0].description.features")
                                .value(hasItem("dost??p do ka??dego sprz??tu fitness")),

                        jsonPath("$.[0].description.features")
                                .value(hasItem("grupowe i indywidualne zaj??cia fitness")),

                        jsonPath("$.[0].description.features")
                                .value(hasItem("dowolne godziny wej??cia")),

                        jsonPath("$.[0].description.features")
                                .value(hasItem("nieograniczony czas wej??cia")),

                        jsonPath("$.[0].description.features")
                                .value(hasItem("nieograniczona liczba wej????")),

                        jsonPath("$.[0].description.features")
                                .value(hasItem("wa??no???? 30 dni")),

                        jsonPath("$.[0].description.features")
                                .value(hasItem("dost??p do sauny")),

                        jsonPath("$.[1].documentId").value(is(gymPass2Id)),
                        jsonPath("$.[1].title").value(is("Wej??cie jednorazowe")),
                        jsonPath("$.[1].subheader").doesNotExist(),
                        jsonPath("$.[1].price.amount").value(is(19.99)),
                        jsonPath("$.[1].price.currency").value(is("z??")),
                        jsonPath("$.[1].price.period").value(is("wej??cie")),
                        jsonPath("$.[1].description.synopsis")
                                .value(is("Gdy potrzebujesz skorzysta?? jednorazowo z naszej si??owni")),

                        jsonPath("$.[1].description.features").isArray(),

                        jsonPath("$.[1].description.features")
                                .value(hasItem("dost??p do ka??dego sprz??tu fitness")),

                        jsonPath("$.[1].description.features")
                                .value(hasItem("dowolne godziny wej??cia")),

                        jsonPath("$.[1].description.features")
                                .value(hasItem("nieograniczony czas wej??cia")),

                        jsonPath("$.[1].description.features")
                                .value(hasItem("dost??p do sauny"))
                ));
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldNotReturnAnyOfferWhenEmptyOffersList(TestCountry country) throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = messages.get("exception.no.offers");

        doThrow(NoOffersException.class)
                .when(offerService)
                .getGymPassOffers();

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                .isInstanceOf(NoOffersException.class)
                );
    }

    @ParameterizedTest
    @EnumSource(TestCountry.class)
    void shouldThrowIllegalStateExceptionWhenInternalErrorOccurs(TestCountry country)
            throws Exception {
        Map<String, String> messages = getMessagesAccordingToLocale(country);
        Locale testedLocale = convertEnumToLocale(country);

        RequestBuilder request = MockMvcRequestBuilders
                .get(uri)
                .header("Accept-Language", testedLocale.toString())
                .header("Authorization", managerToken)
                .contentType(MediaType.APPLICATION_JSON);

        doThrow(IllegalStateException.class)
                .when(offerService)
                .getGymPassOffers();

        String expectedMessage = messages.get("exception.internal.error");

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(status().reason(is(expectedMessage)))
                .andExpect(result ->
                        assertThat(Objects.requireNonNull(result.getResolvedException()).getCause())
                                .isInstanceOf(IllegalStateException.class)
                );
    }
}
