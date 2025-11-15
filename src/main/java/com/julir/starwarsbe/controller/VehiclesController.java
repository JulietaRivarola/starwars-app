package com.julir.starwarsbe.controller;

import com.julir.starwarsbe.dto.PagedResponse;
import com.julir.starwarsbe.service.VehiclesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehiclesController {

    private final VehiclesService vehiclesService;

    @GetMapping
    public PagedResponse<Object> getAll(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String model
    ) {
        return vehiclesService.getAll(page, limit, name, model);
    }

    @GetMapping("/{id}")
    public Object getById(@PathVariable String id) {
        return vehiclesService.getById(id);
    }
}
