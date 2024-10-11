package com.services.group4.parser.communication;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ModulesCommunicationTest {
  @Autowired private MockMvc mockMvc;

  @Test
  void testOwnPermissionCommunication() throws Exception {
    this.mockMvc
        .perform(get("/test/permission/communication"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.source").value("Parser"))
        .andExpect(jsonPath("$.message").value("Communication from Permission to Parser works!"));
  }

  @Test
  void testOwnSnippetCommunication() throws Exception {
    this.mockMvc
        .perform(get("/test/snippet/communication"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.source").value("Parser"))
        .andExpect(jsonPath("$.message").value("Communication from Snippet to Parser works!"));
  }
}
