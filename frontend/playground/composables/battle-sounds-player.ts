import type {Battle, BattleUpdate} from "~/playground/data/battle";
import type {Player} from "~/playground/audio/player";
import {CollideObjectType, ShellHitType, ShellType} from "~/playground/data/common";
import {useUserSettingsStore} from "~/stores/user-settings";
import {SoundSettingsNames} from "~/dictionary/sound-settings-names";
import type {ShellModel, VehicleModel} from "~/playground/data/model";
import {useSoundsPlayerBase} from "~/playground/composables/sounds-player-base";
import {useUserStore} from "~/stores/user";

export function useBattleSoundsPlayer(player: Player) {
  const soundsPlayerBase = useSoundsPlayerBase()
  const userStore = useUserStore()

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
          const pan = soundsPlayerBase.calculatePan(shell.state.position.x)
          const gain = soundsPlayerBase.calculateGain(shell.state.position)
          const fileName = getHitSoundName(shellType, hitType, caliber)
          fileName && play(fileName, pan, gain)
        })
      }
      if (battleUpdate.events.collides) {
        battleUpdate.events.collides.forEach(collide => {
          const vehicle = Object.values(battle.model.vehicles)
              .filter(vehicle => vehicle.id === collide.vehicleId)[0]
          const pan = soundsPlayerBase.calculatePan(vehicle.state.position.x)
          const gain = soundsPlayerBase.calculateGain(vehicle.state.position)
          const fileName = getCollideSoundName(collide.object.type)
          fileName && play(fileName, pan, gain)
        })
      }
    }
    if (battleUpdate.updates) {
      if (battleUpdate.updates.added) {
        const addedShells = battleUpdate.updates.added.shells
        if (addedShells) {
          addedShells.forEach(shell => playShot(shell))
        }
      }
      if (battleUpdate.updates.removed) {
        const removedVehicleKeys = battleUpdate.updates.removed.vehicleKeys
        if (removedVehicleKeys) {
          removedVehicleKeys.forEach(vehicleKey => playVehicleDestroy(battle.model.vehicles[vehicleKey]))
        }
      }
    }
    if (battleUpdate.state) {
      const prevLoadedShell = battle.model.vehicles[userStore.user!.nickname]?.state.gunState.loadedShell
      const loadedShell = battleUpdate.state.vehicles[userStore.user!.nickname]?.gunState.loadedShell
      if (loadedShell && !prevLoadedShell) {
        play('reload', 0, 1)
      }
    }
  }

  function playVehicleDestroy(vehicle: VehicleModel) {
    const pan = soundsPlayerBase.calculatePan(vehicle.state.position.x)
    const gain = soundsPlayerBase.calculateGain(vehicle.state.position)
    play('vehicle-destroy', pan, gain)
  }

  function playShot(shell: ShellModel) {
    const caliber = shell.specs.caliber
    const pan = soundsPlayerBase.calculatePan(shell.state.position.x)
    const gain = soundsPlayerBase.calculateGain(shell.state.position)
    if (caliber <= 0.04) {
      play('shot-small', pan, gain)
    } else if (caliber <= 0.06) {
      play('shot-mid', pan, gain)
    } else {
      play('shot-large', pan, gain)
    }
  }

  function getHitSoundName(shellType: ShellType, hitType: ShellHitType, caliber: number): string | undefined {
    if (shellType === ShellType.HE) {
      return 'he-explosion'
    } else if (shellType === ShellType.AP) {
      if (hitType === ShellHitType.GROUND) {
        if (caliber > 0.06) {
          return 'ap-hit-ground-large'
        } else {
          return 'ap-hit-ground'
        }
      } else if (hitType === ShellHitType.VEHICLE_HULL) {
        if (caliber > 0.06) {
          return 'ap-hit-vehicle-large'
        } else if (caliber > 0.04) {
          return 'ap-hit-vehicle-medium'
        } else {
          return 'ap-hit-vehicle-small'
        }
      } else if (hitType === ShellHitType.VEHICLE_TRACK) {
        // todo different sounds for tracks
        if (caliber > 0.06) {
          return 'ap-hit-vehicle-large'
        } else if (caliber > 0.04) {
          return 'ap-hit-vehicle-medium'
        } else {
          return 'ap-hit-vehicle-small'
        }
      }
    }
  }

  function getCollideSoundName(type: CollideObjectType): string | undefined {
    if (type === CollideObjectType.GROUND) {
      return 'collide-ground'
    } else if (type === CollideObjectType.WALL) {
      return 'collide-wall'
    } else if (type === CollideObjectType.VEHICLE) {
      return 'collide-vehicle-' + (Math.floor(Math.random() * 4) + 1)
    }
  }

  function play(fileName: string, pan: number, gain: number) {
    setTimeout(() => player.play('/sounds/' + fileName + '.wav', pan, gain))
  }

  return {playSounds}
}
