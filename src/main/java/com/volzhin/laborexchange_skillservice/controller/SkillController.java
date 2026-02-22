package com.volzhin.laborexchange_skillservice.controller;

import com.volzhin.laborexchange_skillservice.dto.SkillDto;
import com.volzhin.laborexchange_skillservice.entity.Skill;
import com.volzhin.laborexchange_skillservice.mapper.SkillMapper;
import com.volzhin.laborexchange_skillservice.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;
    private final SkillMapper mapper;

    @PostMapping
    public ResponseEntity<SkillDto> create(@RequestBody @Valid SkillDto dto) {
        Skill saved = skillService.create(dto);
        return new ResponseEntity<>(mapper.toDto(saved), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillDto> update(@PathVariable Long id, @RequestBody @Valid SkillDto dto) {
        Skill updated = skillService.update(id, dto);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toDto(skillService.findSkillById(id)));
    }

    @GetMapping
    public ResponseEntity<List<SkillDto>> getAll() {
        List<SkillDto> skills = skillService.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/names/by-ids")
    public ResponseEntity<List<String>> findSkillNamesByIds(@RequestParam Set<Long> ids) {
        return ResponseEntity.ok(skillService.findSkillNamesByIds(ids));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        skillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}