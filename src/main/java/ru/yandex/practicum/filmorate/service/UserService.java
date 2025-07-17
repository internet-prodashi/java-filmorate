package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExceptionNotFound;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user)
                .orElseThrow(() -> new ExceptionNotFound("Пользователь с идентификатором '%d' не найден".formatted(user.getId())));
    }

    public void addFriend(Long userId, Long friendId) {
        if (getUserById(userId).getId() > 0 && getUserById(friendId).getId() > 0) {
            userStorage.addFriend(userId, friendId);
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        if (getUserById(userId).getId() > 0 && getUserById(friendId).getId() > 0) {
            userStorage.removeFriend(userId, friendId);
        }
    }

    public Set<Optional<User>> getCommonFriends(Long userIdOne, Long userIdTwo) {
        return userStorage.getCommonFriends(userIdOne, userIdTwo);
    }

    public Set<User> getFriends(Long userId) {
        if (getUserById(userId).getId() > 0) {
            return userStorage.getFriends(userId);
        }
        return null;
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new ExceptionNotFound("Пользователь с идентификатором '%d' не найден".formatted(id)));
    }

}
