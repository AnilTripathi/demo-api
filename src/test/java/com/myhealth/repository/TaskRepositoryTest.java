package com.myhealth.repository;

import com.myhealth.entity.task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private StatusRepository statusRepository;
    
    @Autowired
    private PriorityRepository priorityRepository;
    
    @Autowired
    private ChecklistRepository checklistRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    private Status todoStatus;
    private Priority mediumPriority;
    
    @BeforeEach
    void setUp() {
        // Create test status
        todoStatus = new Status();
        todoStatus.setId((short) 2);
        todoStatus.setName("Todo");
        todoStatus.setIsDone(false);
        statusRepository.save(todoStatus);
        
        // Create test priority
        mediumPriority = new Priority();
        mediumPriority.setId((short) 3);
        mediumPriority.setName("Medium");
        priorityRepository.save(mediumPriority);
    }
    
    @Test
    void shouldSaveAndRetrieveTask() {
        // Given
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescriptionMd("Test description");
        task.setStatus(todoStatus);
        task.setPriority(mediumPriority);
        task.setAccountId(UUID.randomUUID());
        
        Map<String, Object> extras = new HashMap<>();
        extras.put("customField", "customValue");
        task.setExtras(extras);
        
        // When
        Task savedTask = taskRepository.save(task);
        
        // Then
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Test Task");
        assertThat(savedTask.getExtras()).containsEntry("customField", "customValue");
        assertThat(savedTask.getCreatedAt()).isNotNull();
        assertThat(savedTask.getUpdatedAt()).isNotNull();
    }
    
    @Test
    void shouldCascadeDeleteChecklists() {
        // Given
        Task task = new Task();
        task.setTitle("Task with Checklist");
        task.setStatus(todoStatus);
        task.setPriority(mediumPriority);
        task.setExtras(new HashMap<>());
        Task savedTask = taskRepository.save(task);
        
        Checklist checklist = new Checklist();
        checklist.setTask(savedTask);
        checklist.setTitle("Test Checklist");
        checklistRepository.save(checklist);
        
        // When
        taskRepository.delete(savedTask);
        
        // Then
        assertThat(checklistRepository.findByTaskIdOrderByOrderIndex(savedTask.getId())).isEmpty();
    }
    
    @Test
    void shouldFindTasksByStatus() {
        // Given
        Task task1 = createTestTask("Task 1");
        Task task2 = createTestTask("Task 2");
        taskRepository.save(task1);
        taskRepository.save(task2);
        
        // When
        var tasks = taskRepository.findByStatusId(todoStatus.getId());
        
        // Then
        assertThat(tasks).hasSize(2);
    }
    
    @Test
    void shouldFindTasksDueBefore() {
        // Given
        Task task = createTestTask("Due Task");
        task.setDueAt(ZonedDateTime.now().minusDays(1));
        taskRepository.save(task);
        
        // When
        var dueTasks = taskRepository.findTasksDueBefore(ZonedDateTime.now());
        
        // Then
        assertThat(dueTasks).hasSize(1);
        assertThat(dueTasks.get(0).getTitle()).isEqualTo("Due Task");
    }
    
    private Task createTestTask(String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setStatus(todoStatus);
        task.setPriority(mediumPriority);
        task.setExtras(new HashMap<>());
        return task;
    }
}