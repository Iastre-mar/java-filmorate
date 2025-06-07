package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class PositiveDurationValidator
        implements ConstraintValidator<PositiveDuration, Duration> {
    @Override
    public void initialize(PositiveDuration constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Duration duration,
                           ConstraintValidatorContext constraintValidatorContext
    ) {
        return duration == null || duration.getSeconds() > 0;
    }
}
