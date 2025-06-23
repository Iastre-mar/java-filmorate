package ru.yandex.practicum.filmorate.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Genre {
    COMEDY("comedy"),
    DRAMA("drama"),
    CARTOON("cartoon"),
    THRILLER("thriller"),
    DOCUMENTARY("documentary"),
    ACTION("action");

    private final String value;

    Genre(String value) {
        this.value = value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Genre fromValue(@JsonProperty("genre") String value){
        for (Genre genre : Genre.values()){
            if (genre.value.equals(value)){
                return  genre;
            }
        }
        throw new IllegalArgumentException("Illegal genre: " + value);
    }

    @JsonValue
    public String getValue(){
        return value;
    }
}
