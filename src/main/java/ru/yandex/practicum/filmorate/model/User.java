package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * User.
 */
@Data
public class User {
    @EqualsAndHashCode.Exclude
    private Set<Friendship> friendships = new HashSet<>();
    private long id;
    @NotBlank @Email private String email;
    @NotBlank @Pattern(regexp = "\\S+") private String login;
    private String name;
    @PastOrPresent private LocalDate birthday;

    public String getName() {
        return name == null ? login : name;
    }
}
