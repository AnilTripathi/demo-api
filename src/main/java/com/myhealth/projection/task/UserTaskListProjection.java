package com.myhealth.projection.task;

import java.time.Instant;
import java.util.UUID;

public interface UserTaskListProjection {
    UUID getId();
    String getTitle();
    String getDescriptionMd();
    Short getStatusId();
    String getStatusName();
    Short getPriorityId();
    String getPriorityName();
    Instant getDueAt();
    Integer getEstimateMinutes();
    Instant getCreatedAt();
    Instant getUpdatedAt();
}