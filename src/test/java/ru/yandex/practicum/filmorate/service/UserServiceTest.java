package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.mapping.UserMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(new FakeUserStorage(), Mappers.getMapper(UserMapper.class));
    }

    @Test
    void create_validUser_assignsIdAndStoresUser() {
        UserDto created = service.create(newUserDto("user@example.com", "userlogin", "User"));

        assertEquals(1L, created.getId(), "Ожидается id == 1 после создания первого пользователя");
        Collection<UserDto> all = service.findAll();
        assertEquals(1, all.size(), "В хранилище должен быть один пользователь");
        assertTrue(all.contains(created), "Хранилище должно содержать созданного пользователя");
    }

    @Test
    void create_nameNull_setsNameToLogin() {
        UserDto created = service.create(newUserDto("a@b.com", "alice", null));

        assertEquals("alice", created.getName(), "Если name == null, он должен быть заменён на login");
    }

    @Test
    void create_nameBlank_setsNameToLogin() {
        UserDto created = service.create(newUserDto("c@d.com", "charlie", "   "));

        assertEquals("charlie", created.getName(), "Если name пустая строка, он должен быть заменён на login");
    }

    @Test
    void create_nameNotBlank_keepsProvidedName() {
        UserDto created = service.create(newUserDto("bob@example.com", "boblogin", "Bob"));

        assertEquals("Bob", created.getName(), "Если name задан, он не должен перезаписываться");
    }

    @Test
    void update_existingUser_replacesAndReturnsUser() {
        UserDto created = service.create(newUserDto("orig@example.com", "origlogin", "Original"));

        UserDto updated = newUserDto("updated@example.com", "updatedlogin", "Updated");
        updated.setId(created.getId());
        UserDto result = service.update(updated);

        assertEquals(updated, result, "Метод update должен вернуть обновлённого пользователя");
        Collection<UserDto> all = service.findAll();
        assertEquals(1, all.size(), "В хранилище должен быть один пользователь после обновления");
        assertTrue(all.contains(updated), "Хранилище должно содержать обновлённого пользователя");
    }

    @Test
    void update_nonExistingUser_throwsNotFoundException() {
        UserDto user = newUserDto("missing@example.com", "missing", "Missing");
        user.setId(999L);

        assertThrows(NotFoundException.class, () -> service.update(user),
                "Ожидается NotFoundException при обновлении несуществующего пользователя");
    }

    @Test
    void addFriend_addsFriendOnlyToRequester() {
        UserDto createdFirst = service.create(newUserDto("first@example.com", "first", "First"));
        UserDto createdSecond = service.create(newUserDto("second@example.com", "second", "Second"));

        service.addFriend(createdFirst.getId(), createdSecond.getId());

        assertEquals(1, service.getFriends(createdFirst.getId()).size());
        assertEquals(0, service.getFriends(createdSecond.getId()).size());
    }

    private UserDto newUserDto(String email, String login, String name) {
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setLogin(login);
        userDto.setName(name);
        userDto.setBirthday(LocalDate.of(1990, 1, 1));
        return userDto;
    }

    private static class FakeUserStorage implements UserStorage {
        private final Map<Long, User> users = new LinkedHashMap<>();
        private long nextId = 1L;

        @Override
        public User createUser(User user) {
            user.setId(nextId++);
            users.put(user.getId(), user);
            return user;
        }

        @Override
        public Optional<User> getUserById(long id) {
            return Optional.ofNullable(users.get(id));
        }

        @Override
        public Collection<User> getAllUsers() {
            return users.values();
        }

        @Override
        public Optional<User> updateUser(User user) {
            if (!users.containsKey(user.getId())) {
                return Optional.empty();
            }
            users.put(user.getId(), user);
            return Optional.of(user);
        }

        @Override
        public void deleteUserById(long id) {
            users.remove(id);
        }

        @Override
        public void addFriend(long userId, long friendId) {
            users.get(userId).getFriends().add(friendId);
        }

        @Override
        public void removeFriend(long userId, long friendId) {
            users.get(userId).getFriends().remove(friendId);
        }

        @Override
        public Collection<User> getUserFriends(long userId) {
            return users.get(userId).getFriends().stream()
                    .map(users::get)
                    .toList();
        }

        @Override
        public Collection<User> getCommonFriends(long userId, long friendId) {
            return users.get(userId).getFriends().stream()
                    .filter(users.get(friendId).getFriends()::contains)
                    .map(users::get)
                    .toList();
        }
    }
}
