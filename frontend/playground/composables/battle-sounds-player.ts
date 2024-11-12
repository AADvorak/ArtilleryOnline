import type {Battle, BattleUpdate} from "~/playground/data/battle";
import {usePlayer} from "~/playground/audio/player";
import {ShellHitType, ShellType} from "~/playground/data/common";
import {useUserSettingsStore} from "~/stores/user-settings";
import {SoundSettingsNames} from "~/dictionary/sound-settings-names";

export function useBattleSoundsPlayer() {
  function playSounds(battleUpdate: BattleUpdate, battle: Battle) {
    if (useUserSettingsStore().soundSettingsOrDefaultsNameValueMapping[SoundSettingsNames.ENABLE] !== '1') {
      return
    }
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
    if (battleUpdate.updates) {
      if (battleUpdate.updates.added) {
        const addedShells = battleUpdate.updates.added.shells
        if (addedShells) {
          addedShells.forEach(shell => playShot(shell.specs.caliber))
        }
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

  return {playSounds}
}
