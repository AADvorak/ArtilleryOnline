package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.battle.Battle;
import com.github.aadvorak.artilleryonline.entity.BattleHistory;
import com.github.aadvorak.artilleryonline.entity.UserBattleHistory;
import com.github.aadvorak.artilleryonline.entity.UserBattleHistoryId;
import com.github.aadvorak.artilleryonline.repository.BattleHistoryRepository;
import com.github.aadvorak.artilleryonline.repository.UserBattleHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class BattleHistoryService {

    private final BattleHistoryRepository battleHistoryRepository;

    private final UserBattleHistoryRepository userBattleHistoryRepository;

    private final ModelMapper mapper = new ModelMapper();

    public void writeHistory(Battle battle) {
        var battleHistory = battleHistoryRepository.save(new BattleHistory()
                .setBattleTypeId(battle.getType().getId())
                .setBeginTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(battle.getBeginTime()),
                        ZoneId.systemDefault())));
        battle.getUserMap().values().forEach(user -> {
            var statistics = battle.getModel().getStatistics().get(user.getId());
            var userBattleHistory = mapper.map(statistics, UserBattleHistory.class)
                    .setId(new UserBattleHistoryId()
                            .setBattleHistoryId(battleHistory.getId())
                            .setUserId(user.getId()))
                    .setSurvived(battle.getModel().getVehicles().containsKey(user.getNickname()));
            userBattleHistoryRepository.save(userBattleHistory);
        });
    }
}
