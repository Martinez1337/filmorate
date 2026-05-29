package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.mapping.UserMapper;
import ru.yandex.practicum.filmorate.dto.validation.groups.OnUpdate;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Received a request to create a user: {}", userDto);
        return userMapper.mapToDto(userService.create(userMapper.map(userDto)));
    }

    @PutMapping
    public UserDto updateUser(@Validated({Default.class, OnUpdate.class}) @RequestBody UserDto userDto) {
        log.info("Received a request to update the user with id: {}", userDto.getId());
        return userMapper.mapToDto(userService.update(userMapper.map(userDto)));
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@Positive @PathVariable Long id) {
        log.info("Received a request to get the user with id: {}", id);
        return userMapper.mapToDto(userService.findById(id));
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        log.info("A request for a list of all users has been received");
        return userService.findAll()
                .stream()
                .map(userMapper::mapToDto)
                .toList();
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@Positive @PathVariable Long id) {
        log.info("Received a request to delete the user with id: {}", id);
        userService.deleteById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriendToUser(
            @Positive @PathVariable Long id,
            @Positive @PathVariable Long friendId
    ) {
        log.info("Received a request to add the friend to the user with id: {}", id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriendFromUser(
            @Positive @PathVariable Long id,
            @Positive @PathVariable Long friendId
    ) {
        log.info("Received a request to remove the friend from the user with id: {}", id);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> getUserFriends(@Positive @PathVariable Long id) {
        log.info("Received a request to get the user's friends with id: {}", id);
        return userService.getFriends(id).stream()
                .map(userMapper::mapToDto)
                .toList();
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getUserCommonFriends(
            @Positive @PathVariable Long id,
            @Positive @PathVariable Long otherId
    ) {
        log.info("Received a request to get the user's {} common friends with other user id: {}", id, otherId);
        return userService.getCommonFriends(id, otherId).stream()
                .map(userMapper::mapToDto)
                .toList();
    }
}
