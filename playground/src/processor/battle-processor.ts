import {useBattleStore} from "@/stores/battle";
import {ref} from "vue";
import {BattleStage} from "@/data/battle";
import type {Battle} from "@/data/battle";
import type {ShellModel, VehicleModel} from "@/data/model";
import {MovingDirection} from "@/data/common";

export function useBattleProcessor() {

  const battleStore = useBattleStore()

  const previousTime = ref<number>(0)

  function startProcessing() {
    previousTime.value = new Date().getTime()
    setTimeout(processStep, 10)
  }

  function processStep() {
    const battle = JSON.parse(JSON.stringify(battleStore.battle)) as Battle
    const battleStage = battle.battleStage
    if ([BattleStage.WAITING, BattleStage.ACTIVE].includes(battleStage)) {
      const currentTime = new Date().getTime()
      const timeStep = currentTime - previousTime.value
      const timeStepSecs = timeStep / 1000
      battle.time += timeStep
      if (BattleStage.ACTIVE === battleStage) {
        processStepActive(battle, timeStepSecs)
      }
      battleStore.battle = battle
      previousTime.value = currentTime
      setTimeout(processStep, 10)
    }
  }

  function processStepActive(battle: Battle, timeStepSecs: number) {
    Object.values(battle.model.vehicles).forEach(vehicle => {
      processVehicle(vehicle, timeStepSecs)
    })
    Object.values(battle.model.shells).forEach(shell => {
      processShell(shell, timeStepSecs, battle.model.room.specs.gravityAcceleration)
    })
  }

  function processVehicle(vehicleModel: VehicleModel, timeStepSecs: number) {
    if (vehicleModel.state.movingDirection) {
      const sign = MovingDirection.LEFT === vehicleModel.state.movingDirection ? -1 : 1
      vehicleModel.state.position.x += sign * vehicleModel.specs.movingVelocity * timeStepSecs
    }
    if (vehicleModel.state.gunRotatingDirection) {
      const sign = MovingDirection.RIGHT === vehicleModel.state.gunRotatingDirection ? -1 : 1
      vehicleModel.state.gunAngle += sign * vehicleModel.config.gun.rotationVelocity * timeStepSecs
    }
    if (vehicleModel.state.gunState.loadingShell) {
      vehicleModel.state.gunState.loadRemainTime -= timeStepSecs
    }
  }

  function processShell(shellModel: ShellModel, timeStepSecs: number, gravityAcceleration: number) {
    const prevPosition = shellModel.state.position
    let velocity = shellModel.state.velocity
    let angle = shellModel.state.angle
    let velocityX = velocity * Math.cos(angle)
    let velocityY = velocity * Math.sin(angle)
    const nextPosition = {
      x: prevPosition.x + velocityX * timeStepSecs,
      y: prevPosition.y + velocityY * timeStepSecs
    }
    velocityY -= gravityAcceleration * timeStepSecs
    velocity = Math.sqrt(Math.pow(velocityX, 2.0) + Math.pow(velocityY, 2.0))
    angle = Math.atan(velocityY / velocityX) + (velocityX < 0 ? Math.PI : 0.0)
    shellModel.state.position = nextPosition
    shellModel.state.velocity = velocity
    shellModel.state.angle = angle
  }

  return { startProcessing }
}
