package com.swucapstone.docucore.repository;

import com.swucapstone.docucore.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SummaryRepository extends JpaRepository<Summary, Long> {

}