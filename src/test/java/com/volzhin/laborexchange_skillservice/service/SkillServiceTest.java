package com.volzhin.laborexchange_skillservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.volzhin.laborexchange_skillservice.dto.SkillDto;
import com.volzhin.laborexchange_skillservice.entity.Skill;
import com.volzhin.laborexchange_skillservice.repository.SkillRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    private final Long SKILL_ID = 1L;
    private final String SKILL_NAME = "Spring Boot";

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("Успех: навык создаётся и возвращается")
        void create_Success() {
            SkillDto dto = SkillDto.builder().name(SKILL_NAME).build();
            Skill saved = Skill.builder().id(SKILL_ID).name(SKILL_NAME).build();

            when(skillRepository.save(any(Skill.class))).thenReturn(saved);

            Skill result = skillService.create(dto);

            assertNotNull(result);
            assertEquals(SKILL_ID, result.getId());
            assertEquals(SKILL_NAME, result.getName());
            verify(skillRepository).save(any(Skill.class));
        }

        @Test
        @DisplayName("Конкурентная вставка: DataIntegrityViolation → возврат существующего навыка")
        void create_ConcurrentDuplicate_ReturnsExisting() {
            SkillDto dto = SkillDto.builder().name(SKILL_NAME).build();
            Skill existing = Skill.builder().id(SKILL_ID).name(SKILL_NAME).build();

            when(skillRepository.save(any(Skill.class)))
                    .thenThrow(new DataIntegrityViolationException("unique constraint"));
            when(skillRepository.findByNameIgnoreCase(SKILL_NAME)).thenReturn(Optional.of(existing));

            Skill result = skillService.create(dto);

            assertNotNull(result);
            assertEquals(SKILL_ID, result.getId());
            assertEquals(SKILL_NAME, result.getName());
            verify(skillRepository).findByNameIgnoreCase(SKILL_NAME);
        }

        @Test
        @DisplayName("Конкурентная вставка: DataIntegrityViolation, но навык исчез → EntityNotFoundException")
        void create_ConcurrentDuplicate_NotFoundAfterConflict_ThrowsException() {
            SkillDto dto = SkillDto.builder().name(SKILL_NAME).build();

            when(skillRepository.save(any(Skill.class)))
                    .thenThrow(new DataIntegrityViolationException("unique constraint"));
            when(skillRepository.findByNameIgnoreCase(SKILL_NAME)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> skillService.create(dto));
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("Успех: навык удаляется по ID")
        void delete_Success() {
            when(skillRepository.deleteSkillById(SKILL_ID)).thenReturn(1);

            assertDoesNotThrow(() -> skillService.delete(SKILL_ID));
            verify(skillRepository).deleteSkillById(SKILL_ID);
        }

        @Test
        @DisplayName("Ошибка: удаление несуществующего навыка → EntityNotFoundException")
        void delete_NotFound_ThrowsException() {
            when(skillRepository.deleteSkillById(SKILL_ID)).thenReturn(0);

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> skillService.delete(SKILL_ID));
            assertTrue(ex.getMessage().contains(String.valueOf(SKILL_ID)));
            verify(skillRepository).deleteSkillById(SKILL_ID);
        }
    }

    @Nested
    @DisplayName("findSkillById()")
    class FindByIdTests {

        @Test
        @DisplayName("Успех: навык найден по ID")
        void findSkillById_Success() {
            Skill skill = Skill.builder().id(SKILL_ID).name(SKILL_NAME).build();
            when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));

            Skill result = skillService.findSkillById(SKILL_ID);

            assertEquals(SKILL_NAME, result.getName());
        }

        @Test
        @DisplayName("Ошибка: навык не найден → EntityNotFoundException")
        void findSkillById_NotFound_ThrowsException() {
            when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> skillService.findSkillById(SKILL_ID));
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("Успех: навык обновляется")
        void update_Success() {
            Skill existing = Skill.builder().id(SKILL_ID).name("Old Name").build();
            SkillDto dto = SkillDto.builder().name("New Name").build();

            when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(existing));
            when(skillRepository.save(any(Skill.class))).thenAnswer(i -> i.getArgument(0));

            Skill result = skillService.update(SKILL_ID, dto);

            assertEquals("New Name", result.getName());
            verify(skillRepository).save(existing);
        }
    }
}
