import type {Battle, BattleUpdate} from "~/playground/data/battle";
import type {Player} from "~/playground/audio/player";
import {type BodyPosition, CollideObjectType, MovingDirection, ShellHitType, ShellType} from "~/playground/data/common";
import {useUserSettingsStore} from "~/stores/user-settings";
import {SoundSettingsNames} from "~/dictionary/sound-settings-names";
import type {
  BattleModel,
  DroneModel,
  MissileModel,
  ShellModel,
  ShellModels,
  VehicleModel,
  VehicleModels
} from "~/playground/data/model";
import {useSoundsPlayerBase} from "~/playground/composables/sound/sounds-player-base";
import {useUserStore} from "~/stores/user";
import type {VehicleStates} from "~/playground/data/state";
import {
  type BomberFlyEvent,
  type CollideEvent,
  type RepairEvent,
  RepairEventType,
  type RicochetEvent,
  type ShellHitEvent
} from "~/playground/data/events";

export function useEventSoundsPlayer(player: Player) {
  const soundsPlayerBase = useSoundsPlayerBase()
  const userStore = useUserStore()

  function playSounds(battleUpdate: BattleUpdate, battle: Battle) {
    if (useUserSettingsStore().soundSettingsOrDefaultsNameValueMapping[SoundSettingsNames.ENABLE] !== '1') {
      return
    }
    if (battleUpdate.events) {
      if (battleUpdate.events.hits) {
        battleUpdate.events.hits.forEach(hit => playHit(hit, battle.model.shells))
      }
      if (battleUpdate.events.collides) {
        battleUpdate.events.collides.forEach(collide => playCollide(collide, battle.model))
      }
      if (battleUpdate.events.ricochets) {
        battleUpdate.events.ricochets.forEach(ricochet => playRicochet(ricochet, battle.model.shells))
      }
      if (battleUpdate.events.bomberFlyEvents) {
        battleUpdate.events.bomberFlyEvents.forEach(bomberFly => playBomberFly(bomberFly))
      }
      if (battleUpdate.events.repairs) {
        battleUpdate.events.repairs.forEach(repair => playRepair(repair, battle.model.vehicles))
      }
    }
    if (battleUpdate.updates) {
      if (battleUpdate.updates.added) {
        const addedShells = battleUpdate.updates.added.shells
        if (addedShells) {
          addedShells.forEach(shell => playShot(shell))
        }
        const addedMissiles = battleUpdate.updates.added.missiles
        if (addedMissiles) {
          addedMissiles.forEach(missile => playMissileLaunch(missile))
        }
      }
      if (battleUpdate.updates.removed) {
        const removedVehicleKeys = battleUpdate.updates.removed.vehicleKeys
        if (removedVehicleKeys) {
          removedVehicleKeys.forEach(vehicleKey => playVehicleDestroy(battle.model.vehicles[vehicleKey]))
        }
        const removedMissileIds = battleUpdate.updates.removed.missileIds
        if (removedMissileIds) {
          removedMissileIds.forEach(missileId => playMissileExplosion(battle.model.missiles[missileId]))
        }
        const removedDroneIds = battleUpdate.updates.removed.droneIds
        if (removedDroneIds) {
          removedDroneIds.forEach(droneId => {
            if (isDroneDestroyed(droneId, battleUpdate)) {
              playDroneDestroy(battle.model.drones[droneId])
            }
          })
        }
      }
    }
    if (battleUpdate.state?.vehicles) {
      const prevLoadedShell = battle.model.vehicles[userStore.user!.nickname]?.state.gunState.loadedShell
      const loadedShell = battleUpdate.state.vehicles[userStore.user!.nickname]?.gunState.loadedShell
      if (loadedShell && !prevLoadedShell) {
        play('reload', 0, 1)
      }
      playTracksBroken(battleUpdate.state.vehicles, battle.model.vehicles)
    }
  }

  function playHit(hit: ShellHitEvent, shellsModels: ShellModels) {
    const shell = shellsModels[hit.shellId]
    const shellSpecs = shell.specs
    const shellType = shellSpecs.type
    const caliber = shellSpecs.caliber
    const hitType = hit.object.type
    const pan = soundsPlayerBase.calculatePan(shell.state.position.x)
    const gain = soundsPlayerBase.calculateGain(shell.state.position)
    const fileName = getHitSoundName(shellType, hitType, caliber)
    fileName && play(fileName, pan, gain)
  }

  function playRicochet(ricochet: RicochetEvent, shellsModels: ShellModels) {
    const shell = shellsModels[ricochet.shellId]
    const pan = soundsPlayerBase.calculatePan(shell.state.position.x)
    const gain = soundsPlayerBase.calculateGain(shell.state.position)
    const fileName = getRicochetSoundName()
    fileName && play(fileName, pan, gain)
  }

  function playBomberFly(bomberFlyEvent: BomberFlyEvent) {
    const fileName = MovingDirection.LEFT === bomberFlyEvent.movingDirection
        ? 'bomber-right-left' : 'bomber-left-right'
    play(fileName, 0.0, 1.0)
  }

  function playRepair(repair: RepairEvent, vehicleModels: VehicleModels) {
    const vehicle = Object.values(vehicleModels)
        .filter(vehicle => vehicle.id === repair.vehicleId)[0]
    const pan = soundsPlayerBase.calculatePan(vehicle.state.position.x)
    const gain = soundsPlayerBase.calculateGain(vehicle.state.position)
    const fileName = repair.type === RepairEventType.HEAL ? 'use-box' : 'vehicle-repair'
    play(fileName, pan, gain)
  }

  function playCollide(collide: CollideEvent, battleModel: BattleModel) {
    const position = getCollidePosition(collide, battleModel)
    if (position) {
      const pan = soundsPlayerBase.calculatePan(position.x)
      const gain = soundsPlayerBase.calculateGain(position)
      const fileName = getCollideSoundName(collide.type === CollideObjectType.BOX
          ? collide.type : collide.object.type)
      fileName && play(fileName, pan, gain)
    }
  }

  function getCollidePosition(collide: CollideEvent, battleModel: BattleModel): BodyPosition | undefined {
    if (collide.type === CollideObjectType.VEHICLE) {
      return (Object.values(battleModel.vehicles)
          .filter(vehicle => vehicle.id === collide.id)[0]).state.position
    } else if (collide.type === CollideObjectType.BOX) {
      return (Object.values(battleModel.boxes)
          .filter(box => box.id === collide.id)[0]).state.position
    }
  }

  function playTracksBroken(vehicleStates: VehicleStates, vehicleModels: VehicleModels) {
    Object.keys(vehicleStates).forEach(key => {
      const vehicleState = vehicleStates[key]
      const nowBroken = vehicleState.trackState.broken
      const wasBroken = vehicleModels[key].state.trackState.broken
      if (nowBroken && !wasBroken) {
        const pan = soundsPlayerBase.calculatePan(vehicleState.position.x)
        const gain = soundsPlayerBase.calculateGain(vehicleState.position)
        const fileName = getBrokenTrackSoundName()
        fileName && play(fileName, pan, gain)
      }
    })
  }

  function playVehicleDestroy(vehicle: VehicleModel) {
    const pan = soundsPlayerBase.calculatePan(vehicle.state.position.x)
    const gain = soundsPlayerBase.calculateGain(vehicle.state.position)
    play('vehicle-destroy', pan, gain)
  }

  function isDroneDestroyed(droneId: number, battleUpdate: BattleUpdate) {
    const addedExplosions = battleUpdate.updates?.added?.explosions
    if (!addedExplosions || !addedExplosions.length) {
      return false
    }
    // todo search by parent id
    return true
  }

  function playDroneDestroy(drone: DroneModel) {
    const pan = soundsPlayerBase.calculatePan(drone.state.position.x)
    const gain = soundsPlayerBase.calculateGain(drone.state.position)
    play('drone-destroy', pan, gain)
  }

  function playShot(shell: ShellModel) {
    const caliber = shell.specs.caliber
    const pan = soundsPlayerBase.calculatePan(shell.state.position.x)
    const gain = soundsPlayerBase.calculateGain(shell.state.position)
    // todo how to check if bomb is shot or thrown
    if (shell.specs.type === ShellType.BMB && shell.specs.velocity < 10.0) {
      // todo sound
    } else if (shell.specs.type === ShellType.SGN || caliber <= 0.04) {
      play('shot-small', pan, gain)
    } else if (caliber <= 0.06) {
      play('shot-mid', pan, gain)
    } else {
      play('shot-large', pan, gain)
    }
  }

  function playMissileLaunch(missile: MissileModel) {
    const pan = soundsPlayerBase.calculatePan(missile.state.position.x)
    const gain = soundsPlayerBase.calculateGain(missile.state.position)
    play('missile-launch', pan, gain)
  }

  function playMissileExplosion(missile: MissileModel) {
    const pan = soundsPlayerBase.calculatePan(missile.state.position.x)
    const gain = soundsPlayerBase.calculateGain(missile.state.position)
    play('missile-explosion-' + (Math.ceil(Math.random() * 2)), pan, gain)
  }

  function getHitSoundName(shellType: ShellType, hitType: ShellHitType, caliber: number): string | undefined {
    if (shellType === ShellType.HE) {
      return 'he-explosion-' + (Math.ceil(Math.random() * 2))
    } else if (shellType === ShellType.BMB) {
      return 'bomb-explosion-' + (Math.ceil(Math.random() * 3))
    } else if (shellType === ShellType.AP) {
      if (hitType === ShellHitType.GROUND) {
        if (caliber > 0.06) {
          return 'ap-hit-ground-large-' + (Math.ceil(Math.random() * 2))
        } else {
          return 'ap-hit-ground-' + (Math.ceil(Math.random() * 3))
        }
      } else if (hitType === ShellHitType.VEHICLE_HULL || hitType === ShellHitType.VEHICLE_TRACK) {
        if (caliber > 0.06) {
          return 'ap-hit-vehicle-large'
        } else if (caliber > 0.04) {
          return 'ap-hit-vehicle-medium'
        } else {
          return 'ap-hit-vehicle-small'
        }
      } else if (hitType === ShellHitType.DRONE) {
        return 'ap-hit-drone-' + (Math.ceil(Math.random() * 4))
      } else if (hitType === ShellHitType.BOX) {
        return 'ap-hit-box-1'
      }
    }
  }

  function getBrokenTrackSoundName() {
    return 'track-broken-' + (Math.ceil(Math.random() * 3))
  }

  function getCollideSoundName(type: CollideObjectType): string | undefined {
    if (type === CollideObjectType.GROUND) {
      return 'collide-ground-' + (Math.ceil(Math.random() * 2))
    } else if (type === CollideObjectType.WALL) {
      return 'collide-wall'
    } else if (type === CollideObjectType.VEHICLE) {
      return 'collide-vehicle-' + (Math.ceil(Math.random() * 6))
    } else if (type === CollideObjectType.BOX) {
      return 'collide-box-' + (Math.ceil(Math.random() * 2))
    }
  }

  function getRicochetSoundName() {
    return 'ricochet-' + (Math.ceil(Math.random() * 2))
  }

  function play(fileName: string, pan: number, gain: number) {
    setTimeout(() => player.play({path: '/sounds/' +fileName + '.mp3',
      pan, gain, randomise: true}))
  }

  return {playSounds}
}
