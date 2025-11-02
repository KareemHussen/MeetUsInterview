package com.meetus.MeetUSInterview.service;

import com.meetus.MeetUSInterview.dto.request.task.TaskCreateRequest;
import com.meetus.MeetUSInterview.dto.request.task.TaskSearchRequest;
import com.meetus.MeetUSInterview.dto.response.task.TaskPageResponse;
import com.meetus.MeetUSInterview.dto.response.task.TaskResponse;
import com.meetus.MeetUSInterview.entity.Task;
import com.meetus.MeetUSInterview.enums.TaskStatus;
import com.meetus.MeetUSInterview.mapper.TaskMapper;
import com.meetus.MeetUSInterview.repository.TaskRepository;
import com.meetus.MeetUSInterview.repository.TaskSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;


    public TaskResponse createTask(TaskCreateRequest request, Long userId) {
        log.info("Creating new task for user ID: {}", userId);

        Task task = taskMapper.toEntity(request);
        task.setUserId(userId);
        task.setStatus(TaskStatus.OPEN);
        
        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {} for user ID: {}", savedTask.getId(), userId);

        return taskMapper.toResponse(savedTask);
    }


    @Transactional(readOnly = true)
    public TaskPageResponse getAllTasksForUser(TaskSearchRequest searchRequest, Long userId) {
        log.info("Fetching tasks for user ID: {} with criteria: {}", userId, searchRequest);

        Specification<Task> specification = TaskSpecifications.build(userId, searchRequest);
        Pageable pageable = createPageable(searchRequest);

        Page<Task> taskPage = taskRepository.findAll(specification, pageable);

        log.info("Found {} tasks out of {} total for user ID: {}", 
            taskPage.getNumberOfElements(), 
            taskPage.getTotalElements(), 
            userId);

        return buildTaskPageResponse(taskPage);
    }


    public TaskPageResponse getTasks(TaskSearchRequest searchRequest, Long userId) {
        return getAllTasksForUser(searchRequest, userId);
    }


    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId, Long userId) {
        log.info("Fetching task with ID: {} for user ID: {}", taskId, userId);

        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        if (!task.getUserId().equals(userId)) {
            throw new SecurityException("User is not authorized to access this task");
        }

        log.info("Task found with ID: {}", taskId);
        return taskMapper.toResponse(task);
    }


    public TaskResponse updateTask(Long taskId, Long userId) {
        log.info("Updating task with ID: {} for user ID: {}", taskId, userId);

        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        if (!task.getUserId().equals(userId)) {
            throw new SecurityException("User is not authorized to update this task");
        }

        if (task.getStatus() == TaskStatus.DONE) {
            throw new IllegalStateException("Task status is already done");
        }

        if (task.getStatus() == TaskStatus.OPEN) {
            task.setStatus(TaskStatus.DONE);
            log.info("Updating task status from OPEN to DONE");
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully with ID: {}", updatedTask.getId());

        return taskMapper.toResponse(updatedTask);
    }

    public void deleteTask(Long taskId, Long userId) {
        log.info("Deleting task with ID: {} for user ID: {}", taskId, userId);

        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        if (!task.getUserId().equals(userId)) {
            throw new SecurityException("User is not authorized to delete this task");
        }

        taskRepository.delete(task);
        log.info("Task deleted successfully with ID: {}", taskId);
    }

    private Pageable createPageable(TaskSearchRequest searchRequest) {
        int page = searchRequest.getPageOrDefault();
        int size = searchRequest.getSizeOrDefault();
        
        Sort sort = createSort(searchRequest);
        
        return PageRequest.of(page, size, sort);
    }

    
    private Sort createSort(TaskSearchRequest searchRequest) {
        String sortBy = searchRequest.getSortByOrDefault();
        String sortDirection = searchRequest.getSortDirectionOrDefault();

        Sort.Direction direction =
                "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;

        return Sort.by(direction, sortBy);
    }

    private TaskPageResponse buildTaskPageResponse(Page<Task> taskPage) {
        List<TaskResponse> taskResponses = taskPage.getContent()
            .stream()
            .map(taskMapper::toResponse)
            .toList();
        
        return TaskPageResponse.builder()
            .tasks(taskResponses)
            .currentPage(taskPage.getNumber())
            .totalPages(taskPage.getTotalPages())
            .totalElements(taskPage.getTotalElements())
            .size(taskPage.getSize())
            .first(taskPage.isFirst())
            .last(taskPage.isLast())
            .build();
    }
}
