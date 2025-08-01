package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class EventController {
    private final EventService eventService;

    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable Long id) {
        return eventService.getEventsByUserId(id);
    }
}