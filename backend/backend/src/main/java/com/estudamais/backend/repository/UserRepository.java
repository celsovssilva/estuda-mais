package com.estudamais.backend.repository;

import com.estudamais.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
   Optional <User> existsByEmail(String email);
}
