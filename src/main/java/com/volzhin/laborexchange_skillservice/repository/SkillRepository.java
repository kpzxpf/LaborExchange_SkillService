package com.volzhin.laborexchange_skillservice.repository;

import com.volzhin.laborexchange_skillservice.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByNameIgnoreCase(String name);
    List<Skill> findAllByIdIn(Set<Long> ids);

    @Modifying
    @Query("DELETE FROM Skill s WHERE s.id = :id")
    int deleteSkillById(@Param("id") Long id);
}
