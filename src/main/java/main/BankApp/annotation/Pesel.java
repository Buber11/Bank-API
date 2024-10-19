package main.BankApp.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = PeselValidator.class)
public @interface Pesel {

    String message() default "Pesel is invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
