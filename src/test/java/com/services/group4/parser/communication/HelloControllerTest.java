package com.services.group4.parser.communication;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.services.group4.parser.DotenvConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class HelloControllerTest {
  @BeforeAll
  public static void setup() {
    DotenvConfig.loadEnv();
  }

  @Autowired private MockMvc mockMvc;

  @Test
  public void testSayHello() {
    try {
      mockMvc
          .perform(get("/api/hello"))
          .andExpect(status().isOk())
          .andExpect(content().string("Hello, World!"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testEcho() {
    try {
      mockMvc
          .perform(post("/api/echo").content("Hello, World!"))
          .andExpect(status().isOk())
          .andExpect(content().string("Hello, World!"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
