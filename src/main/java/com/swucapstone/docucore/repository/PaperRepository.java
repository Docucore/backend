package com.swucapstone.docucore.repository;

import com.swucapstone.docucore.entity.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaperRepository extends JpaRepository<Paper, Long> {
    List<Paper> findByTitleOrderByViewsDesc(String title);
    List<Paper> findByTitleOrderByPublicationDateDesc(String title);
    Optional<Paper> findByArXivId(String arXivId);

    @Query("""
    SELECT p FROM Paper p
    WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(p.title_ko) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(p.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Paper> searchByKeyword(@Param("keyword") String keyword);
}
