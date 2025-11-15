package com.julir.starwarsbe.controller;

import com.julir.starwarsbe.dto.PagedResponse;
import com.julir.starwarsbe.service.StarshipsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/starships")
@RequiredArgsConstructor
public class StarshipsController {

    private final StarshipsService starshipsService;

    @GetMapping
    public PagedResponse<Object> getAll(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Boolean expanded
    ) {
        return starshipsService.getAll(page, limit, name, model, expanded);
    }

    @GetMapping("/{id}")
    public Object getById(@PathVariable String id) {
        return starshipsService.getById(id);
    }
}
