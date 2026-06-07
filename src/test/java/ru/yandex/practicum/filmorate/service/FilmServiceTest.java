package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.mapping.FilmMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    private FilmService service;

    @BeforeEach
    void setUp() {
        service = new FilmService(
                new InMemoryFilmStorage(),
                new InMemoryUserStorage(),
                new TestMpaStorage(),
                new TestGenreStorage(),
                new TestFilmMapper()
        );
    }

    @Test
    void create_validFilm_assignsIdAndStoresFilm() {
        FilmRequestDto film = new FilmRequestDto();
        FilmResponseDto created = service.create(film);

        assertEquals(1L, created.getId(), "Ожидается id == 1 после создания первого фильма");
        Collection<FilmResponseDto> all = service.findAll();
        assertEquals(1, all.size(), "В хранилище должен быть один фильм");
        assertTrue(all.contains(created), "Хранилище должно содержать созданный фильм");
    }

    @Test
    void update_existingFilm_replacesAndReturnsFilm() {
        FilmRequestDto original = new FilmRequestDto();
        FilmResponseDto created = service.create(original);
        Long id = created.getId();

        FilmRequestDto updated = new FilmRequestDto();
        updated.setId(id);
        FilmResponseDto result = service.update(updated);

        assertEquals(updated.getId(), result.getId(), "Метод update должен вернуть обновлённый фильм");
        Collection<FilmResponseDto> all = service.findAll();
        assertEquals(1, all.size(), "В хранилище должен быть один фильм после обновления");
        assertTrue(all.contains(result), "Хранилище должно содержать обновлённый фильм");
    }

    @Test
    void update_nonExistingFilm_throwsNotFoundException() {
        FilmRequestDto film = new FilmRequestDto();
        film.setId(999L);

        assertThrows(NotFoundException.class, () -> service.update(film),
                "Ожидается NotFoundException при обновлении несуществующего фильма");
    }

    private static class TestMpaStorage implements MpaStorage {
        @Override
        public Optional<Mpa> getById(long id) {
            Mpa mpa = new Mpa();
            mpa.setId(id);
            mpa.setName("Mpa " + id);
            return Optional.of(mpa);
        }

        @Override
        public Collection<Mpa> getAll() {
            return List.of();
        }
    }

    private static class TestFilmMapper implements FilmMapper {
        @Override
        public FilmResponseDto mapToRsDto(ru.yandex.practicum.filmorate.model.Film film) {
            return new FilmResponseDto()
                    .setId(film.getId())
                    .setName(film.getName())
                    .setDescription(film.getDescription())
                    .setReleaseDate(film.getReleaseDate())
                    .setDuration(film.getDuration());
        }

        @Override
        public ru.yandex.practicum.filmorate.model.Film map(FilmRequestDto filmRqDto) {
            ru.yandex.practicum.filmorate.model.Film film = new ru.yandex.practicum.filmorate.model.Film();
            film.setId(filmRqDto.getId());
            film.setName(filmRqDto.getName());
            film.setDescription(filmRqDto.getDescription());
            film.setReleaseDate(filmRqDto.getReleaseDate());
            film.setDuration(filmRqDto.getDuration());
            return film;
        }
    }

    private static class TestGenreStorage implements GenreStorage {
        @Override
        public Optional<Genre> getGenreById(long id) {
            Genre genre = new Genre();
            genre.setId(id);
            genre.setName("Genre " + id);
            return Optional.of(genre);
        }

        @Override
        public Collection<Genre> getAllGenres() {
            return List.of();
        }

        @Override
        public Map<Integer, Set<Genre>> getGenresForFilms(Collection<Long> id) {
            return Map.of();
        }

        @Override
        public void setGenresForFilm(long filmId, Collection<Genre> genres) {
        }

        @Override
        public void deleteGenresFromFilm(long filmId) {
        }
    }
}
