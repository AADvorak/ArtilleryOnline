import {useBattleStore} from "~/stores/battle";
import type {Battle} from "@/playground/data/battle";
import type {BodyState, ShellState} from "~/playground/data/state";
import type {BodyPosition, Position, Velocity} from "~/playground/data/common";
import type {BodyModel, BoxModel, DroneModel, ShellModel} from "~/playground/data/model";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {Constants} from "~/playground/data/constants";

export function useBattleSmoothTransition() {

  const battleStore = useBattleStore()

  function process(serverBattle: Battle, timeStepSecs: number) {
    const clientBattle = battleStore.clientBattle
        ? JSON.parse(JSON.stringify(battleStore.clientBattle)) as Battle
        : undefined
    if (clientBattle) {
      clientBattle.battleStage = serverBattle.battleStage
      clientBattle.time = serverBattle.time
      clientBattle.fps = serverBattle.fps
      clientBattle.paused = serverBattle.paused
      clientBattle.model.room = serverBattle.model.room
      clientBattle.model.explosions = serverBattle.model.explosions
      doSmoothTransition(serverBattle, clientBattle, 1.5 * timeStepSecs)
      battleStore.updateClientBattle(clientBattle)
    }
  }

  function doSmoothTransition(serverBattle: Battle, clientBattle: Battle, timeStepSecs: number) {
    let isSmooth = false
    Object.keys(clientBattle.model.vehicles).forEach(key => {
      const clientModel = clientBattle.model.vehicles[key]!
      const serverModel = serverBattle.model.vehicles[key]
      if (serverModel) {
        if (doSmoothTransitionForBody(serverModel, clientModel, timeStepSecs)) {
          isSmooth = true
        }
      } else {
        delete clientBattle.model.vehicles[key]
      }
    })
    Object.keys(clientBattle.model.boxes).forEach(key => {
      // @ts-ignore
      const clientModel = clientBattle.model.boxes[key] as BoxModel
      // @ts-ignore
      const serverModel = serverBattle.model.boxes[key] as BoxModel | undefined
      if (serverModel) {
        if (doSmoothTransitionForBody(serverModel, clientModel, timeStepSecs)) {
          isSmooth = true
        }
      } else {
        // @ts-ignore
        delete clientBattle.model.boxes[key]
      }
    })
    Object.keys(clientBattle.model.drones).forEach(key => {
      // @ts-ignore
      const clientModel = clientBattle.model.drones[key] as DroneModel
      // @ts-ignore
      const serverModel = serverBattle.model.drones[key] as DroneModel | undefined
      if (serverModel) {
        if (doSmoothTransitionForBody(serverModel, clientModel, timeStepSecs)) {
          isSmooth = true
        }
      } else {
        // @ts-ignore
        delete clientBattle.model.drones[key]
      }
    })
    Object.keys(clientBattle.model.missiles).forEach(key => {
      // @ts-ignore
      const clientModel = clientBattle.model.missiles[key] as BodyModel
      // @ts-ignore
      const serverModel = serverBattle.model.missiles[key] as BodyModel | undefined
      if (serverModel) {
        if (doSmoothTransitionForBody(serverModel, clientModel, timeStepSecs)) {
          isSmooth = true
        }
      } else {
        // @ts-ignore
        delete clientBattle.model.missiles[key]
      }
    })
    Object.keys(clientBattle.model.shells).forEach(key => {
      // @ts-ignore
      const clientModel = clientBattle.model.shells[key] as ShellModel
      // @ts-ignore
      const serverModel = serverBattle.model.shells[key] as ShellModel | undefined
      if (serverModel) {
        if (doSmoothTransitionForShell(serverModel, clientModel, timeStepSecs)) {
          isSmooth = true
        }
      } else {
        // @ts-ignore
        delete clientBattle.model.shells[key]
      }
    })

    if (!isSmooth) {
      battleStore.needSmoothTransition = false
    }

    Object.keys(serverBattle.model.vehicles).forEach(key => {
      if (!clientBattle.model.vehicles[key]) {
        clientBattle.model.vehicles[key] = serverBattle.model.vehicles[key]!
      }
    })
    Object.keys(serverBattle.model.boxes).forEach(key => {
      // @ts-ignore
      if (!clientBattle.model.boxes[key]) {
        // @ts-ignore
        clientBattle.model.boxes[key] = serverBattle.model.boxes[key]!
      }
    })
    Object.keys(serverBattle.model.drones).forEach(key => {
      // @ts-ignore
      if (!clientBattle.model.drones[key]) {
        // @ts-ignore
        clientBattle.model.drones[key] = serverBattle.model.drones[key]!
      }
    })
    Object.keys(serverBattle.model.missiles).forEach(key => {
      // @ts-ignore
      if (!clientBattle.model.missiles[key]) {
        // @ts-ignore
        clientBattle.model.missiles[key] = serverBattle.model.missiles[key]!
      }
    })
    Object.keys(serverBattle.model.shells).forEach(key => {
      // @ts-ignore
      if (!clientBattle.model.shells[key]) {
        // @ts-ignore
        clientBattle.model.shells[key] = serverBattle.model.shells[key]!
      }
    })
  }

  function doSmoothTransitionForBody(serverModel: BodyModel, clientModel: BodyModel, timeStepSecs: number) {
    let isSmooth = false
    const serverState = JSON.parse(JSON.stringify(serverModel.state)) as BodyState
    let clientState = JSON.parse(JSON.stringify(clientModel.state)) as BodyState
    const serverPosition = JSON.parse(JSON.stringify(serverState.position)) as BodyPosition
    const clientPosition = JSON.parse(JSON.stringify(clientState.position)) as BodyPosition
    if (doSmoothTransitionForPosition(serverPosition, clientPosition, serverState.velocity, timeStepSecs)) {
      isSmooth = true
    }
    const angleDiff = BattleUtils.calculateAngleDiff(clientPosition.angle, serverPosition.angle)
    const angleVelocity = serverState.velocity.angle
    const maxAngleMove = Math.max(
        Constants.MAX_TRANSITION_VELOCITY * timeStepSecs,
        Math.abs(angleVelocity) * timeStepSecs
    )
    if (Math.abs(angleDiff) < maxAngleMove) {
      clientPosition.angle = serverPosition.angle
    } else {
      const direction = Math.sign(angleVelocity !== 0 ? angleVelocity : angleDiff)
      clientPosition.angle += maxAngleMove * direction
      isSmooth = true
    }
    clientState = serverState
    clientState.position = clientPosition
    clientModel.state = clientState
    return isSmooth
  }

  function doSmoothTransitionForShell(serverModel: ShellModel, clientModel: ShellModel, timeStepSecs: number) {
    const serverState = JSON.parse(JSON.stringify(serverModel.state)) as ShellState
    let clientState = JSON.parse(JSON.stringify(clientModel.state)) as ShellState
    const serverPosition = JSON.parse(JSON.stringify(serverState.position)) as Position
    const clientPosition = JSON.parse(JSON.stringify(clientState.position)) as Position
    const isSmooth = doSmoothTransitionForPosition(serverPosition, clientPosition, serverState.velocity, timeStepSecs)
    clientState = serverState
    clientState.position = clientPosition
    clientModel.state = clientState
    return isSmooth
  }

  function doSmoothTransitionForPosition(
      serverPosition: Position,
      clientPosition: Position,
      serverVelocity: Velocity,
      timeStepSecs: number
  ) {
    let isSmooth = false
    const xDiff = serverPosition.x - clientPosition.x
    const maxXMove = Math.max(
        xDiff / 10,
        Constants.MAX_TRANSITION_VELOCITY * timeStepSecs,
        Math.abs(serverVelocity.x) * timeStepSecs
    )
    if (Math.abs(xDiff) < maxXMove) {
      clientPosition.x = serverPosition.x
    } else {
      clientPosition.x += maxXMove * Math.sign(xDiff)
      isSmooth = true
    }
    const yDiff = serverPosition.y - clientPosition.y
    const maxYMove = Math.max(
        yDiff / 10,
        Constants.MAX_TRANSITION_VELOCITY * timeStepSecs,
        Math.abs(serverVelocity.y) * timeStepSecs
    )
    if (Math.abs(yDiff) < maxYMove) {
      clientPosition.y = serverPosition.y
    } else {
      clientPosition.y += maxYMove * Math.sign(yDiff)
      isSmooth = true
    }
    return isSmooth
  }

  return { process }
}
