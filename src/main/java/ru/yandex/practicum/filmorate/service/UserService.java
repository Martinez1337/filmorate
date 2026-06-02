package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.mapping.UserMapper;
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
    private final UserMapper userMapper;

    public UserService(
            @Qualifier("inMemoryUserStorage") UserStorage userStorage,
            UserMapper userMapper
    ) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    public UserDto create(UserDto userDto) {
        User createdUser =  userStorage.createUser(userMapper.map(userDto));
        log.info("Created user: {}", createdUser);
        return userMapper.mapToDto(createdUser);
    }

    public UserDto update(UserDto userDto) {
        User user = userMapper.map(userDto);
        getUserOrThrow(user.getId());
        User updatedUser = userStorage.updateUser(user)
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("Updated user: {}", updatedUser);
        return userMapper.mapToDto(updatedUser);
    }

    public Collection<UserDto> findAll() {
        return userStorage.getAllUsers()
                .stream()
                .map(userMapper::mapToDto)
                .toList();
    }

    public UserDto findById(Long id) {
        User user = getUserOrThrow(id);
        log.info("User: {}", user);
        return userMapper.mapToDto(user);
    }

    public void deleteById(Long id) {
        getUserOrThrow(id);
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

    public Collection<UserDto> getFriends(Long userId) {
        User user = getUserOrThrow(userId);

        return user.getFriends().stream()
                .map(this::getUserOrThrow)
                .map(userMapper::mapToDto)
                .toList();
    }

    public Collection<UserDto> getCommonFriends(Long userId,  Long friendId) {
        validateFriendId(userId, friendId);

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        Set<Long> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(friend.getFriends());

        return commonFriends.stream()
                .map(this::getUserOrThrow)
                .map(userMapper::mapToDto)
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
