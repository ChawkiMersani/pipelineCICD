package com.Guidewire.Monitoring.Controllers;

import com.Guidewire.Monitoring.Entities.Logs.Log;
import com.Guidewire.Monitoring.Services.Implementations.JwtService;
import com.Guidewire.Monitoring.Services.Implementations.LogCreationService;
import com.Guidewire.Monitoring.Services.Implementations.UserDetailsCustomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogController.class)
@Import(TestSecurityConfig.class)
public class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LogCreationService logCreationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsCustomService userDetailsCustomService;

    @Test
    public void testAddLogWhenLogIsValidThenReturnOk() throws Exception {
        // Arrange
        String validLogJson = "{\"id\":\"123\", \"content\":\"{\\\"key\\\":\\\"value\\\"}\"}";
        Log validLog = new Log("123", "{\"key\":\"value\"}");
        Mockito.when(logCreationService.createLog(Mockito.any())).thenReturn(validLog);

        // Act & Assert
        mockMvc.perform(post("/log/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLogJson))
                .andExpect(status().isOk())
                .andExpect(content().json(validLogJson));
    }

    @Test
    public void testAddLogWhenLogIsInvalidThenReturnBadRequest() throws Exception {
        // Arrange
        String invalidLogJson = "{\"id\":\"123\"}"; // Missing content field
        Mockito.when(logCreationService.createLog(Mockito.any())).thenThrow(JsonProcessingException.class);

        // Act & Assert
        mockMvc.perform(post("/log/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLogJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid JSON format"));
    }

    @Test
    public void testAddLogWhenLogHasParsingErrorThenReturnInternalServerError() throws Exception {
        // Arrange
        String validLogJson = "{\"id\":\"123\", \"content\":\"{\\\"key\\\":\\\"value\\\"}\"}";
        Mockito.when(logCreationService.createLog(Mockito.any())).thenThrow(ParseException.class);

        // Act & Assert
        mockMvc.perform(post("/log/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLogJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error parsing log"));
    }

    @Test
    public void testGetLogWhenLogExistsThenReturnOk() throws Exception {
        // Arrange
        String logId = "123";
        Log log = new Log("123", "{\"key\":\"value\"}");
        Mockito.when(logCreationService.getLog(logId)).thenReturn(log);

        // Act & Assert
        mockMvc.perform(get("/log/get/id={id}", logId))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":\"123\", \"content\":\"{\\\"key\\\":\\\"value\\\"}\"}"));
    }

    @Test
    public void testGetLogWhenLogDoesNotExistThenReturnNotFound() throws Exception {
        // Arrange
        String logId = "123";
        Mockito.when(logCreationService.getLog(logId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/log/get/id={id}", logId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Log not found"));
    }
}