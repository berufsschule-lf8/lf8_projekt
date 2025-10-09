package de.szut.lf8_starter.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SkillsNotMatchingException extends RuntimeException {
  public SkillsNotMatchingException(String message) {super(message);}
}
