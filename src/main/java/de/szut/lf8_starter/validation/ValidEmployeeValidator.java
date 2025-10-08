package de.szut.lf8_starter.validation;

import de.szut.lf8_starter.client.EmployeeServiceClient;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ValidEmployeeValidator implements ConstraintValidator<ValidEmployee, Long> {

  private final EmployeeServiceClient employeeServiceClient;

  public ValidEmployeeValidator(EmployeeServiceClient employeeServiceClient) {
    this.employeeServiceClient = employeeServiceClient;
  }

  @Override
  public void initialize(ValidEmployee constraintAnnotation) {}

  @Override
  public boolean isValid(Long employeeId, ConstraintValidatorContext context) {
    if (employeeId == null) {
      return false;
    }

    return employeeServiceClient.employeeExists(employeeId);
  }
}
