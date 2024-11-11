import {useBattleStore} from '~/stores/battle'
import type {Battle, BattleUpdate} from '@/playground/data/battle'
import {useSettingsStore} from "~/stores/settings";
import {useStompClientStore} from "~/stores/stomp-client";
import type {StompSubscription} from "@stomp/stompjs";
import {usePlayer} from "~/playground/audio/player";
import {ShellHitType, ShellType} from "~/playground/data/common";

export function useBattleUpdater() {
  const battleStore = useBattleStore()
  const settingsStore = useSettingsStore()
  const stompClientStore = useStompClientStore()

  const subscriptions: StompSubscription[] = []

  function subscribe() {
    const battle = battleStore.battle as Battle
    subscriptions.push(stompClientStore.client!.subscribe('/topic/battle/' + battle.id, function (msgOut) {
      const battle = JSON.parse(msgOut.body) as Battle
      if (battle) {
        updateBattle(battle)
      }
    }))
    subscriptions.push(stompClientStore.client!.subscribe('/topic/battle/' + battle.id + '/updates', function (msgOut) {
      const battleUpdate = JSON.parse(msgOut.body) as BattleUpdate
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
    processBattleEvents(battleUpdate, battle)
    battle.time = battleUpdate.time
    if (battleUpdate.updates) {
      if (battleUpdate.updates.added) {
        const addedShells = battleUpdate.updates.added.shells
        if (addedShells) {
          addedShells.forEach(shell => {
            battle.model.shells[shell.id] = shell
            playShot(shell.specs.caliber)
          })
        }
        const addedExplosions = battleUpdate.updates.added.explosions
        if (addedExplosions) {
          addedExplosions.forEach(explosion => battle.model.explosions[explosion.id] = explosion)
        }
      }
      if (battleUpdate.updates.removed) {
        const removedShellIds = battleUpdate.updates.removed.shellIds
        if (removedShellIds) {
          removedShellIds.forEach(shellId => delete battle.model.shells[shellId])
        }
        const removedExplosionIds = battleUpdate.updates.removed.explosionIds
        if (removedExplosionIds) {
          removedExplosionIds.forEach(explosionId => delete battle.model.explosions[explosionId])
        }
      }
      if (battleUpdate.updates.roomStateUpdates) {
        for (const roomStateUpdate of battleUpdate.updates.roomStateUpdates) {
          for (let index = 0; index < roomStateUpdate.groundLinePart.length; index++) {
            battle.model.room.state.groundLine[roomStateUpdate.begin + index] = roomStateUpdate.groundLinePart[index]
          }
        }
      }
    }
    if (battleUpdate.state) {
      Object.keys(battleUpdate.state.vehicles).forEach(key => {
        battle.model.vehicles[key].state = battleUpdate.state.vehicles[key]
      })
      Object.keys(battleUpdate.state.shells).forEach(key => {
        battle.model.shells[key].state = battleUpdate.state.shells[key]
      })
    }
    battleStore.updateBattle(battle)
  }

  function processBattleEvents(battleUpdate: BattleUpdate, battle: Battle) {
    if (battleUpdate.events) {
      if (battleUpdate.events.hits) {
        battleUpdate.events.hits.forEach(hit => {
          const shellSpecs = battle.model.shells[hit.shellId].specs
          const shellType = shellSpecs.type
          const caliber = shellSpecs.caliber
          const hitType = hit.object.type
          if (shellType === ShellType.HE) {
            play('he-explosion')
          } else if (shellType === ShellType.AP) {
            if (hitType === ShellHitType.GROUND) {
              if (caliber > 0.06) {
                play('ap-hit-ground-large')
              } else {
                play('ap-hit-ground')
              }
            } else if (hitType === ShellHitType.VEHICLE_HULL) {
              play('ap-hit-vehicle')
            } else if (hitType === ShellHitType.VEHICLE_TRACK) {
              play('ap-hit-vehicle')
            }
          }
        })
      }
    }
  }

  function playShot(caliber: number) {
    if (caliber <= 0.04) {
      play('shot-small')
    } else if (caliber <= 0.06) {
      play('shot-mid')
    } else {
      play('shot-large')
    }
  }

  function play(fileName: string) {
    setTimeout(() => usePlayer().play('/sounds/' + fileName + '.wav').then())
  }

  return { subscribe, unsubscribe }
}
