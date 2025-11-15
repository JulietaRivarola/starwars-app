package com.julir.starwarsbe.controller;

import com.julir.starwarsbe.dto.PagedResponse;
import com.julir.starwarsbe.service.PeopleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/people")
@RequiredArgsConstructor
public class PeopleController {

    private final PeopleService peopleService;

    @GetMapping
    public PagedResponse<Object> getAll(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String name
    ) {
        return peopleService.getAll(page, limit, name);
    }

    @GetMapping("/{id}")
    public Object getById(@PathVariable String id) {
        return peopleService.getById(id);
    }
}