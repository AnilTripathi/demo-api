package com.myhealth.entity.task;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "priorities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Priority {
    
    @Id
    private Short id;
    
    @Column(name = "name", nullable = false)
    private String name;
}