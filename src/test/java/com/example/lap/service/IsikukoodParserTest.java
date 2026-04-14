package com.example.lap.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IsikukoodParserTest {

    private final IsikukoodParser parser = new IsikukoodParser();

    @Test
    void testGetBirthDate_1900s_Male() {
        // 3 = 1900s male, 90 = 1990, 05 = May, 15 = 15th
        LocalDate birthDate = parser.getBirthDate("39005151234");
        assertThat(birthDate.getYear()).isEqualTo(1990);
        assertThat(birthDate.getMonthValue()).isEqualTo(5);
        assertThat(birthDate.getDayOfMonth()).isEqualTo(15);
    }

    @Test
    void testGetBirthDate_2000s_Female() {
        // 6 = 2000s female, 01 = 2001, 12 = December, 30 = 30th
        LocalDate birthDate = parser.getBirthDate("60112301234");
        assertThat(birthDate.getYear()).isEqualTo(2001);
        assertThat(birthDate.getMonthValue()).isEqualTo(12);
        assertThat(birthDate.getDayOfMonth()).isEqualTo(30);
    }

    @Test
    void testGetAge() {
        int age = parser.getAge("39005151234");
        assertThat(age).isGreaterThan(30); 
    }
    
    @Test
    void testInvalidIsikukood() {
        assertThatThrownBy(() -> parser.getBirthDate("3900"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid personal code format");
    }
}
