package com.github.aadvorak.artilleryonline.endpoint;

import com.github.aadvorak.artilleryonline.dto.request.PageRequest;
import com.github.aadvorak.artilleryonline.dto.request.UserBattleHistoryFiltersRequest;
import com.github.aadvorak.artilleryonline.dto.response.PageResponse;
import com.github.aadvorak.artilleryonline.dto.response.UserBattleHistoryResponse;
import com.github.aadvorak.artilleryonline.service.BattleHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battles/history")
@RequiredArgsConstructor
public class UserBattleHistoryEndpoint {

    private final BattleHistoryService battleHistoryService;

    @PostMapping("/page")
    public PageResponse<UserBattleHistoryResponse> loadHistoryPage(
            @RequestBody @Valid PageRequest<UserBattleHistoryFiltersRequest> request
    ) {
        return battleHistoryService.getUserBattleHistoryPage(request);
    }
}
