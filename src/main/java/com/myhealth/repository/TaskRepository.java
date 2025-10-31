package com.myhealth.repository;

import com.myhealth.entity.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    
    List<Task> findByStatusId(Short statusId);
    
    List<Task> findByPriorityId(Short priorityId);
    
    List<Task> findByParentTaskId(UUID parentTaskId);
    
    List<Task> findByAccountId(UUID accountId);
    
    @Query("SELECT t FROM Task t WHERE t.dueAt <= :dueDate AND t.deletedAt IS NULL AND t.isArchived = false")
    List<Task> findTasksDueBefore(@Param("dueDate") ZonedDateTime dueDate);
    
    @Query("SELECT t FROM Task t WHERE t.deletedAt IS NULL AND t.isArchived = false ORDER BY t.orderIndex")
    List<Task> findActiveTasksOrderedByIndex();
    
    @Query("SELECT t FROM Task t WHERE t.accountId = :accountId AND t.deletedAt IS NULL ORDER BY t.createdAt DESC")
    List<Task> findByAccountIdAndNotDeleted(@Param("accountId") UUID accountId);
}