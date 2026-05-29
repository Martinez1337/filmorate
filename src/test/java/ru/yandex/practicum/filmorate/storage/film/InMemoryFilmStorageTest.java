package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {
    private InMemoryFilmStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryFilmStorage();
    }

    @Test
    void createFilm_assignsIncrementalIdAndStoresFilm() {
        Film first = new Film();
        Film second = new Film();

        Film createdFirst = storage.createFilm(first);
        Film createdSecond = storage.createFilm(second);

        assertSame(first, createdFirst);
        assertSame(second, createdSecond);
        assertEquals(1L, createdFirst.getId());
        assertEquals(2L, createdSecond.getId());
        assertEquals(Optional.of(first), storage.getFilmById(1L));
        assertEquals(Optional.of(second), storage.getFilmById(2L));
    }

    @Test
    void getFilmById_unknownId_returnsEmptyOptional() {
        Optional<Film> result = storage.getFilmById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllFilms_returnsStoredFilms() {
        Film first = storage.createFilm(new Film());
        Film second = storage.createFilm(new Film());

        Collection<Film> result = storage.getAllFilms();

        assertEquals(2, result.size());
        assertTrue(result.contains(first));
        assertTrue(result.contains(second));
    }

    @Test
    void updateFilm_existingId_replacesAndReturnsFilm() {
        Film original = storage.createFilm(new Film());
        Film updated = new Film();
        updated.setId(original.getId());
        updated.setName("Updated");

        Optional<Film> result = storage.updateFilm(updated);

        assertEquals(Optional.of(updated), result);
        assertEquals(Optional.of(updated), storage.getFilmById(original.getId()));
    }

    @Test
    void updateFilm_unknownId_returnsEmptyOptional() {
        Film film = new Film();
        film.setId(999L);

        Optional<Film> result = storage.updateFilm(film);

        assertTrue(result.isEmpty());
        assertTrue(storage.getAllFilms().isEmpty());
    }

    @Test
    void deleteFilmById_existingId_removesFilm() {
        Film film = storage.createFilm(new Film());

        storage.deleteFilmById(film.getId());

        assertTrue(storage.getFilmById(film.getId()).isEmpty());
        assertTrue(storage.getAllFilms().isEmpty());
    }
}
