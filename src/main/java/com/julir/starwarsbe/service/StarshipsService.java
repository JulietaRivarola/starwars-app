package com.julir.starwarsbe.service;

import com.julir.starwarsbe.dto.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StarshipsService {

    public static final String STARSHIPS = "starships";
    private final SwapiService swapiService;

    public PagedResponse<Object> getAll(Integer page, Integer limit, String name, String model, Boolean expanded) {
        Map<String, String> searchParams = new HashMap<>();
        if (name != null && !name.isBlank()) {
            searchParams.put("name", name);
        }
        if (model != null && !model.isBlank()) {
            searchParams.put("model", model);
        }
        return swapiService.getList(STARSHIPS, page, limit, searchParams.isEmpty() ? null : searchParams, expanded);
    }

    public Object getById(String id) {
        return swapiService.getById(STARSHIPS, id);
    }
}
