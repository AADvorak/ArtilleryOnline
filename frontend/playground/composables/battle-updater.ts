import {useBattleStore} from '~/stores/battle'
import type {Battle, BattleUpdate} from '@/playground/data/battle'
import {useSettingsStore} from "~/stores/settings";
import {useStompClientStore} from "~/stores/stomp-client";
import type {StompSubscription} from "@stomp/stompjs";
import {useEventSoundsPlayer} from "~/playground/composables/sound/event-sounds-player";
import type {Player} from "~/playground/audio/player";
import {deserializeBattle, deserializeBattleUpdate} from "~/playground/data/battle-deserialize";
import {DeserializerInput} from "~/deserialization/deserializer-input";
import type {VehicleModel} from "~/playground/data/model";
import type {VehicleState} from "~/playground/data/state";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {DefaultColors} from "~/dictionary/default-colors";
import {RepairEventType} from "~/playground/data/events";

export function useBattleUpdater(player: Player) {
  const battleStore = useBattleStore()
  const settingsStore = useSettingsStore()
  const stompClientStore = useStompClientStore()
  const eventSoundsPlayer = useEventSoundsPlayer(player)

  const subscriptions: StompSubscription[] = []

  function subscribe() {
    const battle = battleStore.battle as Battle
    subscriptions.push(stompClientStore.client!.subscribe('/topic/battle/' + battle.id, function (msgOut) {
      const battleBinary = msgOut.binaryBody.buffer
      // @ts-ignore
      const battle = deserializeBattle(new DeserializerInput(battleBinary))
      if (battle) {
        updateBattle(battle)
      }
    }))
    subscriptions.push(stompClientStore.client!.subscribe('/topic/battle/' + battle.id + '/updates', function (msgOut) {
      const battleUpdateBinary = msgOut.binaryBody.buffer
      // @ts-ignore
      const battleUpdate = deserializeBattleUpdate(new DeserializerInput(battleUpdateBinary))
      if (battleUpdate) {
        updateBattleState(battleUpdate)
      }
    }))
  }

  function unsubscribe() {
    subscriptions.forEach(subscription => subscription.unsubscribe())
  }

  function updateBattle(battle: Battle) {
    if (!battleStore.battle) {
      return
    }
    !battle.paused || battle.model.updated || !settingsStore.settings!.clientProcessing
        ? battleStore.updateBattle(battle) : battleStore.updateServerBattle(battle)
  }

  function updateBattleState(battleUpdate: BattleUpdate) {
    if (!battleStore.battle) {
      return
    }
    const battle = JSON.parse(JSON.stringify(battleStore.battle)) as Battle
    eventSoundsPlayer.playSounds(battleUpdate, battle)
    battle.time = battleUpdate.time
    battle.fps = battleUpdate.fps
    if (battleUpdate.stage) {
      battle.battleStage = battleUpdate.stage
    }
    if (battleUpdate.updates) {
      if (battleUpdate.updates.added) {
        const addedShells = battleUpdate.updates.added.shells
        if (addedShells) {
          addedShells.forEach(shell => battle.model.shells[shell.id] = shell)
        }
        const addedMissiles = battleUpdate.updates.added.missiles
        if (addedMissiles) {
          addedMissiles.forEach(missile => battle.model.missiles[missile.id] = missile)
        }
        const addedExplosions = battleUpdate.updates.added.explosions
        if (addedExplosions) {
          addedExplosions.forEach(explosion => battle.model.explosions[explosion.id] = explosion)
        }
        const addedDrones = battleUpdate.updates.added.drones
        if (addedDrones) {
          addedDrones.forEach(drone => battle.model.drones[drone.id] = drone)
        }
        const addedBoxes = battleUpdate.updates.added.boxes
        if (addedBoxes) {
          addedBoxes.forEach(box => battle.model.boxes[box.id] = box)
        }
      }
      if (battleUpdate.updates.removed) {
        const removedShellIds = battleUpdate.updates.removed.shellIds
        if (removedShellIds) {
          removedShellIds.forEach(shellId => delete battle.model.shells[shellId])
        }
        const removedMissileIds = battleUpdate.updates.removed.missileIds
        if (removedMissileIds) {
          removedMissileIds.forEach(missileId => delete battle.model.missiles[missileId])
        }
        const removedDroneIds = battleUpdate.updates.removed.droneIds
        if (removedDroneIds) {
          removedDroneIds.forEach(droneId => delete battle.model.drones[droneId])
        }
        const removedExplosionIds = battleUpdate.updates.removed.explosionIds
        if (removedExplosionIds) {
          removedExplosionIds.forEach(explosionId => delete battle.model.explosions[explosionId])
        }
        const removedBoxIds = battleUpdate.updates.removed.boxIds
        if (removedBoxIds) {
          removedBoxIds.forEach(boxId => delete battle.model.boxes[boxId])
        }
        const removedVehicleKeys = battleUpdate.updates.removed.vehicleKeys
        if (removedVehicleKeys) {
          removedVehicleKeys.forEach(vehicleKey => delete battle.model.vehicles[vehicleKey])
        }
      }
      if (battleUpdate.updates.roomStateUpdates) {
        for (const roomStateUpdate of battleUpdate.updates.roomStateUpdates) {
          for (let index = 0; index < roomStateUpdate.groundLinePart.length; index++) {
            battle.model.room.state.groundLine[roomStateUpdate.begin + index] = roomStateUpdate.groundLinePart[index]!
          }
        }
      }
    }
    if (battleUpdate.state) {
      const vehicles = battleUpdate.state.vehicles
      if (vehicles) {
        Object.keys(vehicles).forEach(key => {
          const model = battle.model.vehicles[key]
          if (model) {
            const newState = vehicles[key]!
            showChangeHp(model, model.state, newState)
            model.state = newState
          }
        })
      }
      const shells = battleUpdate.state.shells
      if (shells) {
        Object.keys(shells).forEach(key => {
          // @ts-ignore
          battle.model.shells[key].state = shells[key]
        })
      }
      const missiles = battleUpdate.state.missiles
      if (missiles) {
        Object.keys(missiles).forEach(key => {
          // @ts-ignore
          battle.model.missiles[key].state = missiles[key]
        })
      }
      const drones = battleUpdate.state.drones
      if (drones) {
        Object.keys(drones).forEach(key => {
          // @ts-ignore
          battle.model.drones[key].state = drones[key]
        })
      }
      const boxes = battleUpdate.state.boxes
      if (boxes) {
        Object.keys(boxes).forEach(key => {
          // @ts-ignore
          battle.model.boxes[key].state = boxes[key]
        })
      }
    }
    const repairs = battleUpdate.events?.repairs
    if (repairs) {
      repairs
          .filter(repair => repair.type === RepairEventType.REFILL_AMMO)
          .forEach(repair => {
            const vehicleModel = Object.values(battle.model.vehicles)
                .filter(vehicle => vehicle.id === repair.vehicleId)[0]
            vehicleModel && showChangeAmmo(vehicleModel)
          })
    }
    battleStore.updateBattle(battle)
  }

  function showChangeHp(model: VehicleModel, oldState: VehicleState, newState: VehicleState) {
    const hpDiff = newState.hitPoints - oldState.hitPoints
    if (Math.abs(hpDiff) > 1) {
      const position = BattleUtils.shiftedPosition(model.state.position, model.preCalc.maxRadius, Math.PI / 2)
      const color = hpDiff > 0 ? DefaultColors.BRIGHT_GREEN : DefaultColors.BRIGHT_RED
      const hpDiffToFixed = hpDiff.toFixed(0)
      const text = hpDiff > 0 ? '+' + hpDiffToFixed : hpDiffToFixed
      battleStore.addParticle(BattleUtils.generateParticle(position, 1.0), {color, text})
    }
  }

  function showChangeAmmo(model: VehicleModel) {
    const position = BattleUtils.shiftedPosition(model.state.position, model.preCalc.maxRadius, Math.PI / 2)
    battleStore.addParticle(BattleUtils.generateParticle(position, 1.0),
        {color: DefaultColors.BRIGHT_GREEN, text: '+ammo'})
  }

  return { subscribe, unsubscribe }
}
