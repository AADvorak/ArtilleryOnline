import type {Battle, BattleUpdate} from "~/playground/data/battle";
import {usePlayer} from "~/playground/audio/player";
import {ShellHitType, ShellType} from "~/playground/data/common";
import {useUserSettingsStore} from "~/stores/user-settings";
import {SoundSettingsNames} from "~/dictionary/sound-settings-names";
import type {RoomSpecs} from "~/playground/data/specs";
import type {ShellModel} from "~/playground/data/model";

export function useBattleSoundsPlayer() {
  function playSounds(battleUpdate: BattleUpdate, battle: Battle) {
    if (useUserSettingsStore().soundSettingsOrDefaultsNameValueMapping[SoundSettingsNames.ENABLE] !== '1') {
      return
    }
    if (battleUpdate.events) {
      if (battleUpdate.events.hits) {
        battleUpdate.events.hits.forEach(hit => {
          const shell = battle.model.shells[hit.shellId]
          const shellSpecs = shell.specs
          const shellType = shellSpecs.type
          const caliber = shellSpecs.caliber
          const hitType = hit.object.type
          const pan = calculatePan(shell.state.position.x, battle.model.room.specs)
          if (shellType === ShellType.HE) {
            play('he-explosion', pan)
          } else if (shellType === ShellType.AP) {
            if (hitType === ShellHitType.GROUND) {
              if (caliber > 0.06) {
                play('ap-hit-ground-large', pan)
              } else {
                play('ap-hit-ground', pan)
              }
            } else if (hitType === ShellHitType.VEHICLE_HULL) {
              play('ap-hit-vehicle', pan)
            } else if (hitType === ShellHitType.VEHICLE_TRACK) {
              play('ap-hit-vehicle', pan)
            }
          }
        })
      }
    }
    if (battleUpdate.updates) {
      if (battleUpdate.updates.added) {
        const addedShells = battleUpdate.updates.added.shells
        if (addedShells) {
          addedShells.forEach(shell => playShot(shell, battle.model.room.specs))
        }
      }
    }
  }

  function playShot(shell: ShellModel, roomSpecs: RoomSpecs) {
    const caliber = shell.specs.caliber
    const pan = calculatePan(shell.state.position.x, roomSpecs)
    if (caliber <= 0.04) {
      play('shot-small', pan)
    } else if (caliber <= 0.06) {
      play('shot-mid', pan)
    } else {
      play('shot-large', pan)
    }
  }

  function play(fileName: string, pan: number) {
    console.log(pan)
    setTimeout(() => usePlayer().play('/sounds/' + fileName + '.wav', pan).then())
  }

  function calculatePan(x: number, roomSpecs: RoomSpecs) {
    const xMin = roomSpecs.leftBottom.x
    const xMax = roomSpecs.rightTop.x
    return 2 * (x - xMax) / (xMax - xMin) + 1
  }

  return {playSounds}
}
