package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(
            @Qualifier("inMemoryUserStorage") UserStorage userStorage
    ) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        User createdUser =  userStorage.createUser(user);
        log.info("Created user: {}", createdUser);
        return createdUser;
    }

    public User update(User user) {
        User updatedUser = userStorage.updateUser(user)
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("Updated user: {}", updatedUser);
        return updatedUser;
    }

    public Collection<User> findAll() {
        return userStorage.getAllUsers();
    }

    public User findById(Long id) {
        User user = getUserOrThrow(id);
        log.info("User: {}", user);
        return user;
    }

    public void deleteById(Long id) {
        userStorage.deleteUserById(id);
        log.info("Deleted user: {}", id);
    }

    public void addFriend(Long userId, Long friendId) {
        validateFriendId(userId, friendId);

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        validateFriendId(userId, friendId);

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public Collection<User> getFriends(Long userId) {
        User user = getUserOrThrow(userId);

        return user.getFriends().stream()
                .map(this::getUserOrThrow)
                .toList();
    }

    public Collection<User> getCommonFriends(Long userId,  Long friendId) {
        validateFriendId(userId, friendId);

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        Set<Long> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(friend.getFriends());

        return commonFriends.stream()
                .map(this::getUserOrThrow)
                .toList();
    }

    private void validateFriendId(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("A friend cannot have the same id");
        }
    }

    private User getUserOrThrow(long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }
}
