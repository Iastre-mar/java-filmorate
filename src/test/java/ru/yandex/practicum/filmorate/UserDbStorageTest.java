package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FriendshipMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, FriendshipMapper.class})
@Sql(scripts = {"/schema.sql", "/test-users-data.sql"},
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;


    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM friendships");
        jdbcTemplate.update("DELETE FROM users");

        jdbcTemplate.update(
                "INSERT INTO users (id, login, email, name, birthday) VALUES " +
                "(1, 'user1', 'user1@example.com', 'User One', '1990-01-01')," +
                "(2, 'user2', 'user2@example.com', 'User Two', '1995-05-15')," +
                "(3, 'user3', 'user3@example.com', 'User Three', '2000-10-20')");

        jdbcTemplate.update(
                "INSERT INTO friendships (user_id, friend_id) VALUES " +
                "(1, 2)," +
                "(1, 3)," +
                "(2, 3)");
    }

    @Test
    void getAll_shouldReturnAllUsers() {
        Collection<User> users = userDbStorage.getAll();

        assertThat(users).hasSize(3);

        User user1 = users.stream()
                          .filter(u -> u.getId() == 1)
                          .findFirst()
                          .get();
        assertThat(user1.getFriendships()).hasSize(2);
    }

    @Test
    void get_shouldReturnUserByIdWithFriendships() {
        Optional<User> userOptional = userDbStorage.get(1L);

        assertThat(userOptional).isPresent();

        User user = userOptional.get();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getLogin()).isEqualTo("user1");
        assertThat(user.getEmail()).isEqualTo("user1@example.com");
        assertThat(user.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));

        Set<Friendship> friendships = user.getFriendships();
        assertThat(friendships).hasSize(2);
        assertThat(friendships).extracting(Friendship::getFriendId)
                               .containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    void persist_shouldSaveUserWithFriendships() {
        User newUser = new User();
        newUser.setLogin("new_user");
        newUser.setEmail("new@example.com");
        newUser.setName("New User");
        newUser.setBirthday(LocalDate.of(2000, 1, 1));

        Friendship friendship = new Friendship();
        friendship.setFriendId(1L);
        newUser.getFriendships()
               .add(friendship);

        User savedUser = userDbStorage.persist(newUser);

        assertThat(savedUser.getId()).isNotNull();

        Optional<User> retrievedUser = userDbStorage.get(savedUser.getId());
        assertThat(retrievedUser).isPresent();

        User user = retrievedUser.get();
        assertThat(user.getLogin()).isEqualTo("new_user");
        assertThat(user.getFriendships()).hasSize(1);
        assertThat(user.getFriendships()
                       .iterator()
                       .next()
                       .getFriendId()).isEqualTo(1L);
    }

    @Test
    void update_shouldUpdateUserAndFriendships() {
        User user = userDbStorage.get(1L)
                                 .get();
        user.setName("Updated Name");
        user.setEmail("updated@example.com");

        user.getFriendships()
            .clear();
        Friendship newFriendship = new Friendship();
        newFriendship.setFriendId(3L);
        user.getFriendships()
            .add(newFriendship);

        Optional<User> updatedUser = userDbStorage.update(user);

        assertThat(updatedUser).isPresent();

        User retrievedUser = userDbStorage.get(1L)
                                          .get();
        assertThat(retrievedUser.getName()).isEqualTo("Updated Name");
        assertThat(retrievedUser.getEmail()).isEqualTo("updated@example.com");

        assertThat(retrievedUser.getFriendships()).hasSize(1);
        assertThat(retrievedUser.getFriendships()
                                .iterator()
                                .next()
                                .getFriendId()).isEqualTo(3L);
    }

    @Test
    void getFriends_shouldReturnUserFriends() {
        List<User> friends = userDbStorage.getFriends(1L);

        assertThat(friends).hasSize(2);
        assertThat(friends).extracting(User::getId)
                           .containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    void persist_shouldHandleUserWithoutFriendships() {
        User newUser = new User();
        newUser.setLogin("no_friends");
        newUser.setEmail("nofriends@example.com");
        newUser.setName("No Friends");
        newUser.setBirthday(LocalDate.of(2005, 5, 5));

        User savedUser = userDbStorage.persist(newUser);

        Optional<User> retrievedUser = userDbStorage.get(savedUser.getId());
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get()
                                .getFriendships()).isEmpty();
    }
}