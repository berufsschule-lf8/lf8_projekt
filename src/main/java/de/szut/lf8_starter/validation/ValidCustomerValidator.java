package de.szut.lf8_starter.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCustomerValidator implements ConstraintValidator<ValidCustomer, Long> {
  @Override
  public void initialize(ValidCustomer constraintAnnotation) {
  }

  @Override
  public boolean isValid(Long kundenId, ConstraintValidatorContext context) {
    return kundenId != null;
  }

}
