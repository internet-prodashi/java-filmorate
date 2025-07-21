package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = {"ru.yandex.practicum.filmorate.storage"})
class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

    private User createUser(String login, String name) {
        User user = new User();
        user.setEmail("email@mail.ru");
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(LocalDate.of(1990, 2, 5));
        return user;
    }

    @Test
    void addAndFindUser() {
        User user1 = createUser("Логин №1", "Имя №1");
        User savedUser = userDbStorage.createUser(user1);
        Optional<User> getUserfromDb = userDbStorage.getUserById(savedUser.getId());

        assertThat(savedUser.getId()).isNotNull();
        assertThat(getUserfromDb).isPresent();
        assertThat(getUserfromDb.get().getLogin()).isEqualTo("Логин №1");
        assertThat(getUserfromDb.get().getName()).isEqualTo("Имя №1");

        Optional<User> user = userDbStorage.getUserById(100L);
        assertThat(user).isEmpty();
    }

    @Test
    void findAllUsers() {
        User user2 = createUser("Логин №2", "Имя №2");
        userDbStorage.createUser(user2);
        User user3 = createUser("Логин №3", "Имя №3");
        userDbStorage.createUser(user3);
        User user4 = createUser("Логин №4", "Имя №4");
        userDbStorage.createUser(user4);

        Collection<User> users = userDbStorage.getUsers();
        assertThat(users).hasSize(3);
    }

    @Test
    void updateUser() {
        User user5 = createUser("Логин №5", "Имя №5");
        userDbStorage.createUser(user5);
        user5.setLogin("Логин №5 новый");
        user5.setName("Имя №5 новое");
        Optional<User> updatedUser = userDbStorage.updateUser(user5);
        Optional<User> loadedUser = userDbStorage.getUserById(user5.getId());

        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getLogin()).isEqualTo("Логин №5 новый");
        assertThat(updatedUser.get().getName()).isEqualTo("Имя №5 новое");
        assertThat(loadedUser).isPresent();
        assertThat(loadedUser.get().getLogin()).isEqualTo("Логин №5 новый");
        assertThat(loadedUser.get().getName()).isEqualTo("Имя №5 новое");

        User user6 = createUser("Логин №6", "Имя №6");
        userDbStorage.createUser(user6);
        user6.setId(200L);
        Optional<User> result = userDbStorage.updateUser(user6);
        assertThat(result).isEmpty();
    }

    @Test
    void addAndDeleteFriendship() {
        User user7 = createUser("Логин №7", "Имя №7");
        userDbStorage.createUser(user7);
        User user8 = createUser("Логин №8", "Имя №8");
        userDbStorage.createUser(user8);
        User user9 = createUser("Логин №9", "Имя №9");
        userDbStorage.createUser(user9);
        userDbStorage.addFriend(user7.getId(), user8.getId());
        userDbStorage.addFriend(user9.getId(), user8.getId());
        userDbStorage.addFriend(user7.getId(), user9.getId());

        Set<User> friendsOfOne = userDbStorage.getFriends(user7.getId());
        assertThat(friendsOfOne.size()).isEqualTo(2L);

        userDbStorage.removeFriend(user7.getId(), user8.getId());

        Set<User> friendsOfOneNewSet = userDbStorage.getFriends(user7.getId());
        assertThat(friendsOfOneNewSet.size()).isEqualTo(1L);
    }

    @Test
    void findCommonFriends() {
        User user10 = createUser("Логин №10", "Имя №10");
        userDbStorage.createUser(user10);
        User user11 = createUser("Логин №11", "Имя №11");
        userDbStorage.createUser(user11);
        User user12 = createUser("Логин №12", "Имя №12");
        userDbStorage.createUser(user12);
        userDbStorage.addFriend(user10.getId(), user11.getId());
        userDbStorage.addFriend(user12.getId(), user11.getId());
        userDbStorage.addFriend(user10.getId(), user12.getId());

        Set<Optional<User>> commonFriends = userDbStorage.getCommonFriends(user10.getId(), user12.getId());
        assertThat(commonFriends.size()).isEqualTo(1L);
    }

}
