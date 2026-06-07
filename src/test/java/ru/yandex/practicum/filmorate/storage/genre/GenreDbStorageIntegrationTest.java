package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(GenreDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageIntegrationTest {
    private final GenreDbStorage genreStorage;
    private final JdbcTemplate jdbc;

    @Test
    void getGenreById_existingId_returnsGenre() {
        assertThat(genreStorage.getGenreById(1))
                .isPresent()
                .hasValueSatisfying(genre -> {
                    assertThat(genre.getId()).isEqualTo(1L);
                    assertThat(genre.getName()).isEqualTo("Комедия");
                });
    }

    @Test
    void getGenreById_unknownId_returnsEmptyOptional() {
        assertThat(genreStorage.getGenreById(999)).isEmpty();
    }

    @Test
    void getAllGenres_returnsAllGenresOrderedById() {
        assertThat(genreStorage.getAllGenres())
                .extracting("id")
                .containsExactly(1L, 2L, 3L, 4L, 5L, 6L);
    }

    @Test
    void setGetAndDeleteGenresForFilm_updatesFilmGenres() {
        long filmId = createFilm();
        Genre firstGenre = new Genre();
        firstGenre.setId(1L);
        Genre thirdGenre = new Genre();
        thirdGenre.setId(3L);

        genreStorage.setGenresForFilm(filmId, Set.of(thirdGenre, firstGenre));

        assertThat(genreStorage.getGenresForFilms(List.of(filmId)).get((int) filmId))
                .extracting("id")
                .containsExactly(1L, 3L);

        genreStorage.deleteGenresFromFilm(filmId);

        assertThat(genreStorage.getGenresForFilms(List.of(filmId))).doesNotContainKey((int) filmId);
    }

    private long createFilm() {
        jdbc.update(
                """
                        INSERT INTO films (id, name, description, release_date, duration, mpa_id)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                1L,
                "Film",
                "Description",
                LocalDate.of(2000, 1, 1),
                100,
                1L
        );
        return 1L;
    }
}
