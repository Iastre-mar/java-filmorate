package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AfterDateValidator
        implements ConstraintValidator<AfterDate, LocalDate> {
    private LocalDate threshold;

    @Override
    public void initialize(AfterDate constraintAnnotation) {

        ConstraintValidator.super.initialize(constraintAnnotation);
        this.threshold = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate localDate,
                           ConstraintValidatorContext constraintValidatorContext
    ) {
        return localDate == null || !localDate.isBefore(threshold);
    }
}
