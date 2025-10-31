package com.myhealth.entity.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDependencyId implements Serializable {
    
    private UUID taskId;
    private UUID dependsOnId;
}