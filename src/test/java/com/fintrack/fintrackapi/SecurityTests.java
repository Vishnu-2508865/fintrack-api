package com.fintrack.fintrackapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/balances"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice")
    void authenticatedUserCanRequestOwnBalances() throws Exception {
        mockMvc.perform(get("/api/balances"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "alice")
    void createdExpenseProducesBalanceForAnotherAuthenticatedParticipant() throws Exception {
        mockMvc.perform(post("/api/shared-expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Dinner",
                                  "totalAmount": 10.00,
                                  "splitType": "EQUAL",
                                  "participants": [
                                    {"userId": "alice"},
                                    {"userId": "bob"}
                                  ]
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/balances").with(user("bob")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].otherUserId").value("alice"))
                .andExpect(jsonPath("$[0].youOwe").value(5.00));
    }
}
