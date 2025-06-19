package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User.
 */
@Data
public class User {
    private long id;
    @NotBlank @Email private String email;
    @NotBlank @Pattern(regexp = "\\S+") private String login;
    private String name;
    @PastOrPresent private LocalDate birthday;

    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private final Set<Long> friendsIdSet = new HashSet<>();

    public String getName() {
        return name == null ? login : name;
    }
}
