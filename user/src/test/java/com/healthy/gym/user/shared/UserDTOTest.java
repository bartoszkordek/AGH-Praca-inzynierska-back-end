package com.healthy.gym.user.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDTOTest {

    private UserDTO userDTO1;
    private UserDTO userDTO2;

    @BeforeEach
    void setUp() {
        userDTO1 = new UserDTO();
        userDTO2 = new UserDTO();
    }

    @Test
    void twoEmptyUserDTOShouldBeEqual() {
        assertThat(userDTO1)
                .isEqualTo(userDTO2)
                .hasSameHashCodeAs(userDTO2);
    }

    @Test
    void userDTOHasAllNullField() {
        assertThat(userDTO1).hasAllNullFieldsOrProperties();
        assertThat(userDTO2).hasAllNullFieldsOrProperties();
    }

    @Test
    void twoUserDTOWithSameFieldValuesShouldBeEqual() {
        userDTO1.setUserId("randomID");
        userDTO1.setName("John");
        userDTO2.setUserId("randomID");
        userDTO2.setName("John");
        assertThat(userDTO1)
                .isEqualTo(userDTO2)
                .hasSameHashCodeAs(userDTO2);
    }

    @Test
    void twoUserDTOWithDifferentFieldValuesShouldNotBeEqual() {
        userDTO1.setUserId("randomID");
        userDTO1.setName("John");
        userDTO2.setUserId("randomID");
        userDTO2.setName("Johny");

        assertThat(userDTO1).isNotEqualTo(userDTO2);
        assertThat(userDTO1.hashCode()).isNotEqualTo(userDTO2.hashCode());
    }
}