package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    @Qualifier("userDbStorage") private final UserStorage userStorage;
    private final EventService eventService;

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
        getUser(id);
        return userStorage.getFriends(id);
    }

    @LogMethodResult
    public void addFriend(Long id, Long friendId) {
        checkIdsSanity(id, friendId);

        User user = getUser(id).get();
        User otherUser = getUser(friendId).get();
        if (!addToFriends(user, otherUser)) {
            throw new RuntimeException("Не удалось добавить в друзья");
        }

        updateUser(user);
        updateUser(otherUser);

        addFriendEvent(id, friendId, Event.Operation.ADD);
    }

    @LogMethodResult
    public void removeFriend(Long id, Long friendId) {
        checkIdsSanity(id, friendId);
        User user = getUser(id).get();
        User otherUser = getUser(friendId).get();

        removeFromFriends(user, otherUser);

        updateUser(user);
        updateUser(otherUser);

        addFriendEvent(id, friendId, Event.Operation.REMOVE);
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
        userFriendship.setUserId(user.getId());
        userFriendship.setFriendId(otherUser.getId());
        user.getFriendships()
            .add(userFriendship);

        return true;
    }

    private boolean isAlreadyFriends(User user, User otherUser) {
        return isFriendWith(user, otherUser) || isFriendWith(otherUser, user);
    }

    private boolean isFriendWith(User user, User otherUser) {
        return user.getFriendships()
                   .stream()
                   .anyMatch(f -> f.getFriendId()
                                   .equals(otherUser.getId()));
    }

    private boolean removeFromFriends(User user, User otherUser) {
        return removeFriend(user, otherUser) && removeFriend(otherUser, user);
    }

    private boolean removeFriend(User user, User otherUser) {
        return user.getFriendships()
                   .removeIf(f -> f.getFriendId()
                                   .equals(otherUser.getId()));
    }

    private Collection<User> getFriendsIntersection(Collection<User> userFriends,
                                                    Collection<User> otherUserFriends
    ) {
        return userFriends.stream()
                          .filter(otherUserFriends::contains)
                          .collect(Collectors.toSet());
    }

    private void addFriendEvent(Long userId,
                                Long friendId,
                                Event.Operation operation
    ) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(friendId);
        event.setEventType(Event.EventType.FRIEND);
        event.setOperation(operation);
        event.setTimestamp(System.currentTimeMillis());
        eventService.addEvent(event);
    }
}
