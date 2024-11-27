package com.github.aadvorak.artilleryonline.repository;

import com.github.aadvorak.artilleryonline.entity.view.UserBattleHistoryView;
import com.github.aadvorak.artilleryonline.repository.filters.UserBattleHistoryFilters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilterUserBattleHistoryRepository {

    Page<UserBattleHistoryView> findByUserIdAndFilters(long userId, UserBattleHistoryFilters filters, Pageable pageable);
}
