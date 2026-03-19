package com.volzhin.laborexchange_skillservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Skill from the shared skill dictionary")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {

    @Schema(description = "Skill ID (null on create request)", example = "1")
    private Long id;

    @Schema(description = "Skill name — unique, case-insensitive (max 100 characters)", example = "Spring Boot", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    @Size(max = 100)
    private String name;
}
