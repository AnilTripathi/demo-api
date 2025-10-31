package com.myhealth.entity.task;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "account_id")
    private UUID accountId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description_md")
    private String descriptionMd;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority_id", nullable = false)
    private Priority priority;
    
    @Column(name = "order_index", nullable = false)
    private Long orderIndex = 0L;
    
    @Column(name = "estimate_minutes")
    private Integer estimateMinutes;
    
    @Column(name = "spent_minutes", nullable = false)
    private Integer spentMinutes = 0;
    
    @Column(name = "points", precision = 5, scale = 2)
    private BigDecimal points;
    
    @Column(name = "start_at")
    private ZonedDateTime startAt;
    
    @Column(name = "due_at")
    private ZonedDateTime dueAt;
    
    @Column(name = "completed_at")
    private ZonedDateTime completedAt;
    
    @Column(name = "recurrence_rrule")
    private String recurrenceRrule;
    
    @Column(name = "timezone")
    private String timezone;
    
    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;
    
    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;
    
    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 1;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extras", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> extras;
    
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Checklist> checklists;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Attachment> attachments;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reminder> reminders;
    
    @ManyToMany
    @JoinTable(
        name = "task_labels",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels;
    
    @PrePersist
    protected void onCreate() {
        ZonedDateTime now = ZonedDateTime.now();
        createdAt = now;
        updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }
}