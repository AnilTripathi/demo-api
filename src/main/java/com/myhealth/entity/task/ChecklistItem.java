package com.myhealth.entity.task;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "checklist_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist;
    
    @Column(name = "content", nullable = false)
    private String content;
    
    @Column(name = "is_done", nullable = false)
    private Boolean isDone = false;
    
    @Column(name = "order_index", nullable = false)
    private Long orderIndex = 0L;
    
    @Column(name = "done_at")
    private ZonedDateTime doneAt;
}