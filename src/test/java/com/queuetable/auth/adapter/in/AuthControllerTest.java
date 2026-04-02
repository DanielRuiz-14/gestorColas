package com.queuetable.auth.adapter.in;

import com.queuetable.auth.dto.AuthResponse;
import com.queuetable.auth.dto.LoginRequest;
import com.queuetable.auth.dto.RefreshRequest;
import com.queuetable.auth.dto.RegisterRequest;
import com.queuetable.shared.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends AbstractIntegrationTest {

    // -------------------------------------------------------------------------
    // POST /auth/register
    // -------------------------------------------------------------------------

    @Test
    void register_success() throws Exception {
        var request = new RegisterRequest(
                "My Restaurant",
                "my-restaurant-1",
                "1 Main Street",
                "owner1@example.com",
                "password123",
                "Alice"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.restaurantId").isNotEmpty())
                .andExpect(jsonPath("$.restaurantName").value("My Restaurant"))
                .andExpect(jsonPath("$.restaurantSlug").value("my-restaurant-1"));
    }

    @Test
    void register_duplicateSlug_returns400() throws Exception {
        registerRestaurant("duplicate-slug-2");

        var request = new RegisterRequest(
                "Another Restaurant",
                "duplicate-slug-2",
                "2 Other Street",
                "unique-email-2@example.com",
                "password123",
                "Bob"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_duplicateEmail_returns400() throws Exception {
        registerRestaurant("first-slug-3", "shared-email-3@example.com");

        var request = new RegisterRequest(
                "Second Restaurant",
                "second-slug-3",
                "3 Another Ave",
                "shared-email-3@example.com",
                "password123",
                "Carol"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_invalidEmail_returns400() throws Exception {
        var request = new RegisterRequest(
                "Bad Email Restaurant",
                "bad-email-slug-4",
                "4 Nowhere Lane",
                "not-a-valid-email",
                "password123",
                "Dan"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------------------
    // POST /auth/login
    // -------------------------------------------------------------------------

    @Test
    void login_success() throws Exception {
        registerRestaurant("login-slug-5", "login5@example.com");

        var loginRequest = new LoginRequest("login5@example.com", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.restaurantId").isNotEmpty())
                .andExpect(jsonPath("$.restaurantSlug").value("login-slug-5"));
    }

    @Test
    void login_wrongPassword_returns400() throws Exception {
        registerRestaurant("wrong-pwd-slug-6", "wrongpwd6@example.com");

        var loginRequest = new LoginRequest("wrongpwd6@example.com", "totally-wrong-password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_nonexistentEmail_returns400() throws Exception {
        var loginRequest = new LoginRequest("ghost7@example.com", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------------------
    // POST /auth/refresh
    // -------------------------------------------------------------------------

    @Test
    void refresh_success() throws Exception {
        AuthResponse auth = registerRestaurant("refresh-slug-8", "refresh8@example.com");

        var refreshRequest = new RefreshRequest(auth.refreshToken());

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.restaurantId").isNotEmpty());
    }

    @Test
    void refresh_invalidToken_returns400() throws Exception {
        var refreshRequest = new RefreshRequest("this.is.garbage");

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------------------
    // Protected endpoints
    // -------------------------------------------------------------------------

    @Test
    void protectedEndpoint_withoutToken_returns401() throws Exception {
        AuthResponse auth = registerRestaurant("no-token-slug-10", "notoken10@example.com");

        mockMvc.perform(get("/restaurants/{id}", auth.restaurantId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withToken_returns200() throws Exception {
        AuthResponse auth = registerRestaurant("with-token-slug-11", "withtoken11@example.com");

        mockMvc.perform(get("/restaurants/{id}", auth.restaurantId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + auth.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.slug").value("with-token-slug-11"));
    }
}
