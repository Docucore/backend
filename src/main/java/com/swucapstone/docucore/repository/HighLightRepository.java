package com.swucapstone.docucore.repository;

import com.swucapstone.docucore.entity.HighLight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HighLightRepository
        extends JpaRepository<HighLight, Long> {

    // 특정 논문과 섹션의 하이라이트 조회
    List<HighLight> findByPaper_IdAndSection_IdOrderByStartAsc(
            Long paperId,
            Long sectionId
    );

    // 특정 사용자의 하이라이트 조회
    List<HighLight> findByUser_IdAndPaper_IdAndSection_IdOrderByStartAsc(
            Long userId,
            Long paperId,
            Long sectionId
    );
}
