package com.tcc.auth.repository;

import com.tcc.auth.model.fii.FiiHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FiiHistoryRepository extends JpaRepository<FiiHistory, Integer> {

    List<FiiHistory> findByFii_IdOrder(Integer id);

    List<FiiHistory> findByFii_TickerIgnoreCaseOrder(String ticker);
}
