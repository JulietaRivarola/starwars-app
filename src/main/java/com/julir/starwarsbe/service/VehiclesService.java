package com.julir.starwarsbe.service;

import com.julir.starwarsbe.dto.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VehiclesService {

    public static final String VEHICLES = "vehicles";
    private final SwapiService swapiService;

    public PagedResponse<Object> getAll(Integer page, Integer limit, String name, String model) {
        Map<String, String> searchParams = new HashMap<>();
        if (name != null && !name.isBlank()) {
            searchParams.put("name", name);
        }
        if (model != null && !model.isBlank()) {
            searchParams.put("model", model);
        }
        return swapiService.getList(VEHICLES, page, limit, searchParams.isEmpty() ? null : searchParams, null);
    }

    public Object getById(String id) {
        return swapiService.getById(VEHICLES, id);
    }
}
