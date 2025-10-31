package com.myhealth.repository;

import com.myhealth.entity.task.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, UUID> {
    
    List<ChecklistItem> findByChecklistIdOrderByOrderIndex(UUID checklistId);
    
    List<ChecklistItem> findByChecklistIdAndIsDone(UUID checklistId, Boolean isDone);
}