package com.example.lap.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IsikukoodParserTest {

    private final IsikukoodParser parser = new IsikukoodParser();

    @Test
    void shouldParseBirthDateFrom1900s() {
        LocalDate date1 = parser.getBirthDate("39001010000"); // 1990-01-01
        assertThat(date1).isEqualTo(LocalDate.of(1990, 1, 1));

        LocalDate date2 = parser.getBirthDate("49912310000"); // 1999-12-31
        assertThat(date2).isEqualTo(LocalDate.of(1999, 12, 31));
    }

    @Test
    void shouldParseBirthDateFrom2000s() {
        LocalDate date1 = parser.getBirthDate("50001010000"); // 2000-01-01
        assertThat(date1).isEqualTo(LocalDate.of(2000, 1, 1));

        LocalDate date2 = parser.getBirthDate("60512310000"); // 2005-12-31
        assertThat(date2).isEqualTo(LocalDate.of(2005, 12, 31));
    }

    @Test
    void shouldThrowExceptionForInvalidFormat() {
        assertThatThrownBy(() -> parser.getBirthDate("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid personal code format");

        assertThatThrownBy(() -> parser.getBirthDate(null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> parser.getBirthDate("3900101000")) // 10 chars
                .isInstanceOf(IllegalArgumentException.class);
    }
}
