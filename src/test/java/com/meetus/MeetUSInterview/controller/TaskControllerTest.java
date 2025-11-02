package com.meetus.MeetUSInterview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetus.MeetUSInterview.dto.request.task.TaskCreateRequest;
import com.meetus.MeetUSInterview.dto.request.task.TaskSearchRequest;
import com.meetus.MeetUSInterview.dto.response.task.TaskPageResponse;
import com.meetus.MeetUSInterview.dto.response.task.TaskResponse;
import com.meetus.MeetUSInterview.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test class for TaskController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private TaskCreateRequest createRequest;
    private TaskResponse taskResponse;
    private TaskPageResponse pageResponse;

    @BeforeEach
    void setUp() {
        createRequest = TaskCreateRequest.builder()
                .title("Test Task")
                .description("Test Description")
                .build();


        taskResponse = TaskResponse.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status("open")
                .userId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        pageResponse = TaskPageResponse.builder()
                .tasks(Arrays.asList(taskResponse))
                .currentPage(0)
                .totalPages(1)
                .totalElements(1L)
                .size(10)
                .first(true)
                .last(true)
                .build();
    }

    @Test
    void testCreateTask_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "1")
    void testCreateTask_Success() throws Exception {
        when(taskService.createTask(any(TaskCreateRequest.class), anyLong())).thenReturn(taskResponse);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.body.title").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "1")
    void testCreateTask_InvalidTitle() throws Exception {
        createRequest.setTitle("ab"); // Too short

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "1")
    void testGetAllTasks_Success() throws Exception {
        when(taskService.getTasks(any(TaskSearchRequest.class), anyLong())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.body.tasks").isArray())
                .andExpect(jsonPath("$.body.totalElements").value(1));
    }

    @Test
    @WithMockUser(username = "1")
    void testGetTaskById_Success() throws Exception {
        when(taskService.getTaskById(anyLong(), anyLong())).thenReturn(taskResponse);

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.body.id").value(1))
                .andExpect(jsonPath("$.body.title").value("Test Task"));
    }

    @Test
    @WithMockUser(username = "1")
    void testUpdateTask_Success() throws Exception {
        when(taskService.updateTask(anyLong(), anyLong())).thenReturn(taskResponse);

        mockMvc.perform(put("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "1")
    void testDeleteTask_Success() throws Exception {
        doNothing().when(taskService).deleteTask(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Task deleted successfully"));
    }

    @Test
    void testGetAllTasks_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isUnauthorized());
    }
}
