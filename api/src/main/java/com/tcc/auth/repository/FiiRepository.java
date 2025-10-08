package com.tcc.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcc.auth.model.fii.Fii;

public interface FiiRepository extends JpaRepository<Fii, Integer> {
}