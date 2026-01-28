package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.error.exception.ConflictAppException;
import com.github.aadvorak.artilleryonline.model.Locale;
import com.github.aadvorak.artilleryonline.model.LocaleCode;
import com.github.aadvorak.artilleryonline.properties.ApplicationLimits;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BattleStarter {

    private final ApplicationLimits applicationLimits;

    private final UserBattleMap userBattleMap;

    private final BattleRunner battleRunner;

    private final BattleFactory battleFactory;

    private final ThreadPoolTaskExecutor runBattleExecutor;

    public Battle start(Set<BattleParticipant> participants, BattleType battleType) {
        checkMaxBattles();
        var battle = battleFactory.createBattle(participants, battleType);
        participants.stream()
                .filter(participant -> participant.getUser() != null)
                .forEach(participant -> userBattleMap.put(participant.getUser().getId(), battle));
        battleRunner.runBattle(battle);
        var nicknames = participants.stream()
                .filter(participant -> participant.getUser() != null)
                .map(BattleParticipant::getNickname)
                .collect(Collectors.toSet());
        log.info("Battle of type {} started for users: {}, map size: {}",
                battleType.name(), nicknames, userBattleMap.size());
        return battle;
    }

    public void checkMaxBattles() {
        if (runBattleExecutor.getActiveCount() >= applicationLimits.getMaxBattles()) {
            throw new ConflictAppException("Max battles limit reached",
                    new Locale().setCode(LocaleCode.MAX_BATTLES_LIMIT_EXCEED));
        }
    }
}
