package com.meetus.MeetUSInterview.service;

import com.meetus.MeetUSInterview.dto.request.task.TaskCreateRequest;
import com.meetus.MeetUSInterview.dto.request.task.TaskSearchRequest;
import com.meetus.MeetUSInterview.dto.response.task.TaskPageResponse;
import com.meetus.MeetUSInterview.dto.response.task.TaskResponse;
import com.meetus.MeetUSInterview.entity.Task;
import com.meetus.MeetUSInterview.enums.TaskStatus;
import com.meetus.MeetUSInterview.mapper.TaskMapper;
import com.meetus.MeetUSInterview.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Test class for TaskService
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private TaskCreateRequest createRequest;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        testTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.OPEN)
                .userId(1L)
                .build();

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
                .build();
    }

    @Test
    void testCreateTask_Success() {
        when(taskMapper.toEntity(any(TaskCreateRequest.class))).thenReturn(testTask);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(taskResponse);

        TaskResponse response = taskService.createTask(createRequest, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Test Task");
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testGetAllTasksForUser_Success() {
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask));
        when(taskRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(taskPage);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(taskResponse);

        TaskSearchRequest searchRequest = new TaskSearchRequest();
        TaskPageResponse response = taskService.getAllTasksForUser(searchRequest, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getTasks()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testGetTaskById_Success() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(testTask));
        when(taskMapper.toResponse(any(Task.class))).thenReturn(taskResponse);

        TaskResponse response = taskService.getTaskById(1L, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Test Task");
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void testUpdateTask_Success() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(taskResponse);

        TaskResponse response = taskService.updateTask(1L, 1L);

        assertThat(response).isNotNull();
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testDeleteTask_Success() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(testTask));

        taskService.deleteTask(1L, 1L);

        verify(taskRepository).delete(testTask);
    }

    @Test
    void testDeleteTask_NotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task not found");
    }
}
