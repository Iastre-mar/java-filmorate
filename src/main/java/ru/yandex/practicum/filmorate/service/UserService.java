package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    private final InMemoryUserStorage userStorage;

    @LogMethodResult
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    @LogMethodResult
    public User createUser(User user) {
        return userStorage.persist(user);
    }

    @LogMethodResult
    public Optional<User> updateUser(User user) {
        return userStorage.update(user);
    }

    @LogMethodResult
    public Optional<User> getUser(Long id) {
        return userStorage.get(id);
    }

    @LogMethodResult
    public Collection<User> getFriends(Long id) {
        return userStorage.getFriends(id);
    }

    @LogMethodResult
    public void addFriend(Long id, Long friendId) {
        checkIdsSanity(id, friendId);

        User user = userStorage.get(id)
                               .get();
        User otherUser = userStorage.get(friendId)
                                    .get();
        if (!addToFriends(user, otherUser)) {
            throw new RuntimeException("Не удалось добавить в друзья");
        }
    }

    @LogMethodResult
    public void removeFriend(Long id, Long friendId) {
        checkIdsSanity(id, friendId);
        User user = userStorage.get(id)
                               .get();
        User otherUser = userStorage.get(friendId)
                                    .get();

        if (!removeFromFriends(user, otherUser)) {
            throw new RuntimeException("Не удалось удалить из друзей");
        }
    }

    @LogMethodResult
    public Collection<User> getCommonFriends(Long id, Long otherId) {
        checkIdsSanity(id, otherId);

        return getFriendsIntersection(userStorage.getFriends(id),
                                      userStorage.getFriends(otherId));

    }

    private void checkIdsSanity(Long id, Long otherId) {
        if (id.equals(otherId)) {
            throw new RuntimeException("Id в запросе одинаковые");
        }
    }

    private boolean addToFriends(User user, User otherUser) {
        return user.getFriendsIdSet()
                   .add(otherUser.getId()) &&
               otherUser.getFriendsIdSet()
                        .add(user.getId());
    }

    private boolean removeFromFriends(User user, User otherUser) {
        return user.getFriendsIdSet()
                   .remove(otherUser.getId()) &&
               otherUser.getFriendsIdSet()
                        .remove(user.getId());
    }

    private Set<User> getFriendsIntersection(Set<User> userFriends,
                                             Set<User> otherUserFriends
    ) {
        return userFriends.stream()
                          .filter(otherUserFriends::contains)
                          .collect(Collectors.toSet());
    }
}
