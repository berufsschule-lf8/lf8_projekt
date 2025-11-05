package de.szut.lf8_starter.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthService {

  private final RestTemplate restTemplate;
  private final String authUrl;
  private final String username;
  private final String appPassword;
  private final String clientId;

  public AuthService(RestTemplate restTemplate,
      @Value("${auth.url:https://authentik.szut.dev/application/o/token/}") String authUrl,
      @Value("${auth.username:john}") String username,
      @Value("${auth.app-password:nt7su3vuTaxtsmKdlhr2RCbRD4tis5i7zBFJbbTWyeTjrRqTpQ513z73ZlV3}") String appPassword,
      @Value("${auth.client-id:hitec_api_client}") String clientId) {
    this.restTemplate = restTemplate;
    this.authUrl = authUrl;
    this.username = username;
    this.appPassword = appPassword;
    this.clientId = clientId;
  }

  public String getBearerToken() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "client_credentials");
    body.add("username", username);
    body.add("password", appPassword);
    body.add("client_id", clientId);
    body.add("scope", "openid");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    ResponseEntity<TokenResponse> response = restTemplate.postForEntity(authUrl, request,
        TokenResponse.class);

    return response.getBody().getAccessToken();
  }

  private static class TokenResponse {

    private String access_token;

    public String getAccessToken() {
      return access_token;
    }

    public void setAccess_token(String access_token) {
      this.access_token = access_token;
    }
  }
}
