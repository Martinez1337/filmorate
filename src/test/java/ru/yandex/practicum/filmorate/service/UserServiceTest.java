package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.mapping.UserMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(new InMemoryUserStorage(), Mappers.getMapper(UserMapper.class));
    }

    @Test
    void create_validUser_assignsIdAndStoresUser() {
        UserDto user = new UserDto();
        user.setEmail("user@example.com");
        user.setLogin("userlogin");

        UserDto created = service.create(user);

        assertEquals(1, created.getId(), "Ожидается id == 1 после создания первого пользователя");
        Collection<UserDto> all = service.findAll();
        assertEquals(1, all.size(), "В хранилище должен быть один пользователь");
        assertTrue(all.contains(created), "Хранилище должно содержать созданного пользователя");
    }

    @Test
    void create_nameNull_setsNameToLogin() {
        UserDto user = new UserDto();
        user.setEmail("a@b.com");
        user.setLogin("alice");
        user.setName(null);

        UserDto created = service.create(user);

        assertEquals("alice", created.getName(), "Если name == null, он должен быть заменён на login");
    }

    @Test
    void create_nameBlank_setsNameToLogin() {
        UserDto user = new UserDto();
        user.setEmail("c@d.com");
        user.setLogin("charlie");
        user.setName("   ");

        UserDto created = service.create(user);

        assertEquals("charlie", created.getName(), "Если name пустая строка, он должен быть заменён на login");
    }

    @Test
    void create_nameNotBlank_keepsProvidedName() {
        UserDto user = new UserDto();
        user.setEmail("bob@example.com");
        user.setLogin("boblogin");
        user.setName("Bob");

        UserDto created = service.create(user);

        assertEquals("Bob", created.getName(), "Если name задан, он не должен перезаписываться");
    }

    @Test
    void update_existingUser_replacesAndReturnsUser() {
        UserDto original = new UserDto();
        original.setEmail("orig@example.com");
        original.setLogin("origlogin");
        UserDto created = service.create(original);
        Long id = created.getId();

        UserDto updated = new UserDto();
        updated.setId(id);
        updated.setEmail("updated@example.com");
        updated.setLogin("updatedlogin");
        updated.setName("Updated");

        UserDto result = service.update(updated);

        assertEquals(updated, result, "Метод update должен вернуть обновлённого пользователя");
        Collection<UserDto> all = service.findAll();
        assertEquals(1, all.size(), "В хранилище должен быть один пользователь после обновления");
        assertTrue(all.contains(updated), "Хранилище должно содержать обновлённого пользователя");
    }

    @Test
    void update_nonExistingUser_throwsNotFoundException() {
        UserDto user = new UserDto();
        user.setId(999L);

        assertThrows(NotFoundException.class, () -> service.update(user),
                "Ожидается NotFoundException при обновлении несуществующего пользователя");
    }

    @Test
    void addFriend_addsFriendOnlyToRequester() {
        UserDto first = new UserDto();
        first.setEmail("first@example.com");
        first.setLogin("first");
        UserDto createdFirst = service.create(first);

        UserDto second = new UserDto();
        second.setEmail("second@example.com");
        second.setLogin("second");
        UserDto createdSecond = service.create(second);

        service.addFriend(createdFirst.getId(), createdSecond.getId());

        assertEquals(1, service.getFriends(createdFirst.getId()).size());
        assertEquals(0, service.getFriends(createdSecond.getId()).size());
    }
}
