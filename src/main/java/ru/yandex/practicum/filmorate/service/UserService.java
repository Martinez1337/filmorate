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
            @Qualifier("userDbStorage") UserStorage userStorage,
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

    public UserDto findById(long id) {
        User user = getUserOrThrow(id);
        log.info("User: {}", user);
        return userMapper.mapToDto(user);
    }

    public void deleteById(long id) {
        getUserOrThrow(id);
        userStorage.deleteUserById(id);
        log.info("Deleted user: {}", id);
    }

    public void addFriend(long userId, long friendId) {
        validateFriendId(userId, friendId);

        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        validateFriendId(userId, friendId);

        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        userStorage.removeFriend(userId, friendId);
    }

    public Collection<UserDto> getFriends(long userId) {
        User user = getUserOrThrow(userId);

        return user.getFriends().stream()
                .map(this::getUserOrThrow)
                .map(userMapper::mapToDto)
                .toList();
    }

    public Collection<UserDto> getCommonFriends(long userId,  long friendId) {
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

    private void validateFriendId(long userId, long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("A friend cannot have the same id");
        }
    }

    private User getUserOrThrow(long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }
}
