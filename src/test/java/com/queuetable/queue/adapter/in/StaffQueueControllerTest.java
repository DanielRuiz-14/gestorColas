package com.queuetable.queue.adapter.in;

import com.jayway.jsonpath.JsonPath;
import com.queuetable.auth.dto.AuthResponse;
import com.queuetable.queue.dto.JoinQueueRequest;
import com.queuetable.shared.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StaffQueueControllerTest extends AbstractIntegrationTest {

    private AuthResponse freshRestaurant() throws Exception {
        String id = UUID.randomUUID().toString().substring(0, 8);
        return registerRestaurant("staff-q-" + id, id + "@test.com");
    }

    private String bearer(AuthResponse auth) {
        return "Bearer " + auth.accessToken();
    }

    private String slugOf(AuthResponse auth) throws Exception {
        MvcResult result = mockMvc.perform(get("/restaurants/{id}", auth.restaurantId())
                        .header("Authorization", bearer(auth)))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.slug");
    }

    private MvcResult joinQueue(String slug, String name, int partySize) throws Exception {
        var request = new JoinQueueRequest(name, partySize, null);
        return mockMvc.perform(post("/public/restaurants/{slug}/queue", slug)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    // -------------------------------------------------------------------------
    // GET /restaurants/{id}/queue
    // -------------------------------------------------------------------------

    @Test
    void listQueue_empty() throws Exception {
        AuthResponse auth = freshRestaurant();

        mockMvc.perform(get("/restaurants/{id}/queue", auth.restaurantId())
                        .header("Authorization", bearer(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void listQueue_withEntries() throws Exception {
        AuthResponse auth = freshRestaurant();
        String slug = slugOf(auth);

        joinQueue(slug, "Alice", 2);
        joinQueue(slug, "Bob", 4);

        mockMvc.perform(get("/restaurants/{id}/queue", auth.restaurantId())
                        .header("Authorization", bearer(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].customerName").value("Alice"))
                .andExpect(jsonPath("$[1].customerName").value("Bob"));
    }

    @Test
    void listQueue_filterByStatus() throws Exception {
        AuthResponse auth = freshRestaurant();
        String slug = slugOf(auth);

        joinQueue(slug, "Active", 2);

        // All WAITING
        mockMvc.perform(get("/restaurants/{id}/queue", auth.restaurantId())
                        .header("Authorization", bearer(auth))
                        .param("status", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // No CANCELLED
        mockMvc.perform(get("/restaurants/{id}/queue", auth.restaurantId())
                        .header("Authorization", bearer(auth))
                        .param("status", "CANCELLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void listQueue_otherRestaurant_returns403() throws Exception {
        AuthResponse owner = freshRestaurant();
        AuthResponse intruder = freshRestaurant();

        mockMvc.perform(get("/restaurants/{id}/queue", owner.restaurantId())
                        .header("Authorization", bearer(intruder)))
                .andExpect(status().isForbidden());
    }

    // -------------------------------------------------------------------------
    // POST /restaurants/{id}/queue/{entryId}/cancel
    // -------------------------------------------------------------------------

    @Test
    void staffCancelEntry_success() throws Exception {
        AuthResponse auth = freshRestaurant();
        String slug = slugOf(auth);

        MvcResult joinResult = joinQueue(slug, "Victim", 3);
        String entryId = JsonPath.read(joinResult.getResponse().getContentAsString(), "$.entryId");

        mockMvc.perform(post("/restaurants/{rid}/queue/{eid}/cancel", auth.restaurantId(), entryId)
                        .header("Authorization", bearer(auth)))
                .andExpect(status().isNoContent());

        // Verify via staff list with status filter
        mockMvc.perform(get("/restaurants/{id}/queue", auth.restaurantId())
                        .header("Authorization", bearer(auth))
                        .param("status", "CANCELLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("CANCELLED"));
    }

    @Test
    void staffCancelEntry_otherRestaurant_returns403() throws Exception {
        AuthResponse owner = freshRestaurant();
        AuthResponse intruder = freshRestaurant();
        String slug = slugOf(owner);

        MvcResult joinResult = joinQueue(slug, "Entry", 2);
        String entryId = JsonPath.read(joinResult.getResponse().getContentAsString(), "$.entryId");

        mockMvc.perform(post("/restaurants/{rid}/queue/{eid}/cancel", owner.restaurantId(), entryId)
                        .header("Authorization", bearer(intruder)))
                .andExpect(status().isForbidden());
    }

    @Test
    void staffCancelEntry_notFound_returns404() throws Exception {
        AuthResponse auth = freshRestaurant();

        mockMvc.perform(post("/restaurants/{rid}/queue/{eid}/cancel",
                        auth.restaurantId(), UUID.randomUUID())
                        .header("Authorization", bearer(auth)))
                .andExpect(status().isNotFound());
    }
}
