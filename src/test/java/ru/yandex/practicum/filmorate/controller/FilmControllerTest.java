package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

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

@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;

    @Test
    void postFilms_emptyBody_returnsBadRequest() throws Exception {
        String emptyJson = "{}";

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation constraints violation"))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void postFilms_invalidFields_returnsBadRequest() throws Exception {
        Film film = new Film();
        film.setName("");
        film.setDescription("x".repeat(201));
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(0);

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation constraints violation"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void postFilms_valid_returnsOkAndFilmReturned() throws Exception {
        Film request = new Film();
        request.setName("A Movie");
        request.setDescription("Desc");
        request.setReleaseDate(LocalDate.of(2000, 1, 1));
        request.setDuration(120);

        Film created = new Film();
        created.setId(1);
        created.setName(request.getName());
        created.setDescription(request.getDescription());
        created.setReleaseDate(request.getReleaseDate());
        created.setDuration(request.getDuration());

        when(filmService.create(any(Film.class))).thenReturn(created);

        mockMvc.perform(post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("A Movie"));
    }

    @Test
    void putFilms_missingId_returnsBadRequest() throws Exception {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        mockMvc.perform(put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation constraints violation"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void putFilms_invalidFields_returnsBadRequest() throws Exception {
        Film film = new Film();
        film.setId(1);
        film.setName("");
        film.setDescription("x".repeat(201));
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(0);

        mockMvc.perform(put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation constraints violation"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void getFilms_noFilms_returnsEmptyList() throws Exception {
        when(filmService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getFilms_withFilms_returnsList() throws Exception {
        Film f = new Film();
        f.setId(1);
        f.setName("Movie");
        f.setDescription("Desc");
        f.setReleaseDate(LocalDate.of(2000,1,1));
        f.setDuration(100);

        when(filmService.findAll()).thenReturn(List.of(f));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Movie"));
    }
}
