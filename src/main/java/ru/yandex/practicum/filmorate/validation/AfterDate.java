package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = AfterDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterDate {
    String value();

    String message() default "Date shall be after {}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
