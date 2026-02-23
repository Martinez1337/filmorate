package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private FilmService service;

    @BeforeEach
    void setUp() {
        service = new FilmService();
    }

    @Test
    void create_validFilm_assignsIdAndStoresFilm() {
        Film film = new Film();
        Film created = service.create(film);

        assertEquals(1, created.getId(), "Ожидается id == 1 после создания первого фильма");
        assertSame(film, created, "Метод create должен возвращать тот же объект фильма");
        Collection<Film> all = service.findAll();
        assertEquals(1, all.size(), "В хранилище должен быть один фильм");
        assertTrue(all.contains(created), "Хранилище должно содержать созданный фильм");
    }

    @Test
    void update_existingFilm_replacesAndReturnsFilm() {
        Film original = new Film();
        service.create(original);
        int id = original.getId();

        Film updated = new Film();
        updated.setId(id);
        Film result = service.update(updated);

        assertSame(updated, result, "Метод update должен вернуть переданный объект");
        Collection<Film> all = service.findAll();
        assertEquals(1, all.size(), "В хранилище должен быть один фильм после обновления");
        assertTrue(all.contains(updated), "Хранилище должно содержать обновлённый фильм");
    }

    @Test
    void update_nonExistingFilm_throwsNotFoundException() {
        Film film = new Film();
        film.setId(999);

        assertThrows(NotFoundException.class, () -> service.update(film),
                "Ожидается NotFoundException при обновлении несуществующего фильма");
    }
}
