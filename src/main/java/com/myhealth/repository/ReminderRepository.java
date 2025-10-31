package com.myhealth.repository;

import com.myhealth.entity.task.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {
    
    List<Reminder> findByTaskId(UUID taskId);
    
    @Query("SELECT r FROM Reminder r WHERE r.remindAt <= :currentTime ORDER BY r.remindAt")
    List<Reminder> findDueReminders(@Param("currentTime") ZonedDateTime currentTime);
    
    List<Reminder> findByChannel(Reminder.ReminderChannel channel);
}