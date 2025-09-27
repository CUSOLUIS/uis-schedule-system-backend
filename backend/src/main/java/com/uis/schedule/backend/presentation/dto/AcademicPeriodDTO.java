package com.uis.schedule.backend.presentation.dto;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class AcademicPeriodDTO {
    private Long periodId;
	private String name;
	private LocalDate startDate;
	private LocalDate endDate;
	private boolean active;
}
