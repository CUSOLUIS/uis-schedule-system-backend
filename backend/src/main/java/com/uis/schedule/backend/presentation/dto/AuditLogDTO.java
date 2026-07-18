package com.uis.schedule.backend.presentation.dto;


import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class AuditLogDTO {
    private UUID auditId;
	private String action;
	private String module;
	private String originIp;
	private LocalDateTime date;
	private UserListDTO userId;
}
