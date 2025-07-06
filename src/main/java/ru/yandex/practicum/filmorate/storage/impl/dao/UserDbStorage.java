package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FriendshipMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private final FriendshipMapper friendshipMapper;

    private final UserRowMapper userRowMapper;

    @Override
    public Collection<User> getAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> get(Long id) {
        User user = null;
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            user.setFriendships(getFriendshipsForUser(user.getId()));
        } catch (EmptyResultDataAccessException e) {
            // Думаю что сюда засунуть
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Set<User> getFriends(Long id) {
        String sql = "SELECT u.* FROM users u " +
                     "JOIN friendships f ON u.id = f.friend_id " +
                     "WHERE f.user_id = ?";

        return new HashSet<>(jdbcTemplate.query(sql, userRowMapper, id));
    }

    @Override
    public User persist(User user) {
        String sql = "INSERT INTO users (login, email, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,
                                                               Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey())
                                  .longValue();
        user.setId(generatedId);
        saveFriendships(user);
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        String sql = "UPDATE users SET login = ?, email = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getLogin(), user.getEmail(),
                            user.getName(),
                            java.sql.Date.valueOf(user.getBirthday()),
                            user.getId());

        deleteFriendships(user.getId());
        saveFriendships(user);
        return Optional.of(user);
    }

    private void saveFriendships(User user) {
        String sql = "INSERT INTO friendships (user_id, friend_id, status_id) VALUES (?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();

        for (Friendship friendship : user.getFriendships()) {
            batchArgs.add(new Object[]{
                    user.getId(),
                    friendship.getFriendId(),
                    friendship.getFriendshipStatus()
                              .ordinal() + 1
            });
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void deleteFriendships(Long userId) {
        String sql = "DELETE FROM friendships WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    private Set<Friendship> getFriendshipsForUser(Long userId) {
        String sql = "SELECT * FROM friendships WHERE user_id = ?";
        return new HashSet<>(
                jdbcTemplate.query(sql, friendshipMapper, userId));
    }

}
