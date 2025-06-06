package ru.yandex.practicum.filmorate.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserManager {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    public Collection<User> getUsers() {
        return users.values();
    }

    public User createUser(User user) {
        log.trace("Вызван метод createUser");
        try {
            if (user.getName() == null) {
                user.setName(user.getLogin());
                log.debug("Имя пользователя не указано, поэтому используем логин как имя");
            }
            user.setId(generateId());
            users.put(user.getId(), user);
            log.trace("Успешно выполнен метод createUser");
            return user;
        } catch (RuntimeException e) {
            log.error("Ошибка в методе createUser: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Optional<User> updateUser(User user) {
        log.trace("Вызван метод updateUser");
        try {
            log.trace("Успешно выполнен метод updateUser");
            return Optional.ofNullable(users.computeIfPresent(user.getId(), (a, b) -> user));
        } catch (RuntimeException e) {
            log.error("Ошибка в методе updateUser: {}", e.getMessage(), e);
            throw e;
        }
    }

    private int generateId() {
        return id++;
    }

}
