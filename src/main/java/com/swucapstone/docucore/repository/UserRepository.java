package com.swucapstone.docucore.repository;

import com.swucapstone.docucore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByName(String name);
    Optional<User> findUserById(Long id);
}
