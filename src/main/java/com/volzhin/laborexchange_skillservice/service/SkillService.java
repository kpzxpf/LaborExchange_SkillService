package com.volzhin.laborexchange_skillservice.service;

import com.volzhin.laborexchange_skillservice.dto.SkillDto;
import com.volzhin.laborexchange_skillservice.entity.Skill;
import com.volzhin.laborexchange_skillservice.repository.SkillRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    @CacheEvict(value = "skills:all", allEntries = true)
    @Transactional
    public Skill create(SkillDto dto) {
        log.info("Creating skill: {}", dto.getName());
        try {
            return skillRepository.save(Skill.builder().name(dto.getName()).build());
        } catch (DataIntegrityViolationException e) {
            // Concurrent insert — return existing skill instead of failing with a duplicate error
            log.info("Skill '{}' already exists due to concurrent insert, returning existing", dto.getName());
            return skillRepository.findByNameIgnoreCase(dto.getName())
                    .orElseThrow(() -> new EntityNotFoundException("Skill not found after conflict: " + dto.getName()));
        }
    }

    @Caching(evict = {
        @CacheEvict(value = "skills", key = "#id"),
        @CacheEvict(value = "skills:all", allEntries = true),
        @CacheEvict(value = "skills:ids", allEntries = true)
    })
    @Transactional
    public Skill update(Long id, SkillDto dto) {
        Skill existingSkill = findSkillById(id);

        existingSkill.setName(dto.getName());
        Skill saved = skillRepository.save(existingSkill);

        log.info("Skill updated: id={} name={}", id, dto.getName());

        return saved;
    }

    @Cacheable(value = "skills", key = "#id")
    @Transactional(readOnly = true)
    public Skill findSkillById(Long id) {
        return skillRepository.findById(id).orElseThrow(() -> {
            log.warn("Skill not found: id={}", id);
            return new EntityNotFoundException("Skill not found: " + id);
        });
    }

    @Cacheable(value = "skills:all")
    @Transactional(readOnly = true)
    public List<Skill> findAll() {
        return skillRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<String> findSkillNamesByIds(Set<Long> ids) {
        return skillRepository.findAllByIdIn(ids)
                .stream()
                .map(Skill::getName)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<Long, String> findSkillMapByIds(Set<Long> ids) {
        return skillRepository.findAllByIdIn(ids)
                .stream()
                .collect(java.util.stream.Collectors.toMap(Skill::getId, Skill::getName));
    }

    @Caching(evict = {
        @CacheEvict(value = "skills", key = "#id"),
        @CacheEvict(value = "skills:all", allEntries = true),
        @CacheEvict(value = "skills:ids", allEntries = true)
    })
    @Transactional
    public void delete(Long id) {
        int deleted = skillRepository.deleteSkillById(id);

        if (deleted == 0) {
            throw new EntityNotFoundException("Skill not found: " + id);
        }

        log.info("Skill deleted: id={}", id);
    }
}
