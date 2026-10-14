package com.swucapstone.docucore.repository;

import com.swucapstone.docucore.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByPaper_IdOrderBySectionOrderAsc(Long paperId);
}
