package com.myhealth.entity.task;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "attachments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    
    @Column(name = "filename", nullable = false)
    private String filename;
    
    @Column(name = "mime_type")
    private String mimeType;
    
    @Column(name = "size_bytes")
    private Long sizeBytes;
    
    @Column(name = "storage_uri", nullable = false)
    private String storageUri;
    
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }
}