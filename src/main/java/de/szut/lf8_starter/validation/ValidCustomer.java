package de.szut.lf8_starter.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ValidCustomerValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCustomer {

  String message() default "Kunde existiert nicht im Customer-Service";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
