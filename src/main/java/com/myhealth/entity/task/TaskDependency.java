package com.myhealth.entity.task;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "task_dependencies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(TaskDependencyId.class)
public class TaskDependency {
    
    @Id
    @Column(name = "task_id")
    private UUID taskId;
    
    @Id
    @Column(name = "depends_on_id")
    private UUID dependsOnId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "dep_type", nullable = false)
    private DependencyType depType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", insertable = false, updatable = false)
    private Task task;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depends_on_id", insertable = false, updatable = false)
    private Task dependsOnTask;
}