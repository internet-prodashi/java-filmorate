package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    Collection<User> getUsers();

    User createUser(User user);

    Optional<User> updateUser(User user);

    void addFriend(Long userId, Long friendId);

    Optional<User> getUserById(Long id);

    void removeFriend(Long userId, Long friendId);

    Set<Optional<User>> getCommonFriends(Long userIdOne, Long userIdTwo);

    Set<User> getFriends(Long ownerId);

}


