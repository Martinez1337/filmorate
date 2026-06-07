package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User createUser(User user);

    Optional<User> getUserById(long id);

    Collection<User> getAllUsers();

    Optional<User> updateUser(User user);

    void deleteUserById(long id);

    default void addFriend(long userId, long friendId) {
        getUserById(userId).ifPresent(user -> user.getFriends().add(friendId));
    }

    default void removeFriend(long userId, long friendId) {
        getUserById(userId).ifPresent(user -> user.getFriends().remove(friendId));
    }
}
