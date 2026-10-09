package com.swucapstone.docucore.repository;

import com.swucapstone.docucore.entity.Paper;
import com.swucapstone.docucore.entity.Progress;
import com.swucapstone.docucore.entity.Save;
import com.swucapstone.docucore.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends CrudRepository<Progress, Long> {
    Optional<Progress> findByPaperAndUser(Paper paper, User user);
    List<Progress> findByUser(User user);
}
