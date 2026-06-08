package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.IdDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.dto.mapping.FilmMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmServiceTest {

    private FilmService service;
    private TestGenreStorage genreStorage;

    @BeforeEach
    void setUp() {
        genreStorage = new TestGenreStorage();
        service = new FilmService(
                new FakeFilmStorage(genreStorage),
                new FakeUserStorage(),
                new TestMpaStorage(),
                genreStorage,
                new TestFilmMapper()
        );
    }

    @Test
    void create_validFilm_assignsIdAndStoresFilm() {
        FilmResponseDto created = service.create(newFilmRequestDto(null, "Film", 1L, 1L, 2L));

        assertEquals(1L, created.getId(), "Ожидается id == 1 после создания первого фильма");
        assertEquals("Mpa 1", created.getMpa().getName());
        assertEquals(List.of(1L, 2L), created.getGenres().stream().map(GenreDto::getId).toList());

        Collection<FilmResponseDto> all = service.findAll();
        assertEquals(1, all.size(), "В хранилище должен быть один фильм");
        assertTrue(all.contains(created), "Хранилище должно содержать созданный фильм");
    }

    @Test
    void update_existingFilm_replacesAndReturnsFilm() {
        FilmResponseDto created = service.create(newFilmRequestDto(null, "Original", 1L, 1L));

        FilmResponseDto result = service.update(newFilmRequestDto(created.getId(), "Updated", 2L, 3L));

        assertEquals(created.getId(), result.getId(), "Метод update должен вернуть обновлённый фильм");
        assertEquals("Updated", result.getName());
        assertEquals("Mpa 2", result.getMpa().getName());
        assertEquals(List.of(3L), result.getGenres().stream().map(GenreDto::getId).toList());
        assertTrue(service.findAll().contains(result), "Хранилище должно содержать обновлённый фильм");
    }

    @Test
    void update_nonExistingFilm_throwsNotFoundException() {
        FilmRequestDto film = newFilmRequestDto(999L, "Missing", 1L, 1L);

        assertThrows(NotFoundException.class, () -> service.update(film),
                "Ожидается NotFoundException при обновлении несуществующего фильма");
    }

    private FilmRequestDto newFilmRequestDto(Long id, String name, Long mpaId, Long... genreIds) {
        FilmRequestDto film = new FilmRequestDto();
        film.setId(id);
        film.setName(name);
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(newIdDto(mpaId));

        LinkedHashSet<IdDto> genres = new LinkedHashSet<>();
        for (Long genreId : genreIds) {
            genres.add(newIdDto(genreId));
        }
        film.setGenres(genres);
        return film;
    }

    private IdDto newIdDto(Long id) {
        IdDto idDto = new IdDto();
        idDto.setId(id);
        return idDto;
    }

    private static class FakeFilmStorage implements FilmStorage {
        private final Map<Long, Film> films = new LinkedHashMap<>();
        private final TestGenreStorage genreStorage;
        private long nextId = 1L;

        private FakeFilmStorage(TestGenreStorage genreStorage) {
            this.genreStorage = genreStorage;
        }

        @Override
        public Film createFilm(Film film) {
            film.setId(nextId++);
            films.put(film.getId(), film);
            genreStorage.setGenresForFilm(film.getId(), film.getGenres());
            return film;
        }

        @Override
        public Optional<Film> getFilmById(long id) {
            return Optional.ofNullable(films.get(id));
        }

        @Override
        public Collection<Film> getAllFilms() {
            return films.values();
        }

        @Override
        public Collection<Film> getPopularFilms(int count) {
            return films.values().stream()
                    .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                    .limit(count)
                    .toList();
        }

        @Override
        public Optional<Film> updateFilm(Film film) {
            if (!films.containsKey(film.getId())) {
                return Optional.empty();
            }
            films.put(film.getId(), film);
            genreStorage.setGenresForFilm(film.getId(), film.getGenres());
            return Optional.of(film);
        }

        @Override
        public void deleteFilmById(long id) {
            films.remove(id);
        }
    }

    private static class FakeUserStorage implements UserStorage {
        @Override
        public User createUser(User user) {
            return user;
        }

        @Override
        public Optional<User> getUserById(long id) {
            User user = new User();
            user.setId(id);
            return Optional.of(user);
        }

        @Override
        public Collection<User> getAllUsers() {
            return List.of();
        }

        @Override
        public Optional<User> updateUser(User user) {
            return Optional.of(user);
        }

        @Override
        public void deleteUserById(long id) {
        }

        @Override
        public Collection<User> getUserFriends(long userId) {
            return List.of();
        }

        @Override
        public Collection<User> getCommonFriends(long userId, long friendId) {
            return List.of();
        }
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

    private static class TestGenreStorage implements GenreStorage {
        private final Map<Long, List<Genre>> genresByFilmId = new LinkedHashMap<>();

        @Override
        public Optional<Genre> getGenreById(long id) {
            return Optional.of(newGenre(id));
        }

        @Override
        public Collection<Genre> getAllGenres() {
            return List.of();
        }

        @Override
        public List<Genre> getGenresByIds(Collection<Long> ids) {
            return ids.stream()
                    .distinct()
                    .map(TestGenreStorage::newGenre)
                    .toList();
        }

        @Override
        public Map<Long, List<Genre>> getGenresForFilms(Collection<Long> ids) {
            Map<Long, List<Genre>> result = new LinkedHashMap<>();
            for (Long id : ids) {
                result.put(id, genresByFilmId.getOrDefault(id, List.of()));
            }
            return result;
        }

        @Override
        public void setGenresForFilm(long filmId, Collection<Genre> genres) {
            genresByFilmId.put(filmId, genres.stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .toList());
        }

        @Override
        public void deleteGenresFromFilm(long filmId) {
            genresByFilmId.remove(filmId);
        }

        private static Genre newGenre(long id) {
            Genre genre = new Genre();
            genre.setId(id);
            genre.setName("Genre " + id);
            return genre;
        }
    }

    private static class TestFilmMapper implements FilmMapper {
        @Override
        public FilmResponseDto mapToRsDto(Film film) {
            FilmResponseDto response = new FilmResponseDto()
                    .setId(film.getId())
                    .setName(film.getName())
                    .setDescription(film.getDescription())
                    .setReleaseDate(film.getReleaseDate())
                    .setDuration(film.getDuration());

            if (film.getMpa() != null) {
                response.setMpa(new MpaDto()
                        .setId(film.getMpa().getId())
                        .setName(film.getMpa().getName()));
            }

            response.setGenres(film.getGenres().stream()
                    .map(genre -> new GenreDto()
                            .setId(genre.getId())
                            .setName(genre.getName()))
                    .toList());
            return response;
        }

        @Override
        public Film map(FilmRequestDto filmRqDto) {
            Film film = new Film();
            film.setId(filmRqDto.getId());
            film.setName(filmRqDto.getName());
            film.setDescription(filmRqDto.getDescription());
            film.setReleaseDate(filmRqDto.getReleaseDate());
            film.setDuration(filmRqDto.getDuration());

            if (filmRqDto.getMpa() != null) {
                Mpa mpa = new Mpa();
                mpa.setId(filmRqDto.getMpa().getId());
                film.setMpa(mpa);
            }

            film.setGenres(filmRqDto.getGenres().stream()
                    .map(idDto -> {
                        Genre genre = new Genre();
                        genre.setId(idDto.getId());
                        return genre;
                    })
                    .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new)));
            return film;
        }
    }
}
