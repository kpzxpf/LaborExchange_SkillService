package com.volzhin.laborexchange_skillservice.mapper;

import com.volzhin.laborexchange_skillservice.dto.SkillDto;
import com.volzhin.laborexchange_skillservice.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    @Mapping(target = "resumeId", source = "resume.id")
    SkillDto toDto(Skill entity);
}
