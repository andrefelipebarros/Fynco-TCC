package com.tcc.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcc.auth.model.fii.Fii;
import com.tcc.auth.model.user.InvestorProfile;

import java.util.List;

@Repository
public interface FiiRepository extends JpaRepository<Fii, Integer> {
    List<Fii> findByPerfil(InvestorProfile perfil);
}
