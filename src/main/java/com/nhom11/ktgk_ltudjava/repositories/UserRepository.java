package com.nhom11.ktgk_ltudjava.repositories;

import com.nhom11.ktgk_ltudjava.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}