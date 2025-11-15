package com.julir.starwarsbe.service;

import com.julir.starwarsbe.dto.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PeopleService {

    public static final String PEOPLE = "people";
    private final SwapiService swapiService;

    public PagedResponse<Object> getAll(Integer page, Integer limit, String name) {
        Map<String, String> searchParams = new HashMap<>();
        if (name != null && !name.isBlank()) {
            searchParams.put("name", name);
        }
        return swapiService.getList(PEOPLE, page, limit, searchParams.isEmpty() ? null : searchParams, null);
    }

    public Object getById(String id) {
        return swapiService.getById(PEOPLE, id);
    }
}