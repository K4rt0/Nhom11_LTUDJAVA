package com.nhom11.ktgk_ltudjava.repositories;

import com.nhom11.ktgk_ltudjava.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
