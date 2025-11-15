package com.julir.starwarsbe.service;

import com.julir.starwarsbe.dto.PagedResponse;
import com.julir.starwarsbe.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SwapiService {

    private final RestClient swapiClient;

    public PagedResponse<Object> getList(
            String resource,
            Integer page,
            Integer limit,
            Map<String, String> filters,
            Boolean expanded
    ) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/" + resource + "/");

        if (page != null) {
            uriBuilder.queryParam("page", page);
        }
        if (limit != null) {
            uriBuilder.queryParam("limit", limit);
        }
        if (Boolean.TRUE.equals(expanded)) {
            uriBuilder.queryParam("expanded", true);
        }

        boolean hasFilters = filters != null && !filters.isEmpty();
        if (hasFilters) {
            filters.forEach((key, value) -> {
                if (value != null && !value.isBlank()) {
                    uriBuilder.queryParam(key, value);
                }
            });
        }

        Map<String, Object> response = swapiClient.get()
                .uri(uriBuilder.toUriString())
                .retrieve()
                .body(Map.class);

        if (response == null) {
            return emptyResponse();
        }

        List<Object> results = extractResults(response);

        boolean swapiIgnoresPagination = hasFilters || Boolean.TRUE.equals(expanded);

        if (swapiIgnoresPagination && (page != null || limit != null)) {
            return paginateManually(response, results, page, limit);
        }

        return PagedResponse.<Object>builder()
                .message((String) response.getOrDefault("message", "ok"))
                .totalRecords(getIntValue(response.get("total_records"), results.size()))
                .totalPages(getIntValue(response.get("total_pages"), 1))
                .previous((String) response.get("previous"))
                .next((String) response.get("next"))
                .results(results)
                .build();
    }

    public Object getById(String resource, String id) {
        return swapiClient.get()
                .uri("/" + resource + "/" + id + "/")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    if (response.getStatusCode().value() == 404) {
                        throw new ResourceNotFoundException(
                                "Resource not found: " + resource + " with id " + id
                        );
                    }
                })
                .body(Object.class);
    }

    private PagedResponse<Object> paginateManually(
            Map<String, Object> response,
            List<Object> fullResults,
            Integer page,
            Integer limit
    ) {
        int currentPage = (page != null && page > 0) ? page : 1;
        int pageSize = (limit != null && limit > 0) ? limit : 10;

        int totalItems = fullResults.size();
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);

        List<Object> paginated = startIndex < totalItems
                ? fullResults.subList(startIndex, endIndex)
                : new ArrayList<>();

        return PagedResponse.<Object>builder()
                .message((String) response.getOrDefault("message", "ok"))
                .totalRecords(totalItems)
                .totalPages((int) Math.ceil((double) totalItems / pageSize))
                .previous(currentPage > 1 ? "page=" + (currentPage - 1) : null)
                .next(endIndex < totalItems ? "page=" + (currentPage + 1) : null)
                .results(paginated)
                .build();
    }

    private List<Object> extractResults(Map<String, Object> response) {
        if (response.containsKey("results")) {
            Object list = response.get("results");
            return list instanceof List ? (List<Object>) list : new ArrayList<>();
        }

        if (response.containsKey("result")) {
            Object result = response.get("result");
            return result instanceof List
                    ? (List<Object>) result
                    : List.of(result);
        }

        return new ArrayList<>();
    }

    private int getIntValue(Object value, int defaultValue) {
        return value instanceof Number number ? number.intValue() : defaultValue;
    }

    private PagedResponse<Object> emptyResponse() {
        return PagedResponse.<Object>builder()
                .message("error")
                .totalRecords(0)
                .totalPages(0)
                .results(new ArrayList<>())
                .build();
    }
}
