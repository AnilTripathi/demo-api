package com.myhealth.repository;

import com.myhealth.entity.task.Task;
import com.myhealth.projection.task.UserTaskDetailProjection;
import com.myhealth.projection.task.UserTaskListProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
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
    
    // Projection-based queries for user tasks
    @Query(value = "SELECT t.id, t.title, t.description_md as descriptionMd, s.id as statusId, s.name as statusName, " +
           "p.id as priorityId, p.name as priorityName, t.due_at as dueAt, " +
           "t.estimate_minutes as estimateMinutes, t.created_at as createdAt, t.updated_at as updatedAt " +
           "FROM tasks t JOIN statuses s ON s.id = t.status_id JOIN priorities p ON p.id = t.priority_id " +
           "WHERE t.account_id = CAST(:accountId AS UUID) AND t.deleted_at IS NULL " +
           "AND (CAST(:statusId AS SMALLINT) IS NULL OR s.id = CAST(:statusId AS SMALLINT)) " +
           "AND (CAST(:query AS TEXT) IS NULL OR LOWER(t.title) LIKE LOWER('%' || CAST(:query AS TEXT) || '%') OR LOWER(COALESCE(t.description_md, '')) LIKE LOWER('%' || CAST(:query AS TEXT) || '%')) " +
           "AND (CAST(:fromDue AS TIMESTAMPTZ) IS NULL OR t.due_at >= CAST(:fromDue AS TIMESTAMPTZ)) " +
           "AND (CAST(:toDue AS TIMESTAMPTZ) IS NULL OR t.due_at <= CAST(:toDue AS TIMESTAMPTZ))", nativeQuery = true)
    Page<UserTaskListProjection> findUserTasksWithFilters(
        @Param("accountId") UUID accountId,
        @Param("statusId") Short statusId,
        @Param("query") String query,
        @Param("fromDue") ZonedDateTime fromDue,
        @Param("toDue") ZonedDateTime toDue,
        Pageable pageable
    );
    
    @Query(value = "SELECT t.id, t.title, t.description_md as descriptionMd, " +
           "s.id as statusId, s.name as statusName, p.id as priorityId, p.name as priorityName, " +
           "t.due_at as dueAt, t.estimate_minutes as estimateMinutes, t.spent_minutes as spentMinutes, " +
           "t.completed_at as completedAt, t.created_at as createdAt, t.updated_at as updatedAt " +
           "FROM tasks t JOIN statuses s ON s.id = t.status_id JOIN priorities p ON p.id = t.priority_id " +
           "WHERE t.id = CAST(:id AS UUID) AND t.account_id = CAST(:accountId AS UUID) AND t.deleted_at IS NULL", nativeQuery = true)
    Optional<UserTaskDetailProjection> findUserTaskDetail(@Param("id") UUID id, @Param("accountId") UUID accountId);
    
    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.accountId = :accountId AND t.deletedAt IS NULL")
    Optional<Task> findByIdAndAccountId(@Param("id") UUID id, @Param("accountId") UUID accountId);
    
    @Modifying
    @Query("UPDATE Task t SET t.deletedAt = :deletedAt WHERE t.id = :id AND t.accountId = :accountId")
    int softDeleteTask(@Param("id") UUID id, @Param("accountId") UUID accountId, @Param("deletedAt") ZonedDateTime deletedAt);
}