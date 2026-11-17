package com.github.aadvorak.artilleryonline.battle;

import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.error.exception.ConflictAppException;
import com.github.aadvorak.artilleryonline.model.Locale;
import com.github.aadvorak.artilleryonline.model.LocaleCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BattleStarter {

    private final UserBattleMap userBattleMap;

    private final BattleRunner battleRunner;

    private final BattleFactory battleFactory;

    public Battle start(Set<BattleParticipant> participants, BattleType battleType) {
        var battle = battleFactory.createBattle(participants, battleType);
        try {
            battleRunner.runBattle(battle);
        } catch (RejectedExecutionException e) {
            throw new ConflictAppException("Max battles limit reached",
                    new Locale().setCode(LocaleCode.MAX_BATTLES_LIMIT_EXCEED));
        }
        participants.stream()
                .filter(participant -> participant.getUser() != null)
                .forEach(participant -> userBattleMap.put(participant.getUser().getId(), battle));
        var nicknames = participants.stream()
                .filter(participant -> participant.getUser() != null)
                .map(BattleParticipant::getNickname)
                .collect(Collectors.toSet());
        log.info("Battle of type {} started for users: {}, map size: {}",
                battleType.name(), nicknames, userBattleMap.size());
        return battle;
    }
}
