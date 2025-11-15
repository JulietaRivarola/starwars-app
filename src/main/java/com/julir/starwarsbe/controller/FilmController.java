package com.julir.starwarsbe.controller;

import com.julir.starwarsbe.dto.PagedResponse;
import com.julir.starwarsbe.service.FilmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public PagedResponse<Object> getAll(
            @RequestParam(required = false) String title
    ) {
        return filmService.getAll(title);
    }

    @GetMapping("/{id}")
    public Object getById(@PathVariable String id) {
        return filmService.getById(id);
    }
}