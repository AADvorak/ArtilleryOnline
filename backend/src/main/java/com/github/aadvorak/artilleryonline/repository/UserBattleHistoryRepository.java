package com.github.aadvorak.artilleryonline.repository;

import com.github.aadvorak.artilleryonline.entity.UserBattleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBattleHistoryRepository extends JpaRepository<UserBattleHistory, Long> {
}
