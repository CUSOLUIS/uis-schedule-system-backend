package com.uis.schedule.backend.presentation.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherDTO {
    private UUID teacherId;
    private String fullName;
    private String linkType;
    private String availability;
    private String department;
}
