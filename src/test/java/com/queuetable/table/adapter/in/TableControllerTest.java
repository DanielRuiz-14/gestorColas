package com.queuetable.table.adapter.in;

import com.jayway.jsonpath.JsonPath;
import com.queuetable.auth.dto.AuthResponse;
import com.queuetable.shared.AbstractIntegrationTest;
import com.queuetable.table.dto.CreateTableRequest;
import com.queuetable.table.dto.UpdateTableRequest;
import com.queuetable.table.dto.UpdateTableStatusRequest;
import com.queuetable.table.domain.TableStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TableControllerTest extends AbstractIntegrationTest {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private AuthResponse freshRestaurant(String suffix) throws Exception {
        String slug = "rest-" + suffix;
        String email = suffix + "@test.com";
        return registerRestaurant(slug, email);
    }

    private String bearer(AuthResponse auth) {
        return "Bearer " + auth.accessToken();
    }

    /**
     * Creates a table for the given restaurant and returns its UUID (as String).
     */
    private String createTableAndGetId(AuthResponse auth, String label, int capacity) throws Exception {
        var request = new CreateTableRequest(label, capacity, null);
        MvcResult result = mockMvc.perform(post("/restaurants/{id}/tables", auth.restaurantId())
                        .header("Authorization", bearer(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
    }

    /**
     * Transitions a table status and returns the raw response body.
     */
    private MvcResult updateStatus(AuthResponse auth, String tableId, TableStatus status) throws Exception {
        var request = new UpdateTableStatusRequest(status);
        return mockMvc.perform(patch("/tables/{id}/status", tableId)
                        .header("Authorization", bearer(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    void listTables_empty() throws Exception {
        AuthResponse auth = freshRestaurant(UUID.randomUUID().toString().substring(0, 8));

        mockMvc.perform(get("/restaurants/{id}/tables", auth.restaurantId())
                        .header("Authorization", bearer(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createTable_success() throws Exception {
        AuthResponse auth = freshRestaurant(UUID.randomUUID().toString().substring(0, 8));
        var request = new CreateTableRequest("Mesa 1", 4, "Terraza");

        mockMvc.perform(post("/restaurants/{id}/tables", auth.restaurantId())
                        .header("Authorization", bearer(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.label").value("Mesa 1"))
                .andExpect(jsonPath("$.capacity").value(4))
                .andExpect(jsonPath("$.zone").value("Terraza"))
                .andExpect(jsonPath("$.status").value("FREE"))
                .andExpect(jsonPath("$.restaurantId").value(auth.restaurantId().toString()));
    }

    @Test
    void createTable_duplicateLabel_returns400() throws Exception {
        AuthResponse auth = freshRestaurant(UUID.randomUUID().toString().substring(0, 8));
        var request = new CreateTableRequest("Mesa Dup", 2, null);

        // First creation must succeed
        mockMvc.perform(post("/restaurants/{id}/tables", auth.restaurantId())
                        .header("Authorization", bearer(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second creation with same label must fail
        mockMvc.perform(post("/restaurants/{id}/tables", auth.restaurantId())
                        .header("Authorization", bearer(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTable_success() throws Exception {
        AuthResponse auth = freshRestaurant(UUID.randomUUID().toString().substring(0, 8));
        String tableId = createTableAndGetId(auth, "Mesa Original", 2);

        var updateRequest = new UpdateTableRequest("Mesa Actualizada", 6, "Interior");

        mockMvc.perform(patch("/tables/{id}", tableId)
                        .header("Authorization", bearer(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("Mesa Actualizada"))
                .andExpect(jsonPath("$.capacity").value(6))
                .andExpect(jsonPath("$.zone").value("Interior"));
    }

    @Test
    void updateStatus_freeToOccupied_success() throws Exception {
        AuthResponse auth = freshRestaurant(UUID.randomUUID().toString().substring(0, 8));
        String tableId = createTableAndGetId(auth, "Mesa A", 2);

        updateStatus(auth, tableId, TableStatus.OCCUPIED);

        mockMvc.perform(get("/restaurants/{id}/tables", auth.restaurantId())
                        .header("Authorization", bearer(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("OCCUPIED"));
    }

    @Test
    void updateStatus_occupiedToCleaning_success() throws Exception {
        AuthResponse auth = freshRestaurant(UUID.randomUUID().toString().substring(0, 8));
        String tableId = createTableAndGetId(auth, "Mesa B", 2);

        // FREE → OCCUPIED
        updateStatus(auth, tableId, TableStatus.OCCUPIED);

        // OCCUPIED → CLEANING
        mockMvc.perform(patch("/tables/{id}/status", tableId)
                        .header("Authorization", bearer(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateTableStatusRequest(TableStatus.CLEANING))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLEANING"));
    }

    @Test
    void updateStatus_cleaningToFree_success() throws Exception {
        AuthResponse auth = freshRestaurant(UUID.randomUUID().toString().substring(0, 8));
        String tableId = createTableAndGetId(auth, "Mesa C", 2);

        // FREE → OCCUPIED → CLEANING
        updateStatus(auth, tableId, TableStatus.OCCUPIED);
        updateStatus(auth, tableId, TableStatus.CLEANING);

        // CLEANING → FREE
        mockMvc.perform(patch("/tables/{id}/status", tableId)
                        .header("Authorization", bearer(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateTableStatusRequest(TableStatus.FREE))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FREE"));
    }

    @Test
    void updateStatus_freeToCleaningInvalid_returns400() throws Exception {
        AuthResponse auth = freshRestaurant(UUID.randomUUID().toString().substring(0, 8));
        String tableId = createTableAndGetId(auth, "Mesa D", 2);

        // FREE → CLEANING is invalid
        mockMvc.perform(patch("/tables/{id}/status", tableId)
                        .header("Authorization", bearer(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateTableStatusRequest(TableStatus.CLEANING))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTable_free_success() throws Exception {
        AuthResponse auth = freshRestaurant(UUID.randomUUID().toString().substring(0, 8));
        String tableId = createTableAndGetId(auth, "Mesa E", 2);

        mockMvc.perform(delete("/tables/{id}", tableId)
                        .header("Authorization", bearer(auth)))
                .andExpect(status().isNoContent());

        // Verify the table is gone from the list
        mockMvc.perform(get("/restaurants/{id}/tables", auth.restaurantId())
                        .header("Authorization", bearer(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deleteTable_occupied_returns400() throws Exception {
        AuthResponse auth = freshRestaurant(UUID.randomUUID().toString().substring(0, 8));
        String tableId = createTableAndGetId(auth, "Mesa F", 2);

        // Move to OCCUPIED
        updateStatus(auth, tableId, TableStatus.OCCUPIED);

        // Deletion of an occupied table must be rejected
        mockMvc.perform(delete("/tables/{id}", tableId)
                        .header("Authorization", bearer(auth)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void accessOtherRestaurantTable_returns403() throws Exception {
        // Two independent restaurants
        AuthResponse ownerAuth = freshRestaurant(UUID.randomUUID().toString().substring(0, 8));
        AuthResponse intruderAuth = freshRestaurant(UUID.randomUUID().toString().substring(0, 8));

        // Owner creates a table
        String tableId = createTableAndGetId(ownerAuth, "Mesa Privada", 4);

        // Intruder tries to read owner's table list via owner's restaurantId
        mockMvc.perform(get("/restaurants/{id}/tables", ownerAuth.restaurantId())
                        .header("Authorization", bearer(intruderAuth)))
                .andExpect(status().isForbidden());

        // Intruder tries to update owner's table directly
        var updateRequest = new UpdateTableRequest("Hackeada", null, null);
        mockMvc.perform(patch("/tables/{id}", tableId)
                        .header("Authorization", bearer(intruderAuth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());

        // Intruder tries to delete owner's table
        mockMvc.perform(delete("/tables/{id}", tableId)
                        .header("Authorization", bearer(intruderAuth)))
                .andExpect(status().isForbidden());
    }
}
