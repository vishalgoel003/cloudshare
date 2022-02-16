package com.ripple.cloudshare;

import com.ripple.cloudshare.dto.request.SignInRequest;
import com.ripple.cloudshare.dto.request.SignUpRequest;
import com.ripple.cloudshare.dto.response.SignInResponse;
import com.ripple.cloudshare.dto.response.SignUpResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CloudShareApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    private InetSocketAddress host;

    private Long adminUserId;
    private String adminUserToken;

    @Test
    @Order(1)
    void contextLoads() {
    }

    @Test
    @Order(2)
    void testSignUp() throws URISyntaxException {
        final String baseUrl = "http://localhost:" + randomServerPort + "/auth/sign-up";
        URI uri = new URI(baseUrl);
        SignUpRequest signUpRequest = new SignUpRequest("Vishal", "vishalgoel004@gmail.com", "9816920670", "ADMIN", "vg003");

		HttpHeaders headers = getBaseHttpHeaders();

		HttpEntity<SignUpRequest> request = new HttpEntity<>(signUpRequest, headers);

		ResponseEntity<SignUpResponse> result = this.restTemplate.postForEntity(uri, request, SignUpResponse.class);
		assertEquals(200, result.getStatusCodeValue());

        SignUpResponse response = result.getBody();
		assertNotNull(response);
		assertNotNull(response.getId());
		adminUserId = response.getId();
    }

    @Disabled
    @Test
    @Order(3)
    void testLogin() throws URISyntaxException {
        final String baseUrl = "http://localhost:" + randomServerPort + "/auth/sign-in";
        URI uri = new URI(baseUrl);
        SignInRequest signInRequest = new SignInRequest("no-reply@ripple.com", "9999988888");

        HttpHeaders headers = getBaseHttpHeaders();

        HttpEntity<SignInRequest> request = new HttpEntity<>(signInRequest, headers);

        ResponseEntity<SignInResponse> result = this.restTemplate.postForEntity(uri, request, SignInResponse.class);
        assertEquals(200, result.getStatusCodeValue());

        SignInResponse response = result.getBody();
        assertNotNull(response);
        assertNotNull(response.getAuthorizationToken());
        adminUserToken = response.getAuthorizationToken();
    }

	private HttpHeaders getBaseHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.setHost(InetSocketAddress.createUnresolved("localhost", randomServerPort));
		return headers;
	}

}
