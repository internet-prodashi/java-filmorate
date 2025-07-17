package ru.yandex.practicum.filmorate.storage.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userRowMapper;

    @Override
    public Collection<User> getUsers() {
        String query = """
                SELECT user_id, email, login, name, birthday
                FROM users
                """;
        return jdbcTemplate.query(query, userRowMapper);
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);
        //log.trace("keyHolder - "+keyHolder.getKeyAs(Long.class));
        Long id = keyHolder.getKeyAs(Long.class);
        if (id == null) {
            throw new IllegalStateException("Не удалось сгенерировать идентификатор пользователя");
        }
        user.setId(id);
        return user;
    }

    @Override
    public Optional<User> updateUser(User user) {
        String query = """
                UPDATE users
                SET email = ?, login = ?, name = ?, birthday = ?
                WHERE user_id = ?
                """;
        int countRows = jdbcTemplate.update(query,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        if (countRows > 0) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        String query = """
                SELECT user_id, email, login, name, birthday
                FROM users
                WHERE user_id = ?
                """;
        List<User> users = jdbcTemplate.query(query, userRowMapper, id);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(users.getFirst());
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String query = "INSERT INTO friendships(user_id, user_id_friend) VALUES (?, ?)";
        jdbcTemplate.update(query, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String query = "DELETE FROM friendships WHERE user_id = ? AND user_id_friend = ?";
        jdbcTemplate.update(query, userId, friendId);
    }

    @Override
    public Set<Optional<User>> getCommonFriends(Long userIdOne, Long userIdTwo) {
        String query = """
                SELECT u.user_id, u.email, u.login, u.name, u.birthday
                FROM friendships f1
                JOIN friendships f2 ON f1.user_id_friend = f2.user_id_friend
                JOIN users u ON u.user_id = f1.user_id_friend
                WHERE f1.user_id = ? AND f2.user_id = ?
                """;
        return jdbcTemplate.query(query, userRowMapper, userIdOne, userIdTwo)
                .stream()
                .map(Optional::ofNullable)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<User> getFriends(Long userId) {
        String query = """
                SELECT u.user_id, u.email, u.login, u.name, u.birthday
                FROM friendships f
                JOIN users u ON u.user_id = f.user_id_friend
                WHERE f.user_id = ?
                """;
        return new HashSet<>(jdbcTemplate.query(query, userRowMapper, userId));
    }

}
