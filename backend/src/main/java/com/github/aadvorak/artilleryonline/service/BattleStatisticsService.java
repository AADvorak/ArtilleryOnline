package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.dto.request.UserBattleHistoryFiltersRequest;
import com.github.aadvorak.artilleryonline.dto.response.UserBattleStatisticsResponse;
import com.github.aadvorak.artilleryonline.repository.UserBattleStatisticsRepository;
import com.github.aadvorak.artilleryonline.repository.filters.UserBattleHistoryFilters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BattleStatisticsService {

    private final UserBattleStatisticsRepository userBattleStatisticsRepository;

    private final UserService userService;

    public UserBattleStatisticsResponse getUserBattleStatistics(UserBattleHistoryFiltersRequest request) {
        var user = userService.getUserFromContext();
        var view = userBattleStatisticsRepository.findByUserIdAndFilters(user.getId(),
                UserBattleHistoryFilters.of(request));
        return UserBattleStatisticsResponse.of(view);
    }
}
