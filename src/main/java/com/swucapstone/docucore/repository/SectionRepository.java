package com.swucapstone.docucore.repository;

import com.swucapstone.docucore.entity.Paper;
import com.swucapstone.docucore.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository
        extends JpaRepository<Section, Long> {

    List<Section> findByPaper_IdOrderBySectionOrderAsc(Long paperId);
    long countByPaper(Paper paper);

    Optional<Section> findByPaperAndSectionOrder(
            Paper paper,
            int sectionOrder
    );
}