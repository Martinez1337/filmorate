package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.mapping.FilmMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private FilmService service;

    @BeforeEach
    void setUp() {
        service = new FilmService(
                new InMemoryFilmStorage(),
                new InMemoryUserStorage(),
                Mappers.getMapper(FilmMapper.class)
        );
    }

    @Test
    void create_validFilm_assignsIdAndStoresFilm() {
        FilmDto film = new FilmDto();
        FilmDto created = service.create(film);

        assertEquals(1L, created.getId(), "Ожидается id == 1 после создания первого фильма");
        Collection<FilmDto> all = service.findAll();
        assertEquals(1, all.size(), "В хранилище должен быть один фильм");
        assertTrue(all.contains(created), "Хранилище должно содержать созданный фильм");
    }

    @Test
    void update_existingFilm_replacesAndReturnsFilm() {
        FilmDto original = new FilmDto();
        FilmDto created = service.create(original);
        Long id = created.getId();

        FilmDto updated = new FilmDto();
        updated.setId(id);
        FilmDto result = service.update(updated);

        assertEquals(updated, result, "Метод update должен вернуть обновлённый фильм");
        Collection<FilmDto> all = service.findAll();
        assertEquals(1, all.size(), "В хранилище должен быть один фильм после обновления");
        assertTrue(all.contains(updated), "Хранилище должно содержать обновлённый фильм");
    }

    @Test
    void update_nonExistingFilm_throwsNotFoundException() {
        FilmDto film = new FilmDto();
        film.setId(999L);

        assertThrows(NotFoundException.class, () -> service.update(film),
                "Ожидается NotFoundException при обновлении несуществующего фильма");
    }
}
