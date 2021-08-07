package com.healthy.gym.trainings.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PageNumberValidatorTest {
    private PageNumberValidator pageNumberValidator;

    @BeforeEach
    void setUp() {
        pageNumberValidator = new PageNumberValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "10", "20", "50", "100", "100000", "2147483647"})
    void shouldReturnTrue(String value) {
        int number = Integer.parseInt(value);
        assertThat(pageNumberValidator.isValid(number, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0", "-100000", "-2147483648"})
    void shouldReturnFalse(String value) {
        int number = Integer.parseInt(value);
        assertThat(pageNumberValidator.isValid(number, null)).isFalse();
    }
}