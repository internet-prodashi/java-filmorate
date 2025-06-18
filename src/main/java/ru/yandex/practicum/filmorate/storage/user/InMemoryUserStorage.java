package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public Collection<User> getUsers() {
        log.trace("Вызван метод getUsers");
        try {
            log.trace("Успешно выполнен метод getUsers");
            return users.values();
        } catch (RuntimeException e) {
            log.error("Ошибка в методе getUsers: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
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

    @Override
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

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.trace("Вызван метод addFriend");
        try {
            log.trace("Успешно выполнен метод addFriend");
            users.get(userId).getFriends().add(friendId);
            users.get(friendId).getFriends().add(userId);
        } catch (RuntimeException e) {
            log.error("Ошибка в методе addFriend: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<User> getUserById(Long id) {
        log.trace("Вызван метод getUserById");
        try {
            log.trace("Успешно выполнен метод getUserById");
            return Optional.ofNullable(users.get(id));
        } catch (RuntimeException e) {
            log.error("Ошибка в методе getUserById: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        log.trace("Вызван метод removeFriend");
        try {
            log.trace("Успешно выполнен метод removeFriend");
            users.get(userId).getFriends().remove(friendId);
            users.get(friendId).getFriends().remove(userId);
        } catch (RuntimeException e) {
            log.error("Ошибка в методе removeFriend: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Set<Optional<User>> getCommonFriends(Long userIdOne, Long userIdTwo) {
        log.trace("Вызван метод getCommonFriends");
        try {
            log.trace("Успешно выполнен метод getCommonFriends");
            return users.get(userIdOne).getFriends()
                    .stream()
                    .filter(users.get(userIdTwo).getFriends()::contains)
                    .map(this::getUserById)
                    .collect(Collectors.toSet());
        } catch (RuntimeException e) {
            log.error("Ошибка в методе getCommonFriends: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Set<User> getFriends(Long userId) {
        log.trace("Вызван метод getFriends");
        try {
            log.trace("Успешно выполнен метод getFriends");
            return users.get(userId).getFriends()
                    .stream()
                    .map(users::get)
                    .collect(Collectors.toSet());
        } catch (RuntimeException e) {
            log.error("Ошибка в методе getFriends: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Long generateId() {
        return id++;
    }

}
