package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.Friendship;
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
        userStorage.get(id)
                   .get(); // check if user exists
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

        removeFromFriends(user, otherUser);
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
        boolean alreadyFriends = isAlreadyFriends(user, otherUser);

        if (alreadyFriends) {
            return false;
        }

        Friendship userFriendship = new Friendship();
        userFriendship.setFriendId(otherUser.getId());
        userFriendship.setFriendshipStatus(FriendshipStatus.ACCEPTED);

        Friendship otherFriendship = new Friendship();
        otherFriendship.setFriendId(user.getId());
        otherFriendship.setFriendshipStatus(FriendshipStatus.ACCEPTED);

        user.getFriendships().add(userFriendship);
        otherUser.getFriendships().add(otherFriendship);

        return true;
    }

    private boolean isAlreadyFriends(User user, User otherUser){
        return isFriendWith(user, otherUser) || isFriendWith(otherUser, user);
    }

    private boolean isFriendWith(User user, User otherUser){
        return user.getFriendships().stream()
                   .anyMatch(f -> f.getFriendId().equals(otherUser.getId()));
    }

    private boolean removeFromFriends(User user, User otherUser) {
        return removeFriend(user, otherUser) && removeFriend(otherUser, user);
    }

    private boolean removeFriend(User user, User otherUser){
        return user.getFriendships().removeIf(
                f -> f.getFriendId().equals(otherUser.getId())
        );
    }

    private Set<User> getFriendsIntersection(Set<User> userFriends,
                                             Set<User> otherUserFriends
    ) {
        return userFriends.stream()
                          .filter(otherUserFriends::contains)
                          .collect(Collectors.toSet());
    }
}
