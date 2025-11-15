package com.julir.starwarsbe.service;

import com.julir.starwarsbe.dto.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FilmService {

    public static final String FILMS = "films";
    private final SwapiService swapiService;

    public PagedResponse<Object> getAll(String title) {
        Map<String, String> searchParams = new HashMap<>();
        if (title != null && !title.isBlank()) {
            searchParams.put("title", title);
        }
        return swapiService.getList(FILMS, null, null, searchParams.isEmpty() ? null : searchParams, null);
    }

    public Object getById(String id) {
        return swapiService.getById(FILMS, id);
    }
}