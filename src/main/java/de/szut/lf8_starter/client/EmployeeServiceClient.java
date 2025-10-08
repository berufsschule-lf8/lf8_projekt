package de.szut.lf8_starter.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class EmployeeServiceClient {

  private final RestTemplate restTemplate;
  private final String employeeServiceUrl;
  private final AuthService authService;

  public EmployeeServiceClient(RestTemplate restTemplate,
      @Value("${employee.service.url:https://employee-api.szut.dev}") String employeeServiceUrl,
      AuthService authService) {
    this.restTemplate = restTemplate;
    this.employeeServiceUrl = employeeServiceUrl;
    this.authService = authService;
  }

  public boolean employeeExists(Long employeeId) {
    try {
      String url = employeeServiceUrl + "/employees/" + employeeId;
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(authService.getBearerToken());

      HttpEntity<String> entity = new HttpEntity<>(headers);
      ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);

      return response.getStatusCode().is2xxSuccessful();
    } catch (HttpClientErrorException.NotFound e) {
      log.error("Could not find employee with id {}", employeeId);
      return false;
    } catch (Exception e) {
      throw new RuntimeException("Fehler beim Überprüfen des Mitarbeiters: " + e.getMessage());
    }
  }

}
