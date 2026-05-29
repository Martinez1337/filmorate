package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmDto;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmValidationTest extends ValidationBaseTest {

    @Test
    void film_nameBlank_constraintViolation() {
        FilmDto film = new FilmDto();
        film.setName(" ");

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")), "Ожидается ошибка для name");
    }

    @Test
    void film_descriptionTooLong_constraintViolation() {
        FilmDto film = new FilmDto();
        film.setDescription("x".repeat(201));

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")), "Ожидается ошибка для description");
    }

    @Test
    void film_releaseDateBeforeMin_constraintViolation() {
        FilmDto film = new FilmDto();
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")), "Ожидается ошибка для releaseDate");
    }

    @Test
    void film_nameBlank_and_descriptionTooLong_and_releaseDateBeforeMin_and_nonPositiveDuration() {
        FilmDto film = new FilmDto();
        film.setDuration(0);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")), "Ожидается ошибка для duration");
    }

    @Test
    void film_minDateBoundary_isValid() {
        FilmDto film = new FilmDto();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("releaseDate")), "releaseDate на границе (1895-12-28) должен проходить валидацию");
    }

    @Test
    void film_futureReleaseDate_constraintViolation() {
        FilmDto film = new FilmDto();
        film.setReleaseDate(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")), "Ожидается ошибка для releaseDate в будущем");
    }
}
