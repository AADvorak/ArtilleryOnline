package com.github.aadvorak.artilleryonline.repository;

import com.github.aadvorak.artilleryonline.entity.BattleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleHistoryRepository extends JpaRepository<BattleHistory, Long> {
}
