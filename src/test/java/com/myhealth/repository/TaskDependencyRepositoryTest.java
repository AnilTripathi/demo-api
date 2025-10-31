package com.myhealth.repository;

import com.myhealth.entity.task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskDependencyRepositoryTest {
    
    @Autowired
    private TaskDependencyRepository taskDependencyRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private StatusRepository statusRepository;
    
    @Autowired
    private PriorityRepository priorityRepository;
    
    private Task task1;
    private Task task2;
    
    @BeforeEach
    void setUp() {
        // Create test status and priority
        Status status = new Status();
        status.setId((short) 2);
        status.setName("Todo");
        status.setIsDone(false);
        statusRepository.save(status);
        
        Priority priority = new Priority();
        priority.setId((short) 3);
        priority.setName("Medium");
        priorityRepository.save(priority);
        
        // Create test tasks
        task1 = new Task();
        task1.setTitle("Task 1");
        task1.setStatus(status);
        task1.setPriority(priority);
        task1.setExtras(new HashMap<>());
        task1 = taskRepository.save(task1);
        
        task2 = new Task();
        task2.setTitle("Task 2");
        task2.setStatus(status);
        task2.setPriority(priority);
        task2.setExtras(new HashMap<>());
        task2 = taskRepository.save(task2);
    }
    
    @Test
    void shouldCreateTaskDependency() {
        // Given
        TaskDependency dependency = new TaskDependency();
        dependency.setTaskId(task1.getId());
        dependency.setDependsOnId(task2.getId());
        dependency.setDepType(DependencyType.FS);
        
        // When
        TaskDependency saved = taskDependencyRepository.save(dependency);
        
        // Then
        assertThat(saved.getTaskId()).isEqualTo(task1.getId());
        assertThat(saved.getDependsOnId()).isEqualTo(task2.getId());
        assertThat(saved.getDepType()).isEqualTo(DependencyType.FS);
    }
    
    @Test
    void shouldFindDependenciesByTaskId() {
        // Given
        TaskDependency dependency = new TaskDependency();
        dependency.setTaskId(task1.getId());
        dependency.setDependsOnId(task2.getId());
        dependency.setDepType(DependencyType.SS);
        taskDependencyRepository.save(dependency);
        
        // When
        var dependencies = taskDependencyRepository.findByTaskId(task1.getId());
        
        // Then
        assertThat(dependencies).hasSize(1);
        assertThat(dependencies.get(0).getDepType()).isEqualTo(DependencyType.SS);
    }
    
    @Test
    void shouldCascadeDeleteWithTask() {
        // Given
        TaskDependency dependency = new TaskDependency();
        dependency.setTaskId(task1.getId());
        dependency.setDependsOnId(task2.getId());
        dependency.setDepType(DependencyType.FF);
        taskDependencyRepository.save(dependency);
        
        // When
        taskRepository.delete(task1);
        
        // Then
        var dependencies = taskDependencyRepository.findByTaskId(task1.getId());
        assertThat(dependencies).isEmpty();
    }
}