package com.uis.schedule.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private String id;
    private String name;
    private String displayName;
    private String description;
    private List<String> permissions;
    private String colorScheme;
    private List<Object> menuItems;
    private String iconClass;
    private List<String> routes;
}