package com.myhealth.repository;

import com.myhealth.entity.task.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, UUID> {
    List<Checklist> findByTaskIdOrderByOrderIndex(UUID taskId);
}