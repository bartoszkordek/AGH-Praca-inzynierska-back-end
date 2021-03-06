package com.healthy.gym.account.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PageSizeValidatorTest {

    private PageSizeValidator pageSizeValidator;

    @BeforeEach
    void setUp() {
        pageSizeValidator = new PageSizeValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"5", "10", "20", "50", "100"})
    void shouldReturnTrue(String value) {
        assertThat(pageSizeValidator.isValid(value, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "-10", "sada", "0.1312", "12.0", "20.0"})
    void shouldReturnFalse(String value) {
        assertThat(pageSizeValidator.isValid(value, null)).isFalse();
    }
}