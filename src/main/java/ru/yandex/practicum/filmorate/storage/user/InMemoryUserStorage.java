package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> userMap = new HashMap<>();
    private long currentId = 0;

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public Collection<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public Optional<User> updateUser(User user) {
        if (userMap.containsKey(user.getId())) {
            userMap.put(user.getId(), user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public void deleteUserById(long id) {
        userMap.remove(id);
    }

    private long getNextId() {
        return ++currentId;
    }
}
