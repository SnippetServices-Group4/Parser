package com.services.group4.parser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ModulesCommunicationTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  void testOwnPermissionCommunication() throws Exception {
    this.mockMvc.perform(get("/test/permission/communication"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.source").value("Parser"))
      .andExpect(jsonPath("$.message").value("Communication between Parser and Permission works!"));
  }

  @Test
  void testOwnSnippetCommunication() throws Exception {
    this.mockMvc.perform(get("/test/snippet/communication"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.source").value("Parser"))
      .andExpect(jsonPath("$.message").value("Communication between Parser and Snippet works!"));
  }
}
