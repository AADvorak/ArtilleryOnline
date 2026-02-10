package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.dto.request.PageRequest;
import com.github.aadvorak.artilleryonline.dto.request.UserBattleHistoryFiltersRequest;
import com.github.aadvorak.artilleryonline.dto.response.PageResponse;
import com.github.aadvorak.artilleryonline.dto.response.UserBattleHistoryResponse;
import com.github.aadvorak.artilleryonline.entity.BattleHistory;
import com.github.aadvorak.artilleryonline.entity.UserBattleHistory;
import com.github.aadvorak.artilleryonline.entity.UserBattleHistoryId;
import com.github.aadvorak.artilleryonline.repository.BattleHistoryRepository;
import com.github.aadvorak.artilleryonline.repository.FilterUserBattleHistoryRepository;
import com.github.aadvorak.artilleryonline.repository.UserBattleHistoryRepository;
import com.github.aadvorak.artilleryonline.repository.filters.UserBattleHistoryFilters;
import com.github.aadvorak.artilleryonline.service.utils.PagingUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BattleHistoryService {

    private static final String DEFAULT_SORT_FIELD = "beginTime";
    private static final List<String> AVAILABLE_SORT_FIELDS = List.of(
            "beginTime",
            "causedDamage",
            "madeShots",
            "causedDirectHits",
            "causedIndirectHits",
            "causedTrackBreaks",
            "destroyedVehicles",
            "receivedDamage",
            "receivedDirectHits",
            "receivedIndirectHits",
            "receivedTrackBreaks"
    );
    private static final int MAX_PAGE_SIZE = 25;

    private final BattleHistoryRepository battleHistoryRepository;

    private final UserBattleHistoryRepository userBattleHistoryRepository;

    private final FilterUserBattleHistoryRepository filterUserBattleHistoryRepository;

    private final UserService userService;

    private final ModelMapper mapper = new ModelMapper();

    private final PagingUtils pagingUtils = new PagingUtils(AVAILABLE_SORT_FIELDS, DEFAULT_SORT_FIELD);

    public void writeHistory(Battle battle) {
        var battleHistory = battleHistoryRepository.save(new BattleHistory()
                .setBattleTypeId(battle.getType().getId())
                .setBeginTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(battle.getBeginTime()),
                        ZoneId.systemDefault())));
        battle.getUserMap().values().forEach(user -> {
            var statistics = battle.getModel().getStatistics().get(user.getNickname());
            var userBattleHistory = mapper.map(statistics, UserBattleHistory.class)
                    .setId(new UserBattleHistoryId()
                            .setBattleHistoryId(battleHistory.getId())
                            .setUserId(user.getId()))
                    .setSurvived(battle.getModel().getVehicles().containsKey(user.getNickname()))
                    .setWon(battle.getWon(user.getNickname()))
                    .setVehicleName(battle.getUserVehicleNameMap().get(user.getId()));
            userBattleHistoryRepository.save(userBattleHistory);
        });
    }

    public PageResponse<UserBattleHistoryResponse> getUserBattleHistoryPage(
            PageRequest<UserBattleHistoryFiltersRequest> request
    ) {
        var user = userService.getUserFromContext();
        var pageable = pagingUtils.getPageable(request, MAX_PAGE_SIZE);
        var page = filterUserBattleHistoryRepository.findByUserIdAndFilters(user.getId(),
                UserBattleHistoryFilters.of(request.getFilters()), pageable);
        return new PageResponse<UserBattleHistoryResponse>()
                .setItems(page.get().map(UserBattleHistoryResponse::of).toList())
                .setPages(page.getTotalPages())
                .setItemsLength(page.getTotalElements());
    }
}
