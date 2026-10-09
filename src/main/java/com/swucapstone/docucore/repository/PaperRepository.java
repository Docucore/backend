package com.swucapstone.docucore.repository;

import com.swucapstone.docucore.entity.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaperRepository extends JpaRepository<Paper, Long> {
    List<Paper> findByTitleOrderByViewsDesc(String title);
    List<Paper> findByTitleOrderByPublicationDateDesc(String title);
}
