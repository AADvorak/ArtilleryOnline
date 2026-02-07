import type {DeserializerInput} from "~/deserialization/deserializer-input";
import {type Battle, BattleStage, BattleType, type BattleUpdate, type NicknameTeamMap} from "~/playground/data/battle";
import {DeserializerBase} from "~/deserialization/deserializer-base";
import {deserializeBattleModel, deserializePlayerBattleStatistics} from "~/playground/data/model-deserialize";
import {deserializeBattleModelState} from "~/playground/data/state-deserialize";
import {deserializeBattleModelUpdates} from "~/playground/data/updates-deserialize";
import {deserializeBattleModelEvents} from "~/playground/data/events-deserialize";

export function deserializeBattle(input: DeserializerInput): Battle {
  const id = DeserializerBase.readString(input)
  const model = deserializeBattleModel(input)
  const time = DeserializerBase.readLong(input)
  const duration = DeserializerBase.readLong(input)
  const fps = DeserializerBase.readInt(input)
  const paused = DeserializerBase.readBoolean(input)
  const battleStage = DeserializerBase.readString(input) as BattleStage
  const type = DeserializerBase.readString(input) as BattleType
  const nicknameTeamMap = DeserializerBase.readMap(input,
      DeserializerBase.readString, DeserializerBase.readInt) as NicknameTeamMap
  return {
    id,
    model,
    time,
    duration,
    fps,
    paused,
    battleStage,
    type,
    nicknameTeamMap
  }
}

export function deserializeBattleUpdate(input: DeserializerInput): BattleUpdate {
  const time = DeserializerBase.readLong(input)
  const fps = DeserializerBase.readInt(input)
  const stage = DeserializerBase.readNullable(input, DeserializerBase.readString) as BattleStage | undefined
  const state = DeserializerBase.readNullable(input, deserializeBattleModelState)
  const updates = DeserializerBase.readNullable(input, deserializeBattleModelUpdates)
  const events = DeserializerBase.readNullable(input, deserializeBattleModelEvents)
  const statistics = DeserializerBase.readMap(input, DeserializerBase.readString, deserializePlayerBattleStatistics)
  return {
    time,
    fps,
    stage,
    state,
    updates,
    events,
    statistics
  }
}
