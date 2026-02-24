package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final Map<Integer, User> userMap = new HashMap<>();
    private int currentId = 0;

    public User create(User user) {
        user.setId(++currentId);

        if (isUserNameEmpty(user)) {
            user.setName(user.getLogin());
        }

        userMap.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        if (userMap.containsKey(user.getId())) {
            userMap.put(user.getId(), user);
            return user;
        }
        throw new NotFoundException("User with id " + user.getId() + " not found");
    }

    public Collection<User> findAll() {
        return userMap.values();
    }

    private boolean isUserNameEmpty(User user) {
        return user.getName() == null || user.getName().isBlank();
    }
}
