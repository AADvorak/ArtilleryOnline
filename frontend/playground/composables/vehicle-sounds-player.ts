import type {AudioControl, Player} from "~/playground/audio/player";
import {useBattleStore} from "~/stores/battle";
import type {VehicleState} from "~/playground/data/state";
import type {RoomSpecs} from "~/playground/data/specs";
import type {VehicleModels} from "~/playground/data/model";
import {useUserSettingsStore} from "~/stores/user-settings";
import {SoundSettingsNames} from "~/dictionary/sound-settings-names";

interface VehicleAudioControls {
  [userKey: string]: AudioControl | undefined
}

const TRACK_KEY = 'Track'
const ENGINE_KEY = 'Engine'
const GUN_KEY = 'Gun'

export function useVehicleSoundsPlayer(player: Player) {
  const vehicleAudioControls: VehicleAudioControls = {}

  const battleStore = useBattleStore()

  const vehicles = computed(() => battleStore.vehicles)

  function start() {
    setTimeout(playSounds, 100)
  }

  async function playSounds() {
    if (useUserSettingsStore().soundSettingsOrDefaultsNameValueMapping[SoundSettingsNames.ENABLE] !== '1') {
      return
    }
    if (vehicles.value) {
      await playVehicleMove(vehicles.value!, battleStore.battle?.model.room.specs!)
      setTimeout(playSounds, 100)
    } else {
      stopAll()
    }
  }

  async function playVehicleMove(vehicles: VehicleModels, roomSpecs: RoomSpecs) {
    const keys = Object.keys(vehicles)
    for (const key of keys) {
      const vehicleState = vehicles[key].state
      const pan = calculatePan(vehicleState.position.x, roomSpecs)
      await playVehicleSound(key, TRACK_KEY, pan, isMovingOnGround(vehicleState), 'vehicle-move-medium.mp3', fadeOutAndStop)
      await playVehicleSound(key, ENGINE_KEY, pan, isEngineActive(vehicleState), 'vehicle-engine.mp3', fadeOutAndStop)
      await playVehicleSound(key, GUN_KEY, pan, !!vehicleState.gunRotatingDirection, 'gun-turn.wav', stopLoop)
    }
  }

  async function playVehicleSound(key: string, addKey: string, pan: number, condition: boolean, file: string,
                                  stopFunction: (audioControl: AudioControl) => void) {
    const fullKey = key + addKey
    const audioControl = vehicleAudioControls[fullKey]
    if (condition && !audioControl) {
      const newAudioControl = await playLooped(file, pan)
      if (newAudioControl) {
        vehicleAudioControls[fullKey] = newAudioControl
        newAudioControl.source.addEventListener('ended', () => {
          vehicleAudioControls[fullKey] = undefined
        })
      }
    }
    if (condition && audioControl) {
      audioControl.panner.pan.value = pan
    }
    if (!condition && audioControl) {
      stopFunction(audioControl)
    }
  }

  function fadeOutAndStop(audioControl: AudioControl) {
    const decay = 500
    audioControl.gainNode.gain.linearRampToValueAtTime(0, audioControl.audioCtx.currentTime + decay / 1000)
    setTimeout(() => {
      audioControl.source.stop()
    }, decay)
  }

  function stopLoop(audioControl: AudioControl) {
    audioControl.source.loop = false
  }

  function stopAll() {
    Object.keys(vehicleAudioControls).forEach(key => {
      for (const addKey of [TRACK_KEY, ENGINE_KEY, GUN_KEY]) {
        const fullKey = key + addKey
        const audioControl = vehicleAudioControls[fullKey]
        if (audioControl) {
          audioControl.source.stop()
          delete vehicleAudioControls[fullKey]
        }
      }
    })
  }

  function isMovingOnGround(vehicleState: VehicleState) {
    const sqrVelocity = Math.pow(vehicleState.velocity.x, 2) + Math.pow(vehicleState.velocity.y, 2)
    return sqrVelocity > 0.1 && vehicleState.onGround
  }

  function isEngineActive(vehicleState: VehicleState) {
    return vehicleState.movingDirection && !vehicleState.jetState.active
  }

  async function playLooped(fileName: string, pan: number): Promise<AudioControl | undefined> {
    return await player.play('/sounds/' + fileName, pan, true)
  }

  function calculatePan(x: number, roomSpecs: RoomSpecs) {
    const xMin = roomSpecs.leftBottom.x
    const xMax = roomSpecs.rightTop.x
    return 2 * (x - xMax) / (xMax - xMin) + 1
  }

  return {start}
}
