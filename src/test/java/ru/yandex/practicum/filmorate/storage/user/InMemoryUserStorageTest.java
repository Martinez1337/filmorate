package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {
    private InMemoryUserStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryUserStorage();
    }

    @Test
    void createUser_assignsIncrementalIdAndStoresUser() {
        User first = new User();
        User second = new User();

        User createdFirst = storage.createUser(first);
        User createdSecond = storage.createUser(second);

        assertSame(first, createdFirst);
        assertSame(second, createdSecond);
        assertEquals(1L, createdFirst.getId());
        assertEquals(2L, createdSecond.getId());
        assertEquals(Optional.of(first), storage.getUserById(1L));
        assertEquals(Optional.of(second), storage.getUserById(2L));
    }

    @Test
    void getUserById_unknownId_returnsEmptyOptional() {
        Optional<User> result = storage.getUserById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllUsers_returnsStoredUsers() {
        User first = storage.createUser(new User());
        User second = storage.createUser(new User());

        Collection<User> result = storage.getAllUsers();

        assertEquals(2, result.size());
        assertTrue(result.contains(first));
        assertTrue(result.contains(second));
    }

    @Test
    void updateUser_existingId_replacesAndReturnsUser() {
        User original = storage.createUser(new User());
        User updated = new User();
        updated.setId(original.getId());
        updated.setEmail("updated@example.com");

        Optional<User> result = storage.updateUser(updated);

        assertEquals(Optional.of(updated), result);
        assertEquals(Optional.of(updated), storage.getUserById(original.getId()));
    }

    @Test
    void updateUser_unknownId_returnsEmptyOptional() {
        User user = new User();
        user.setId(999L);

        Optional<User> result = storage.updateUser(user);

        assertTrue(result.isEmpty());
        assertTrue(storage.getAllUsers().isEmpty());
    }

    @Test
    void deleteUserById_existingId_removesUser() {
        User user = storage.createUser(new User());

        storage.deleteUserById(user.getId());

        assertTrue(storage.getUserById(user.getId()).isEmpty());
        assertTrue(storage.getAllUsers().isEmpty());
    }
}
