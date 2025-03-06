import type {AudioControl, Player} from "~/playground/audio/player";
import {useBattleStore} from "~/stores/battle";
import type {VehicleState} from "~/playground/data/state";
import type {DroneModel, VehicleModels} from "~/playground/data/model";
import {useUserSettingsStore} from "~/stores/user-settings";
import {SoundSettingsNames} from "~/dictionary/sound-settings-names";
import {useSoundsPlayerBase} from "~/playground/composables/sound/sounds-player-base";
import {useUserStore} from "~/stores/user";
import {VehicleUtils} from "~/playground/utils/vehicle-utils";
import {BattleStage} from "~/playground/data/battle";

interface AudioControls {
  [key: string]: AudioControl | undefined
}

const VEHICLE_PREFIX = 'Vehicle'
const DRONE_PREFIX = 'Drone'

const TRACK_KEY = 'Track'
const ENGINE_KEY = 'Engine'
const GUN_KEY = 'Gun'
const JET_KEY = 'Jet'

const MIN_PLAYING_VELOCITY = 0.3
const MAX_PLAYING_VELOCITY = 5.0
const MIN_PLAYING_RATE = 0.5
const MAX_PLAYING_RATE = 1.5

export function useContinuousSoundsPlayer(player: Player) {
  const audioControls: AudioControls = {}

  const battleStore = useBattleStore()
  const userStore = useUserStore()
  const soundsPlayerBase = useSoundsPlayerBase()

  const vehicles = computed(() => battleStore.vehicles)
  const drones = computed(() => battleStore.drones)
  const battleIsFinished = computed(() => battleStore.battle?.battleStage === BattleStage.FINISHED)

  function start() {
    setTimeout(playSounds, 100)
  }

  async function playSounds() {
    if (useUserSettingsStore().soundSettingsOrDefaultsNameValueMapping[SoundSettingsNames.ENABLE] !== '1') {
      return
    }
    if (battleIsFinished.value) {
      stopAll()
      return
    }
    if (vehicles.value) {
      await playVehicleSounds(vehicles.value!)
    }
    await playDroneSounds()
    setTimeout(playSounds, 100)
  }

  async function playDroneSounds() {
    const keys = Object.keys(drones.value || {})
    for (const key of keys) {
      //@ts-ignore
      const drone = drones.value[key] as DroneModel
      const position = drone.state.position
      const destroyed = drone.destroyed
      const mass = drone.specs.mass
      const pan = soundsPlayerBase.calculatePan(position.x)
      const gain = soundsPlayerBase.calculateGain(position)
      const fileName = mass < 0.003 ? 'drone-light.mp3' : 'drone-heavy.mp3'
      await playContinuousSound(DRONE_PREFIX, key, '', pan, gain, 0.5, !destroyed, fileName, fadeOutAndStop)
    }
    stopSoundsForNotExistingObjects(DRONE_PREFIX, keys)
  }

  async function playVehicleSounds(vehicles: VehicleModels) {
    const keys = Object.keys(vehicles)
    for (const key of keys) {
      const vehicleState = vehicles[key].state
      const acceleration = vehicles[key].specs.acceleration
      const pan = soundsPlayerBase.calculatePan(vehicleState.position.x)
      const gain = soundsPlayerBase.calculateGain(vehicleState.position)
      const movingOnGroundVelocity = getMovingOnGroundVelocity(vehicleState)
      await playContinuousSound(VEHICLE_PREFIX, key, TRACK_KEY, pan, gain, velocityToPlayingRate(movingOnGroundVelocity),
          !!movingOnGroundVelocity, getVehicleMoveSoundName(acceleration), fadeOutAndStop)
      await playContinuousSound(VEHICLE_PREFIX, key, ENGINE_KEY, pan, gain / 3, 1.0, isEngineActive(vehicleState),
          'vehicle-engine.mp3', fadeOutAndStop)
      await playContinuousSound(VEHICLE_PREFIX, key, JET_KEY, pan, gain, 1.0, VehicleUtils.isJetActive(vehicles[key]),
          'jet.mp3', fadeOutAndStop)
      if (key === userStore.user!.nickname) {
        await playContinuousSound(VEHICLE_PREFIX, key, GUN_KEY, 0, 0.4, 1.0, !!vehicleState.gunRotatingDirection,
            'gun-turn.mp3', stopLoop)
      }
    }
    stopSoundsForNotExistingObjects(VEHICLE_PREFIX, keys)
  }

  function stopSoundsForNotExistingObjects(prefix: string, keys: string[]) {
    // todo find better solution
    for (const controlKey in audioControls) {
      if (!controlKey.startsWith(prefix)) {
        break
      }
      let objectExists = false
      for (const key of keys) {
        if (controlKey.startsWith(prefix + key)) {
          objectExists = true
          break
        }
      }
      if (!objectExists) {
        stopByKey(controlKey)
      }
    }
  }

  function getVehicleMoveSoundName(acceleration: number) {
    if (acceleration > 12) {
      return 'vehicle-move-fast.mp3'
    } else {
      return Math.random() > 0.5 ? 'vehicle-move-medium.mp3' : 'vehicle-move-slow.mp3'
    }
  }

  async function playContinuousSound(prefix: string, key: string, addKey: string, pan: number, gain: number,
                                     rate: number, condition: boolean, file: string,
                                     stopFunction: (audioControl: AudioControl) => void) {

    const fullKey = prefix + key + addKey
    const audioControl = audioControls[fullKey]
    if (condition && !audioControl) {
      const newAudioControl = await playLooped(file, pan, gain, rate)
      if (newAudioControl) {
        audioControls[fullKey] = newAudioControl
        newAudioControl.source.addEventListener('ended', () => {
          delete audioControls[fullKey]
        })
      }
    }
    if (condition && audioControl) {
      audioControl.panner.pan.value = pan
      audioControl.gainNode.gain.value = gain
      audioControl.source.playbackRate.value = rate
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
    Object.keys(audioControls).forEach(stopByKey)
  }

  function stopByKey(key: string) {
    const audioControl = audioControls[key]
    if (audioControl) {
      fadeOutAndStop(audioControl)
      delete audioControls[key]
    }
  }

  function getMovingOnGroundVelocity(vehicleState: VehicleState) {
    const velocity = Math.sqrt(Math.pow(vehicleState.velocity.x, 2) + Math.pow(vehicleState.velocity.y, 2))
    return velocity > MIN_PLAYING_VELOCITY && vehicleState.onGround ? velocity : 0.0
  }

  function velocityToPlayingRate(velocity: number) {
    if (velocity <= MIN_PLAYING_VELOCITY) {
      return MIN_PLAYING_RATE
    } else if (velocity > MAX_PLAYING_VELOCITY) {
      return MAX_PLAYING_RATE
    } else {
      const a = (MAX_PLAYING_RATE - MIN_PLAYING_RATE) / (MAX_PLAYING_VELOCITY - MIN_PLAYING_VELOCITY)
      const b = MAX_PLAYING_RATE - a * MAX_PLAYING_VELOCITY
      return a * velocity + b
    }
  }

  function isEngineActive(vehicleState: VehicleState) {
    return vehicleState.movingDirection && !vehicleState.jetState?.active
  }

  async function playLooped(fileName: string, pan: number, gain: number, rate: number): Promise<AudioControl | undefined> {
    return await player.play({path: '/sounds/' + fileName, pan, gain, rate, loop: true})
  }

  return {start, stopAll}
}
