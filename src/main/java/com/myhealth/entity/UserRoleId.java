package com.myhealth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleId {
    
    @Column(name = "user_id")
    private UUID userId;
    
    @Column(name = "role_id")
    private UUID roleId;
}