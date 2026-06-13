package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.dto.FilmResponseDto;
import ru.yandex.practicum.filmorate.dto.validation.groups.OnUpdate;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public FilmResponseDto createFilm(@Valid @RequestBody FilmRequestDto filmRequestDto) {
        log.info("Received a request to create a movie: {}", filmRequestDto);
        return filmService.create(filmRequestDto);
    }

    @PutMapping
    public FilmResponseDto updateFilm(
            @Validated({Default.class, OnUpdate.class}) @RequestBody FilmRequestDto filmRequestDto
    ) {
        log.info("Received a request to update the movie with id: {}", filmRequestDto.getId());
        return filmService.update(filmRequestDto);
    }

    @GetMapping("/{id}")
    public FilmResponseDto getFilmById(@Positive @PathVariable Long id) {
        log.info("Received a request to get the film with id: {}", id);
        return filmService.findById(id);
    }

    @GetMapping
    public Collection<FilmResponseDto> findAll() {
        log.info("A request for a list of all films has been received");
        return filmService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@Positive @PathVariable Long id) {
        log.info("Received a request to delete the film with id: {}", id);
        filmService.deleteById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilmById(
            @Positive @PathVariable Long id,
            @Positive @PathVariable Long userId
    ) {
        log.info("Received a request to like the film with id: {}", id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilmById(
            @Positive @PathVariable Long id,
            @Positive @PathVariable Long userId
    ) {
        log.info("Received a request to unlike the film with id: {}", id);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmResponseDto> getPopularFilms(
            @RequestParam(defaultValue = "10") @Positive int count
    ) {
        log.info("Received a request to get the popular films has been received");
        return filmService.getPopular(count);
    }
}
