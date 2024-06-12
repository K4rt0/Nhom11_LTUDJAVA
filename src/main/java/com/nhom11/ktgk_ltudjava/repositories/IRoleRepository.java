package com.nhom11.ktgk_ltudjava.repositories;

import com.nhom11.ktgk_ltudjava.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface IRoleRepository extends JpaRepository<Role, Long>{
    Role findRoleById(Long id);
}
