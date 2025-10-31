package com.myhealth.entity.task;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "statuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Status {
    
    @Id
    private Short id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "is_done", nullable = false)
    private Boolean isDone = false;
}