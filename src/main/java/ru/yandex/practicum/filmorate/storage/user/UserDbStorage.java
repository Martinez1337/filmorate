package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.mapping.UserRowMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String CREATE_USER_SQL = """
            INSERT INTO users (email, login, name, birthday)
            VALUES (?, ?, ?, ?)
            """;
    private static final String GET_USER_BY_ID_SQL = """
            SELECT id, email, login, name, birthday
            FROM users
            WHERE id = ?
            """;
    private static final String GET_ALL_USERS_SQL = """
            SELECT id, email, login, name, birthday
            FROM users
            ORDER BY id
            """;
    private static final String UPDATE_USER_SQL = """
            UPDATE users
            SET email = ?, login = ?, name = ?, birthday = ?
            WHERE id = ?
            """;
    private static final String DELETE_USER_LIKES_SQL = "DELETE FROM film_likes WHERE user_id = ?";
    private static final String DELETE_USER_FRIEND_LINKS_SQL = "DELETE FROM friends WHERE friend_id = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM users WHERE id = ?";
    private static final String ADD_FRIEND_SQL = """
            MERGE INTO friends (user_id, friend_id) KEY(user_id, friend_id)
            VALUES (?, ?)
            """;
    private static final String REMOVE_FRIEND_SQL = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS_SQL = "SELECT friend_id FROM friends WHERE user_id = ?";

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, new UserRowMapper());
    }

    @Override
    public User createUser(User user) {
        long id = insert(CREATE_USER_SQL, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public Optional<User> getUserById(long id) {
        Optional<User> user = findOne(GET_USER_BY_ID_SQL, id);
        user.ifPresent(this::loadFriends);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        Collection<User> users = findMany(GET_ALL_USERS_SQL);
        users.forEach(this::loadFriends);
        return users;
    }

    @Override
    public Optional<User> updateUser(User user) {
        int updatedRows = jdbc.update(
                UPDATE_USER_SQL,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        if (updatedRows == 0) {
            return Optional.empty();
        }
        loadFriends(user);
        return Optional.of(user);
    }

    @Override
    public void deleteUserById(long id) {
        jdbc.update(DELETE_USER_LIKES_SQL, id);
        jdbc.update(DELETE_USER_FRIEND_LINKS_SQL, id);
        delete(DELETE_USER_SQL, id);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        update(ADD_FRIEND_SQL, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        jdbc.update(REMOVE_FRIEND_SQL, userId, friendId);
    }

    private void loadFriends(User user) {
        Set<Long> friends = new HashSet<>(jdbc.queryForList(GET_FRIENDS_SQL, Long.class, user.getId()));
        user.setFriends(friends);
    }
}
