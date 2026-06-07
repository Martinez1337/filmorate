package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageIntegrationTest {
    private final UserDbStorage userStorage;

    @Test
    void createAndGetUserById_returnsCreatedUser() {
        User created = userStorage.createUser(newUser("user@example.com", "user", "User"));

        assertThat(userStorage.getUserById(created.getId()))
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isEqualTo(created.getId());
                    assertThat(user.getEmail()).isEqualTo("user@example.com");
                    assertThat(user.getLogin()).isEqualTo("user");
                    assertThat(user.getName()).isEqualTo("User");
                    assertThat(user.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
                });
    }

    @Test
    void getAllUsers_returnsCreatedUsersOrderedById() {
        User first = userStorage.createUser(newUser("first@example.com", "first", "First"));
        User second = userStorage.createUser(newUser("second@example.com", "second", "Second"));

        assertThat(userStorage.getAllUsers())
                .extracting("id")
                .containsExactly(first.getId(), second.getId());
    }

    @Test
    void updateUser_existingUser_updatesUserData() {
        User created = userStorage.createUser(newUser("old@example.com", "old", "Old"));
        created.setEmail("new@example.com");
        created.setLogin("new");
        created.setName("New");
        created.setBirthday(LocalDate.of(1995, 5, 5));

        assertThat(userStorage.updateUser(created))
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getEmail()).isEqualTo("new@example.com");
                    assertThat(user.getLogin()).isEqualTo("new");
                    assertThat(user.getName()).isEqualTo("New");
                    assertThat(user.getBirthday()).isEqualTo(LocalDate.of(1995, 5, 5));
                });
    }

    @Test
    void addAndRemoveFriend_updatesOnlyRequesterFriends() {
        User user = userStorage.createUser(newUser("user@example.com", "user", "User"));
        User friend = userStorage.createUser(newUser("friend@example.com", "friend", "Friend"));

        userStorage.addFriend(user.getId(), friend.getId());

        assertThat(userStorage.getUserById(user.getId()))
                .isPresent()
                .hasValueSatisfying(foundUser -> assertThat(foundUser.getFriends()).containsExactly(friend.getId()));
        assertThat(userStorage.getUserById(friend.getId()))
                .isPresent()
                .hasValueSatisfying(foundFriend -> assertThat(foundFriend.getFriends()).isEmpty());

        userStorage.removeFriend(user.getId(), friend.getId());

        assertThat(userStorage.getUserById(user.getId()))
                .isPresent()
                .hasValueSatisfying(foundUser -> assertThat(foundUser.getFriends()).isEmpty());
    }

    @Test
    void deleteUserById_removesUser() {
        User created = userStorage.createUser(newUser("user@example.com", "user", "User"));

        userStorage.deleteUserById(created.getId());

        assertThat(userStorage.getUserById(created.getId())).isEmpty();
    }

    private User newUser(String email, String login, String name) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}
