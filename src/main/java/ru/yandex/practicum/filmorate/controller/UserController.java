package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.manager.InMemoryUserManager;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserManager inMemoryUserManager;

    @GetMapping
    public Collection<User> getUsers() {
        return inMemoryUserManager.getUsers();
    }

    @PostMapping
    public User createFilm(@Valid @RequestBody User user) {
        return inMemoryUserManager.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return inMemoryUserManager.updateUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
