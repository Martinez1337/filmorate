package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void postUsers_emptyBody_returnsBadRequest() throws Exception {
        String emptyJson = "{}";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation constraints violation"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void postUsers_invalidFields_returnsBadRequest() throws Exception {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("with space");
        user.setBirthday(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation constraints violation"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void postUsers_valid_returnsOkAndUserReturned() throws Exception {
        User request = new User();
        request.setEmail("user@example.com");
        request.setLogin("userlogin");
        request.setName("User");
        request.setBirthday(LocalDate.now());

        User created = new User();
        created.setId(1);
        created.setEmail(request.getEmail());
        created.setLogin(request.getLogin());
        created.setName(request.getName());
        created.setBirthday(request.getBirthday());

        when(userService.create(any(User.class))).thenReturn(created);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("User"));
    }

    @Test
    void putUsers_missingId_returnsBadRequest() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userlogin");
        user.setName("User");

        mockMvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation constraints violation"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void putUsers_invalidFields_returnsBadRequest() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("invalid-email");
        user.setLogin("with space");

        mockMvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation constraints violation"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void getUsers_noUsers_returnsEmptyList() throws Exception {
        when(userService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getUsers_withUsers_returnsList() throws Exception {
        User u = new User();
        u.setId(1);
        u.setEmail("a@b.com");
        u.setLogin("login");
        u.setName("Name");

        when(userService.findAll()).thenReturn(List.of(u));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("a@b.com"));
    }
}
