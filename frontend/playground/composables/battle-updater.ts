import {useBattleStore} from '~/stores/battle'
import type {Battle, BattleUpdate} from '@/playground/data/battle'
import {useSettingsStore} from "~/stores/settings";
import {useStompClientStore} from "~/stores/stomp-client";
import type {StompSubscription} from "@stomp/stompjs";
import {useEventSoundsPlayer} from "~/playground/composables/sound/event-sounds-player";
import type {Player} from "~/playground/audio/player";
import {deserializeBattle, deserializeBattleUpdate} from "~/playground/data/battle-deserialize";
import {DeserializerInput} from "~/deserialization/deserializer-input";
import type {BodyModel, ShellModel} from "~/playground/data/model";
import type {BodyState, ShellState} from "~/playground/data/state";
import {type Position} from "~/playground/data/common";
import {Constants} from "~/playground/data/constants";
import {useBattleUpdateParticlesGenerator} from "~/playground/composables/battle-update-particles-generator";

const NEED_SMOOTH_TRANSITION_THRESHOLD = 2 * Constants.MAX_TRANSITION_VELOCITY * Constants.TIME_STEP_MS / 1000
const BATTLE_OUTDATED_THRESHOLD = 50
const MAX_REJECTED_UPDATES_COUNT = 2

export function useBattleUpdater(player: Player) {
  const battleStore = useBattleStore()
  const settingsStore = useSettingsStore()
  const stompClientStore = useStompClientStore()
  const eventSoundsPlayer = useEventSoundsPlayer(player)
  const particlesGenerator = useBattleUpdateParticlesGenerator()

  const subscriptions: StompSubscription[] = []

  let rejectedUpdatesCount = 0

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
    !settingsStore.settings!.clientProcessing
        ? battleStore.updateBattle(battle) : battleStore.updateServerBattle(battle)
  }

  function updateBattleState(battleUpdate: BattleUpdate) {
    if (!battleStore.battle) {
      return
    }
    const clientSmoothTransition = settingsStore.settings!.clientProcessing
        && settingsStore.settings!.clientSmoothTransition && !battleUpdate.stage
    const battle = structuredClone(toRaw(battleStore.serverBattle!))
    eventSoundsPlayer.playSounds(battleUpdate, battle)
    let battleOutdated = false
    if (!battleUpdate.stage && rejectedUpdatesCount < MAX_REJECTED_UPDATES_COUNT
        && battle.time - battleUpdate.time > BATTLE_OUTDATED_THRESHOLD) {
      battleOutdated = true
      rejectedUpdatesCount++
    } else {
      battle.time = battleUpdate.time
      rejectedUpdatesCount = 0
    }
    battle.fps = battleUpdate.fps
    if (battleUpdate.stage) {
      battle.battleStage = battleUpdate.stage
    }
    particlesGenerator.generate(battleUpdate, battle.model)

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
      const groundLine = battle.model.room.state.groundLine
      if (battleUpdate.updates.roomStateUpdates && groundLine) {
        for (const roomStateUpdate of battleUpdate.updates.roomStateUpdates) {
          for (let index = 0; index < roomStateUpdate.groundLinePart.length; index++) {
            groundLine[roomStateUpdate.begin + index] = roomStateUpdate.groundLinePart[index]!
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
            if (battleOutdated) {
              applyExceptPositionAndVelocity(model, newState)
            } else {
              clientSmoothTransition && checkNeedSmoothTransition(model.state.position, newState.position)
              model.state = newState
            }
          }
        })
      }
      const shells = battleUpdate.state.shells
      if (shells) {
        Object.keys(shells).forEach(key => {
          // @ts-ignore
          const model = battle.model.shells[key]
          if (model) {
            // @ts-ignore
            const newState = shells[key]!
            if (battleOutdated) {
              applyShellExceptPositionAndVelocity(model, newState)
            } else {
              clientSmoothTransition && checkNeedSmoothTransition(model.state.position, newState.position)
              model.state = newState
            }
          }
        })
      }
      const missiles = battleUpdate.state.missiles
      if (missiles) {
        Object.keys(missiles).forEach(key => {
          // @ts-ignore
          const model = battle.model.missiles[key]
          if (model) {
            // @ts-ignore
            const newState = missiles[key]!
            if (battleOutdated) {
              // @ts-ignore
              applyExceptPositionAndVelocity(model, newState)
            } else {
              clientSmoothTransition && checkNeedSmoothTransition(model.state.position, newState.position)
              model.state = newState
            }
          }
        })
      }
      const drones = battleUpdate.state.drones
      if (drones) {
        Object.keys(drones).forEach(key => {
          // @ts-ignore
          const model = battle.model.drones[key]
          if (model) {
            // @ts-ignore
            const newState = drones[key]!
            if (battleOutdated) {
              applyExceptPositionAndVelocity(model, newState)
            } else {
              clientSmoothTransition && checkNeedSmoothTransition(model.state.position, newState.position)
              model.state = newState
            }
          }
        })
      }
      const boxes = battleUpdate.state.boxes
      if (boxes) {
        Object.keys(boxes).forEach(key => {
          // @ts-ignore
          const model = battle.model.boxes[key]
          if (model) {
            // @ts-ignore
            const newState = boxes[key]!
            if (battleOutdated) {
              applyExceptPositionAndVelocity(model, newState)
            } else {
              clientSmoothTransition && checkNeedSmoothTransition(model.state.position, newState.position)
              model.state = newState
            }
          }
        })
      }
    }

    if (battleUpdate.statistics) {
      Object.keys(battleUpdate.statistics).forEach(nickname => {
        // @ts-ignore
        battle.model.statistics[nickname] = battleUpdate.statistics[nickname]
      })
    }

    clientSmoothTransition && battleStore.needSmoothTransition
        ? battleStore.updateServerBattle(battle) : battleStore.updateBattle(battle)
  }

  function checkNeedSmoothTransition(oldPosition: Position, newPosition: Position) {
    if (battleStore.needSmoothTransition) {
      return
    }
    if (Math.abs(oldPosition.x - newPosition.x) > NEED_SMOOTH_TRANSITION_THRESHOLD
        || Math.abs(oldPosition.y - newPosition.y) > NEED_SMOOTH_TRANSITION_THRESHOLD) {
      battleStore.needSmoothTransition = true
    }
  }

  function applyExceptPositionAndVelocity(model: BodyModel, state: BodyState) {
    const position = {
      x: model.state.position.x,
      y: model.state.position.y,
      angle: model.state.position.angle,
    }
    const velocity = {
      x: model.state.velocity.x,
      y: model.state.velocity.y,
      angle: model.state.velocity.angle,
    }
    model.state = state
    model.state.position = position
    model.state.velocity = velocity
  }

  function applyShellExceptPositionAndVelocity(model: ShellModel, state: ShellState) {
    const position = {
      x: model.state.position.x,
      y: model.state.position.y,
    }
    const velocity = {
      x: model.state.velocity.x,
      y: model.state.velocity.y,
    }
    model.state = state
    model.state.position = position
    model.state.velocity = velocity
  }

  return { subscribe, unsubscribe }
}
