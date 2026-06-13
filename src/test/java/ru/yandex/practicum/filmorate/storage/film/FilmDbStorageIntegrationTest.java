package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(FilmDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageIntegrationTest {
    private final FilmDbStorage filmStorage;
    private final JdbcTemplate jdbc;

    @Test
    void createAndGetFilmById_returnsCreatedFilmWithMpaAndGenres() {
        Film created = filmStorage.createFilm(newFilm("Film", 3L, 5L, 3L));

        assertThat(filmStorage.getFilmById(created.getId()))
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId()).isEqualTo(created.getId());
                    assertThat(film.getName()).isEqualTo("Film");
                    assertThat(film.getMpa().getId()).isEqualTo(3L);
                    assertThat(film.getGenres()).extracting("id").containsExactly(3L, 5L);
                });
    }

    @Test
    void updateFilm_existingFilm_updatesFilmDataAndReplacesGenres() {
        Film created = filmStorage.createFilm(newFilm("Old film", 1L, 1L, 2L));
        created.setName("New film");
        created.setDescription("New description");
        created.setReleaseDate(LocalDate.of(2005, 5, 5));
        created.setDuration(200);
        created.setMpa(newMpa(4L));
        created.setGenres(new LinkedHashSet<>(Set.of(newGenre(6L))));

        assertThat(filmStorage.updateFilm(created))
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getName()).isEqualTo("New film");
                    assertThat(film.getMpa().getId()).isEqualTo(4L);
                    assertThat(film.getGenres()).extracting("id").containsExactly(6L);
                });
    }

    @Test
    void addAndRemoveLike_updatesFilmLikes() {
        Film created = filmStorage.createFilm(newFilm("Film", 1L, 1L));
        long userId = createUser(1L, "user@example.com", "user");

        filmStorage.addLike(created.getId(), userId);

        assertThat(filmStorage.getFilmById(created.getId()))
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film.getLikes()).containsExactly(userId));

        filmStorage.removeLike(created.getId(), userId);

        assertThat(filmStorage.getFilmById(created.getId()))
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film.getLikes()).isEmpty());
    }

    @Test
    void getPopularFilms_returnsFilmsOrderedByLikesCountDesc() {
        Film first = filmStorage.createFilm(newFilm("First", 1L, 1L));
        Film second = filmStorage.createFilm(newFilm("Second", 1L, 2L));
        long firstUserId = createUser(1L, "first@example.com", "first");
        long secondUserId = createUser(2L, "second@example.com", "second");

        filmStorage.addLike(first.getId(), firstUserId);
        filmStorage.addLike(second.getId(), firstUserId);
        filmStorage.addLike(second.getId(), secondUserId);

        assertThat(filmStorage.getPopularFilms(1))
                .extracting("id")
                .containsExactly(second.getId());
    }

    @Test
    void deleteFilmById_removesFilm() {
        Film created = filmStorage.createFilm(newFilm("Film", 1L, 1L));

        filmStorage.deleteFilmById(created.getId());

        assertThat(filmStorage.getFilmById(created.getId())).isEmpty();
    }

    private Film newFilm(String name, Long mpaId, Long... genreIds) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(newMpa(mpaId));
        Set<Genre> genres = new LinkedHashSet<>();
        for (Long genreId : genreIds) {
            genres.add(newGenre(genreId));
        }
        film.setGenres(genres);
        return film;
    }

    private Mpa newMpa(Long id) {
        Mpa mpa = new Mpa();
        mpa.setId(id);
        return mpa;
    }

    private Genre newGenre(Long id) {
        Genre genre = new Genre();
        genre.setId(id);
        return genre;
    }

    private long createUser(Long id, String email, String login) {
        jdbc.update(
                """
                        INSERT INTO users (id, email, login, name, birthday)
                        VALUES (?, ?, ?, ?, ?)
                        """,
                id,
                email,
                login,
                login,
                LocalDate.of(1990, 1, 1)
        );
        return id;
    }
}
