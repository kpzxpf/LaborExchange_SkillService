package com.volzhin.laborexchange_skillservice.controller;

import com.volzhin.laborexchange_skillservice.dto.SkillDto;
import com.volzhin.laborexchange_skillservice.entity.Skill;
import com.volzhin.laborexchange_skillservice.mapper.SkillMapper;
import com.volzhin.laborexchange_skillservice.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Tag(name = "Skills", description = "Skill dictionary CRUD and bulk name lookup")
@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;
    private final SkillMapper mapper;

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Create a skill",
            description = "Adds a new skill to the dictionary. If a skill with the same name already exists (case-insensitive), the existing skill is returned instead of creating a duplicate."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Skill created or existing skill returned",
                    content = @Content(schema = @Schema(implementation = SkillDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error — name is blank or exceeds 100 characters")
    })
    @PostMapping
    public ResponseEntity<SkillDto> create(@RequestBody @Valid SkillDto dto) {
        Skill saved = skillService.create(dto);
        return new ResponseEntity<>(mapper.toDto(saved), HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update a skill")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated skill",
                    content = @Content(schema = @Schema(implementation = SkillDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Skill not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SkillDto> update(
            @Parameter(description = "Skill ID", required = true) @PathVariable Long id,
            @RequestBody @Valid SkillDto dto) {
        Skill updated = skillService.update(id, dto);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @Operation(summary = "Get skill by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Skill found",
                    content = @Content(schema = @Schema(implementation = SkillDto.class))),
            @ApiResponse(responseCode = "404", description = "Skill not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SkillDto> getById(
            @Parameter(description = "Skill ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(mapper.toDto(skillService.findSkillById(id)));
    }

    @Operation(summary = "Get all skills", description = "Returns the full skill dictionary. Result is cached in Redis for 2 hours.")
    @ApiResponse(responseCode = "200", description = "List of all skills",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SkillDto.class))))
    @GetMapping
    public ResponseEntity<List<SkillDto>> getAll() {
        List<SkillDto> skills = skillService.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
        return ResponseEntity.ok(skills);
    }

    @Operation(
            summary = "Get skill names by IDs (bulk)",
            description = "Returns a list of skill names for the given set of IDs. Used internally by VacancyService and ResumeService to build Elasticsearch index events. Result is cached."
    )
    @ApiResponse(responseCode = "200", description = "List of skill name strings",
            content = @Content(array = @ArraySchema(schema = @Schema(type = "string", example = "Java"))))
    @GetMapping("/names/by-ids")
    public ResponseEntity<List<String>> findSkillNamesByIds(
            @Parameter(description = "Set of skill IDs", required = true, example = "1,2,5")
            @RequestParam Set<Long> ids) {
        return ResponseEntity.ok(skillService.findSkillNamesByIds(ids));
    }

    @Operation(
            summary = "Get skill ID→name map by IDs (bulk)",
            description = "Returns a Map of skill ID to skill name for the given set of IDs. Used internally for efficient batch lookups."
    )
    @ApiResponse(responseCode = "200", description = "Map of skillId to skillName")
    @GetMapping("/map/by-ids")
    public ResponseEntity<Map<Long, String>> findSkillMapByIds(
            @Parameter(description = "Set of skill IDs", required = true, example = "1,2,5")
            @RequestParam Set<Long> ids) {
        return ResponseEntity.ok(skillService.findSkillMapByIds(ids));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete a skill")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Skill not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Skill ID", required = true) @PathVariable Long id) {
        skillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
