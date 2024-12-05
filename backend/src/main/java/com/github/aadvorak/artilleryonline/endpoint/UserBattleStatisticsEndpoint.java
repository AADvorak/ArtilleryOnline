package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.dto.request.UserBattleHistoryFiltersRequest;
import com.github.aadvorak.artilleryonline.dto.response.UserBattleStatisticsResponse;
import com.github.aadvorak.artilleryonline.service.BattleStatisticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/battles/statistics")
@RequiredArgsConstructor
public class UserBattleStatisticsEndpoint {

    private final BattleStatisticsService battleStatisticsService;

    @PostMapping("/filter")
    public UserBattleStatisticsResponse loadStatistics(
            @RequestBody @Valid UserBattleHistoryFiltersRequest request
    ) {
        return battleStatisticsService.getUserBattleStatistics(request);
    }
}
