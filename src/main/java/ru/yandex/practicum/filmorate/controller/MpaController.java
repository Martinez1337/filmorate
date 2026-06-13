package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<MpaDto> findAll() {
        log.info("A request for a list of all mpa ratings has been received");
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public MpaDto getMpaById(@Positive @PathVariable Long id) {
        log.info("Received a request to get the mpa with id: {}", id);
        return mpaService.findById(id);
    }
}
