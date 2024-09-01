package com.github.aadvorak.artilleryonline.service;

import com.github.aadvorak.artilleryonline.collection.UserBattleMap;
import com.github.aadvorak.artilleryonline.dto.response.BattleResponse;
import com.github.aadvorak.artilleryonline.dto.response.BattleStateResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBattleService {

    private final UserBattleMap userBattleMap;

    private final ModelMapper mapper = new ModelMapper();

    public BattleResponse getBattle(String userKey) {
        var battle = userBattleMap.get(userKey);
        if (battle == null) {
            return null;
        }
        return mapper.map(battle, BattleResponse.class);
    }

    public BattleStateResponse getBattleState(String userKey) {
        var battle = userBattleMap.get(userKey);
        if (battle == null) {
            return null;
        }
        return mapper.map(battle, BattleStateResponse.class);
    }
}
