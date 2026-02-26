package com.volzhin.laborexchange_skillservice.service;

import com.volzhin.laborexchange_skillservice.dto.SkillDto;
import com.volzhin.laborexchange_skillservice.entity.Skill;
import com.volzhin.laborexchange_skillservice.repository.SkillRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    @Transactional
    public Skill create(SkillDto dto) {
        return skillRepository.findByNameIgnoreCase(dto.getName())
                .orElseGet(() -> {
                    log.info("Creating new skill: {}", dto.getName());
                    return skillRepository.save(
                            Skill.builder()
                                    .name(dto.getName())
                                    .build()
                    );
                });
    }

    @Transactional
    public Skill update(Long id, SkillDto dto) {
        log.info("Updating skill with id: {}", id);
        Skill existingSkill = findSkillById(id);
        existingSkill.setName(dto.getName());
        return skillRepository.save(existingSkill);
    }

    @Transactional(readOnly = true)
    public Skill findSkillById(Long id) {
        return skillRepository.findById(id).orElseThrow(() -> {
            log.info("Skill with id {} not found", id);
            return new EntityNotFoundException("Skill not found with id: " + id);
        });
    }

    @Transactional(readOnly = true)
    public List<Skill> findAll() {
        return skillRepository.findAll();
    }


    @Transactional(readOnly = true)
    public List<String> findSkillNamesByIds(Set<Long> ids) {
        log.info("Fetching skills by ids: {}", ids);
        return skillRepository.findAllByIdIn(ids)
                .stream()
                .map(Skill::getName)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting skill with id: {}", id);
        if (!skillRepository.existsById(id)) {
            throw new EntityNotFoundException("Skill not found with id: " + id);
        }
        skillRepository.deleteById(id);
    }
}