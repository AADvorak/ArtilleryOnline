package com.github.aadvorak.artilleryonline.repository;

import com.github.aadvorak.artilleryonline.entity.view.UserBattleStatisticsView;
import com.github.aadvorak.artilleryonline.repository.filters.UserBattleHistoryFilters;

public interface UserBattleStatisticsRepository {

    UserBattleStatisticsView findByUserIdAndFilters(long userId, UserBattleHistoryFilters filters);
}
