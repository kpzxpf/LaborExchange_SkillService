package com.volzhin.laborexchange_skillservice.repository;

import com.volzhin.laborexchange_skillservice.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByNameIgnoreCase(String name);
    List<Skill> findAllByIdIn(Set<Long> ids);
}
