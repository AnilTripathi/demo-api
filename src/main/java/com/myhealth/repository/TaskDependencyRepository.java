package com.myhealth.repository;

import com.myhealth.entity.task.TaskDependency;
import com.myhealth.entity.task.TaskDependencyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependency, TaskDependencyId> {
    
    List<TaskDependency> findByTaskId(UUID taskId);
    
    List<TaskDependency> findByDependsOnId(UUID dependsOnId);
}