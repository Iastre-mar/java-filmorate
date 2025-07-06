package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Friendship {
    @EqualsAndHashCode.Include
    private Long userId;

    @EqualsAndHashCode.Include
    private Long friendId;

    private FriendshipStatus friendshipStatus;
}
