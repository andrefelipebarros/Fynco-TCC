package com.tcc.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcc.auth.model.fii.Fii;

@Repository
public interface FiiRepository extends JpaRepository<Fii, Integer> {
}
