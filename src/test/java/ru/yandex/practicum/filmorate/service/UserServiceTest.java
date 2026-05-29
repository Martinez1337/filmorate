package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(new InMemoryUserStorage());
    }

    @Test
    void create_validUser_assignsIdAndStoresUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userlogin");

        User created = service.create(user);

        assertEquals(1, created.getId(), "Ожидается id == 1 после создания первого пользователя");
        assertSame(user, created, "Метод create должен возвращать тот же объект пользователя");
        Collection<User> all = service.findAll();
        assertEquals(1, all.size(), "В хранилище должен быть один пользователь");
        assertTrue(all.contains(created), "Хранилище должно содержать созданного пользователя");
    }

    @Test
    void create_nameNull_keepsNameNull() {
        User user = new User();
        user.setEmail("a@b.com");
        user.setLogin("alice");
        user.setName(null);

        User created = service.create(user);

        assertNull(created.getName(), "Сервис должен сохранять пользователя без подстановки name");
    }

    @Test
    void create_nameBlank_keepsProvidedName() {
        User user = new User();
        user.setEmail("c@d.com");
        user.setLogin("charlie");
        user.setName("   ");

        User created = service.create(user);

        assertEquals("   ", created.getName(), "Сервис должен сохранять переданное значение name");
    }

    @Test
    void create_nameNotBlank_keepsProvidedName() {
        User user = new User();
        user.setEmail("bob@example.com");
        user.setLogin("boblogin");
        user.setName("Bob");

        User created = service.create(user);

        assertEquals("Bob", created.getName(), "Если name задан, он не должен перезаписываться");
    }

    @Test
    void update_existingUser_replacesAndReturnsUser() {
        User original = new User();
        original.setEmail("orig@example.com");
        original.setLogin("origlogin");
        service.create(original);
        Long id = original.getId();

        User updated = new User();
        updated.setId(id);
        updated.setEmail("updated@example.com");
        updated.setLogin("updatedlogin");
        updated.setName("Updated");

        User result = service.update(updated);

        assertSame(updated, result, "Метод update должен вернуть переданный объект");
        Collection<User> all = service.findAll();
        assertEquals(1, all.size(), "В хранилище должен быть один пользователь после обновления");
        assertTrue(all.contains(updated), "Хранилище должно содержать обновлённого пользователя");
    }

    @Test
    void update_nonExistingUser_throwsNotFoundException() {
        User user = new User();
        user.setId(999L);

        assertThrows(NotFoundException.class, () -> service.update(user),
                "Ожидается NotFoundException при обновлении несуществующего пользователя");
    }
}
