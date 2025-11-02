package com.meetus.MeetUSInterview.seeder;

import com.github.javafaker.Faker;
import com.meetus.MeetUSInterview.entity.Task;
import com.meetus.MeetUSInterview.entity.User;
import com.meetus.MeetUSInterview.enums.TaskStatus;
import com.meetus.MeetUSInterview.repository.TaskRepository;
import com.meetus.MeetUSInterview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class TaskSeeder implements CommandLineRunner {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {

        if (taskRepository.count() > 0) {
            log.info("Tasks already exist in database. Skipping seeding.");
            return;
        }
        
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            log.info("No users found in database. Creating 3 fake users...");
            users = createFakeUsers();
            log.info("Successfully created {} fake users", users.size());
        }

        log.info("Starting to seed 100 tasks...");

        List<Task> tasks = new ArrayList<>();
        TaskStatus[] statuses = TaskStatus.values();

        for (int i = 0; i < 100; i++) {
            Task task = Task.builder()
                    .title(generateTitle(i))
                    .description(generateDescription())
                    .status(statuses[random.nextInt(statuses.length)])
                    .userId(getRandomUser(users).getId())
                    .createdAt(generateRandomDate())
                    .build();
            
            tasks.add(task);
        }

        taskRepository.saveAll(tasks);
        log.info("Successfully seeded {} tasks", tasks.size());
    }

    private String generateTitle(int index) {

        String[] titleTemplates = {
            "Fix " + faker.programmingLanguage().name() + " issue",
            "Update " + faker.app().name(),
            "Review " + faker.file().fileName(),
            "Optimize " + faker.pokemon().name() + " performance",
            "Deploy " + faker.app().version()
        };
        
        String baseTitle = titleTemplates[random.nextInt(titleTemplates.length)];
        return String.format("%s #%d", baseTitle, index + 1);
    }

    private String generateDescription() {
        return faker.lorem().paragraph(random.nextInt(3) + 1);
    }

    private User getRandomUser(List<User> users) {
        return users.get(random.nextInt(users.size()));
    }

    private LocalDateTime generateRandomDate() {
        return faker.date()
                .past(30, TimeUnit.DAYS)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private List<User> createFakeUsers() {
        List<User> users = new ArrayList<>();
        users.add(User.builder()
                .name("Kareem Hussen")
                .email("kareemhussen500@gmail.com")
                .password(passwordEncoder.encode("12345678"))
                .build());

        users.add(User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());

        users.add(User.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());
        
        return userRepository.saveAll(users);
    }
}
