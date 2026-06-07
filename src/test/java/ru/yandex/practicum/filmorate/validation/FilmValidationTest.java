package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmValidationTest extends ValidationBaseTest {

    @Test
    void film_nameBlank_constraintViolation() {
        FilmRequestDto film = new FilmRequestDto();
        film.setName(" ");

        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")), "Ожидается ошибка для name");
    }

    @Test
    void film_descriptionTooLong_constraintViolation() {
        FilmRequestDto film = new FilmRequestDto();
        film.setDescription("x".repeat(201));

        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")), "Ожидается ошибка для description");
    }

    @Test
    void film_releaseDateBeforeMin_constraintViolation() {
        FilmRequestDto film = new FilmRequestDto();
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")), "Ожидается ошибка для releaseDate");
    }

    @Test
    void film_nameBlank_and_descriptionTooLong_and_releaseDateBeforeMin_and_nonPositiveDuration() {
        FilmRequestDto film = new FilmRequestDto();
        film.setDuration(0);

        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")), "Ожидается ошибка для duration");
    }

    @Test
    void film_minDateBoundary_isValid() {
        FilmRequestDto film = new FilmRequestDto();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(film);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("releaseDate")), "releaseDate на границе (1895-12-28) должен проходить валидацию");
    }

    @Test
    void film_futureReleaseDate_constraintViolation() {
        FilmRequestDto film = new FilmRequestDto();
        film.setReleaseDate(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<FilmRequestDto>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")), "Ожидается ошибка для releaseDate в будущем");
    }
}
