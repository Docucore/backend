package com.swucapstone.docucore.repository;

import com.swucapstone.docucore.entity.Paper;
import com.swucapstone.docucore.entity.Save;
import com.swucapstone.docucore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SaveRepository extends JpaRepository<Save, Long> {
    Integer countByPaper(Paper paper);

    Optional<Save> findByPaperAndUser(Paper paper, User user);
    List<Save> findByUser(User user);

}
