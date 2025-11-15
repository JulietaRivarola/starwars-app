package com.julir.starwarsbe.service;

import com.julir.starwarsbe.dto.PagedResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeopleServiceTest {

    @Mock
    private SwapiService swapiService;

    @InjectMocks
    private PeopleService peopleService;

    @Test
    void getAllWithCompleteParams() {
        PagedResponse<Object> expectedResponse = PagedResponse.<Object>builder()
                .message("ok")
                .totalRecords(1)
                .totalPages(1)
                .results(List.of())
                .build();

        Map<String, String> searchParams = Map.of("name", "Luke");

        when(swapiService.getList("people", 1, 10, searchParams, null)).thenReturn(expectedResponse);

        PagedResponse<Object> result = peopleService.getAll(1, 10, "Luke");

        assertEquals(expectedResponse, result);
        verify(swapiService).getList("people", 1, 10, searchParams, null);
    }

    @Test
    void getAllWithNullParams() {
        PagedResponse<Object> expectedResponse = PagedResponse.<Object>builder()
                .message("ok")
                .results(List.of())
                .build();

        when(swapiService.getList("people", null, null, null, null))
                .thenReturn(expectedResponse);

        PagedResponse<Object> result = peopleService.getAll(null, null, null);

        assertEquals(expectedResponse, result);
        verify(swapiService).getList("people", null, null, null, null);
    }

    @Test
    void getAllFilteredByName() {
        PagedResponse<Object> expectedResponse = PagedResponse.<Object>builder()
                .message("ok")
                .results(List.of(Map.of("name", "Darth Vader")))
                .build();

        Map<String, String> searchParams = Map.of("name", "Vader");
        when(swapiService.getList("people", null, null, searchParams, null))
                .thenReturn(expectedResponse);

        PagedResponse<Object> result = peopleService.getAll(null, null, "Vader");

        assertEquals(expectedResponse, result);
        verify(swapiService).getList("people", null, null, searchParams, null);
    }

    @Test
    void getByIdReturnsCharacter() {
        Map<String, Object> expectedResponse = Map.of("name", "Luke Skywalker");
        when(swapiService.getById("people", "1")).thenReturn(expectedResponse);

        Object result = peopleService.getById("1");

        assertEquals(expectedResponse, result);
        verify(swapiService).getById("people", "1");
    }

    @Test
    void getAllIgnoresBlankName() {
        PagedResponse<Object> expectedResponse = PagedResponse.<Object>builder()
                .message("ok")
                .results(List.of())
                .build();

        when(swapiService.getList("people", 1, 10, null, null))
                .thenReturn(expectedResponse);

        PagedResponse<Object> result = peopleService.getAll(1, 10, "   ");

        assertEquals(expectedResponse, result);
        verify(swapiService).getList("people", 1, 10, null, null);
    }
}
