package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.groups.OnUpdate;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Received a request to create a user: {}", user);
        return userService.create(user);
    }

    @PutMapping
    public User updateUser(@Validated(OnUpdate.class) @RequestBody User user) {
        log.info("Received a request to update the user with id: {}", user.getId());
        return userService.update(user);
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("A request for a list of all users has been received");
        return userService.findAll();
    }
}
