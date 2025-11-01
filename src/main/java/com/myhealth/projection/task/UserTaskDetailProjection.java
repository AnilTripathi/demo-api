package com.myhealth.projection.task;

import java.time.Instant;
import java.util.UUID;

public interface UserTaskDetailProjection {
    UUID getId();
    String getTitle();
    String getDescriptionMd();
    Short getStatusId();
    String getStatusName();
    Short getPriorityId();
    String getPriorityName();
    Instant getDueAt();
    Integer getEstimateMinutes();
    Integer getSpentMinutes();
    Instant getCompletedAt();
    Instant getCreatedAt();
    Instant getUpdatedAt();
}