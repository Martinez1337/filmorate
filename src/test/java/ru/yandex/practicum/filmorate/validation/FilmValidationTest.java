package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmValidationTest extends ValidationBaseTest {

    @Test
    void film_nameBlank_constraintViolation() {
        Film film = new Film();
        film.setName(" ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")), "Ожидается ошибка для name");
    }

    @Test
    void film_descriptionTooLong_constraintViolation() {
        Film film = new Film();
        film.setDescription("x".repeat(201));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")), "Ожидается ошибка для description");
    }

    @Test
    void film_releaseDateBeforeMin_constraintViolation() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")), "Ожидается ошибка для releaseDate");
    }

    @Test
    void film_nameBlank_and_descriptionTooLong_and_releaseDateBeforeMin_and_nonPositiveDuration() {
        Film film = new Film();
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("duration")), "Ожидается ошибка для duration");
    }

    @Test
    void film_minDateBoundary_isValid() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("releaseDate")), "releaseDate на границе (1895-12-28) должен проходить валидацию");
    }

    @Test
    void film_futureReleaseDate_constraintViolation() {
        Film film = new Film();
        film.setReleaseDate(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")), "Ожидается ошибка для releaseDate в будущем");
    }
}
