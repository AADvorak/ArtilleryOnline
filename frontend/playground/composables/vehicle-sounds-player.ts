import type {AudioControl, Player} from "~/playground/audio/player";
import {useBattleStore} from "~/stores/battle";
import type {VehicleState} from "~/playground/data/state";
import type {VehicleModels} from "~/playground/data/model";
import {useUserSettingsStore} from "~/stores/user-settings";
import {SoundSettingsNames} from "~/dictionary/sound-settings-names";
import {useSoundsPlayerBase} from "~/playground/composables/sounds-player-base";
import {useUserStore} from "~/stores/user";

interface VehicleAudioControls {
  [userKey: string]: AudioControl | undefined
}

const TRACK_KEY = 'Track'
const ENGINE_KEY = 'Engine'
const GUN_KEY = 'Gun'
const JET_KEY = 'Jet'

export function useVehicleSoundsPlayer(player: Player) {
  const vehicleAudioControls: VehicleAudioControls = {}

  const battleStore = useBattleStore()
  const userStore = useUserStore()
  const soundsPlayerBase = useSoundsPlayerBase()

  const vehicles = computed(() => battleStore.vehicles)

  function start() {
    setTimeout(playSounds, 100)
  }

  async function playSounds() {
    if (useUserSettingsStore().soundSettingsOrDefaultsNameValueMapping[SoundSettingsNames.ENABLE] !== '1') {
      return
    }
    if (vehicles.value) {
      await playVehicleSounds(vehicles.value!)
      setTimeout(playSounds, 100)
    } else {
      stopAll()
    }
  }

  async function playVehicleSounds(vehicles: VehicleModels) {
    const keys = Object.keys(vehicles)
    for (const key of keys) {
      const vehicleState = vehicles[key].state
      const acceleration = vehicles[key].specs.acceleration
      const pan = soundsPlayerBase.calculatePan(vehicleState.position.x)
      const gain = soundsPlayerBase.calculateGain(vehicleState.position)
      await playVehicleSound(key, TRACK_KEY, pan, gain, isMovingOnGround(vehicleState),
          getVehicleMoveSoundName(acceleration), fadeOutAndStop)
      await playVehicleSound(key, ENGINE_KEY, pan, gain / 3, isEngineActive(vehicleState),
          'vehicle-engine.mp3', fadeOutAndStop)
      await playVehicleSound(key, JET_KEY, pan, gain, isJetActive(vehicleState),
          'jet.wav', fadeOutAndStop)
      if (key === userStore.user!.nickname) {
        await playVehicleSound(key, GUN_KEY, 0, gain, !!vehicleState.gunRotatingDirection,
            'gun-turn.wav', stopLoop)
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

  async function playVehicleSound(key: string, addKey: string, pan: number, gain: number, condition: boolean,
                                  file: string, stopFunction: (audioControl: AudioControl) => void) {
    const fullKey = key + addKey
    const audioControl = vehicleAudioControls[fullKey]
    if (condition && !audioControl) {
      const newAudioControl = await playLooped(file, pan, gain)
      if (newAudioControl) {
        vehicleAudioControls[fullKey] = newAudioControl
        newAudioControl.source.addEventListener('ended', () => {
          vehicleAudioControls[fullKey] = undefined
        })
      }
    }
    if (condition && audioControl) {
      audioControl.panner.pan.value = pan
      audioControl.gainNode.gain.value = gain
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
      const audioControl = vehicleAudioControls[key]
      if (audioControl) {
        audioControl.source.stop()
        delete vehicleAudioControls[key]
      }
    })
  }

  function isMovingOnGround(vehicleState: VehicleState) {
    const sqrVelocity = Math.pow(vehicleState.velocity.x, 2) + Math.pow(vehicleState.velocity.y, 2)
    return sqrVelocity > 0.1 && vehicleState.onGround
  }

  function isEngineActive(vehicleState: VehicleState) {
    return vehicleState.movingDirection && !vehicleState.jetState?.active
  }

  function isJetActive(vehicleState: VehicleState) {
    return !!vehicleState.jetState?.active && vehicleState.jetState?.volume > 0
  }

  async function playLooped(fileName: string, pan: number, gain: number): Promise<AudioControl | undefined> {
    return await player.play({path: '/sounds/' + fileName, pan, gain, loop: true})
  }

  return {start, stopAll}
}
