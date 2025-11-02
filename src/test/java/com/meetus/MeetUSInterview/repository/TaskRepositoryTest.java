package com.meetus.MeetUSInterview.repository;

import com.meetus.MeetUSInterview.entity.Task;
import com.meetus.MeetUSInterview.entity.User;
import com.meetus.MeetUSInterview.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for TaskRepository
 */
@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("hashedPassword")
                .build();
        testUser = userRepository.save(testUser);

        testTask = Task.builder()
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.OPEN)
                .userId(testUser.getId())
                .build();
    }

    @Test
    void testSaveTask() {
        Task savedTask = taskRepository.save(testTask);

        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Test Task");
        assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.OPEN);
        assertThat(savedTask.getUserId()).isEqualTo(testUser.getId());
        assertThat(savedTask.getCreatedAt()).isNotNull();
    }

    @Test
    void testFindById_Success() {
        Task savedTask = taskRepository.save(testTask);

        Optional<Task> found = taskRepository.findById(savedTask.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Task");
        assertThat(found.get().getUserId()).isEqualTo(testUser.getId());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Task> found = taskRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void testDeleteTask() {
        Task savedTask = taskRepository.save(testTask);
        assertThat(taskRepository.findById(savedTask.getId())).isPresent();

        taskRepository.delete(savedTask);

        assertThat(taskRepository.findById(savedTask.getId())).isEmpty();
    }
}
