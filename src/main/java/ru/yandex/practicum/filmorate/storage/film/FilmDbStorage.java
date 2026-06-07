package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.mapping.FilmRowMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String CREATE_FILM_SQL = """
            INSERT INTO films (name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String GET_FILM_BY_ID_SQL = """
            SELECT id, name, description, release_date, duration, mpa_id
            FROM films
            WHERE id = ?
            """;
    private static final String GET_ALL_FILMS_SQL = """
            SELECT id, name, description, release_date, duration, mpa_id
            FROM films
            ORDER BY id
            """;
    private static final String GET_POPULAR_FILMS_SQL = """
            SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id
            FROM films AS f
            LEFT JOIN film_likes AS fl ON f.id = fl.film_id
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id
            ORDER BY COUNT(fl.user_id) DESC, f.id
            LIMIT ?
            """;
    private static final String UPDATE_FILM_SQL = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE id = ?
            """;
    private static final String DELETE_FILM_SQL = "DELETE FROM films WHERE id = ?";
    private static final String ADD_LIKE_SQL = """
            MERGE INTO film_likes (film_id, user_id) KEY(film_id, user_id)
            VALUES (?, ?)
            """;
    private static final String REMOVE_LIKE_SQL = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_LIKES_SQL = "SELECT user_id FROM film_likes WHERE film_id = ?";
    private static final String GET_GENRE_IDS_SQL = """
            SELECT genre_id
            FROM film_genres
            WHERE film_id = ?
            ORDER BY genre_id
            """;
    private static final String DELETE_GENRES_SQL = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String ADD_GENRE_SQL = """
            INSERT INTO film_genres (film_id, genre_id)
            VALUES (?, ?)
            """;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, new FilmRowMapper());
    }

    @Override
    public Film createFilm(Film film) {
        Long mpaId = film.getMpa() == null ? null : film.getMpa().getId();
        long id = insert(
                CREATE_FILM_SQL,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaId
        );
        film.setId(id);
        setGenres(film);
        return film;
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        Optional<Film> film = findOne(GET_FILM_BY_ID_SQL, id);
        film.ifPresent(this::loadRelatedData);
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> films = findMany(GET_ALL_FILMS_SQL);
        films.forEach(this::loadRelatedData);
        return films;
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        Collection<Film> films = findMany(GET_POPULAR_FILMS_SQL, count);
        films.forEach(this::loadRelatedData);
        return films;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        Long mpaId = film.getMpa() == null ? null : film.getMpa().getId();
        int updatedRows = jdbc.update(
                UPDATE_FILM_SQL,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaId,
                film.getId()
        );

        if (updatedRows == 0) {
            return Optional.empty();
        }
        setGenres(film);
        loadLikes(film);
        return Optional.of(film);
    }

    @Override
    public void deleteFilmById(long id) {
        delete(DELETE_FILM_SQL, id);
    }

    @Override
    public void addLike(long filmId, long userId) {
        update(ADD_LIKE_SQL, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        jdbc.update(REMOVE_LIKE_SQL, filmId, userId);
    }

    private void loadLikes(Film film) {
        Set<Long> likes = new HashSet<>(jdbc.queryForList(GET_LIKES_SQL, Long.class, film.getId()));
        film.setLikes(likes);
    }

    private void loadGenres(Film film) {
        Set<Genre> genres = new LinkedHashSet<>();
        jdbc.queryForList(GET_GENRE_IDS_SQL, Long.class, film.getId())
                .forEach(genreId -> {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genres.add(genre);
                });
        film.setGenres(genres);
    }

    private void loadRelatedData(Film film) {
        loadLikes(film);
        loadGenres(film);
    }

    private void setGenres(Film film) {
        jdbc.update(DELETE_GENRES_SQL, film.getId());
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        for (Genre genre : film.getGenres()) {
            update(ADD_GENRE_SQL, film.getId(), genre.getId());
        }
    }
}
