import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {VehicleUtils} from '@/playground/utils/vehicle-utils'
import type {VehicleModel} from "~/playground/data/model";
import {type Contact, type Position} from "~/playground/data/common";
import {useSettingsStore} from "~/stores/settings";
import type {VehicleCalculations} from "~/playground/data/calculations";
import {JetForceCalculator} from "~/playground/battle/calculator/vehicle/jet-force-calculator";
import type {BodyForce} from "~/playground/battle/calculator/body-force";
import {Segment} from "~/playground/data/geometry";
import {VectorUtils} from "~/playground/utils/vector-utils";
import {EngineForceCalculator} from "~/playground/battle/calculator/vehicle/engine-force-calculator";

export function useVehicleDebugDrawer(
    drawerBase: DrawerBase,
    ctx: Ref<CanvasRenderingContext2D | undefined>
) {
  const battleStore = useBattleStore()
  const settingsStore = useSettingsStore()

  function draw() {
    if (battleStore.vehicles && settingsStore.settings?.debug) {
      Object.values(battleStore.vehicles).forEach(drawVehicle)
    }
  }

  function drawVehicle(vehicleModel: VehicleModel) {
    if (ctx.value ) {
      const roomModel = battleStore.battle!.model.room
      const calculations = VehicleUtils.initVehicleCalculations(vehicleModel)
      const wheelRadius = vehicleModel.specs.wheelRadius
      VehicleUtils.calculateGroundContact(calculations.rightWheel, wheelRadius, roomModel)
      VehicleUtils.calculateGroundContact(calculations.leftWheel, wheelRadius, roomModel)
      drawCOM(vehicleModel)
      drawGroundContacts(calculations)
      drawJetForces(calculations)
      drawEngineForces(calculations)
    }
  }

  function drawCOM(vehicleModel: VehicleModel) {
    const position = drawerBase.transformPosition(vehicleModel.state.position)
    ctx.value!.fillStyle = 'white'
    ctx.value!.beginPath()
    ctx.value!.arc(position.x, position.y, 2, 0, 2 * Math.PI)
    ctx.value!.fill()
    ctx.value!.closePath()
  }

  function drawGroundContacts(calculations: VehicleCalculations) {
    const leftWheelContact = calculations.leftWheel.groundContact
    leftWheelContact && drawGroundContact(leftWheelContact)
    const rightWheelContact = calculations.rightWheel.groundContact
    rightWheelContact && drawGroundContact(rightWheelContact)
  }

  function drawGroundContact(contact: Contact) {
    drawerBase.drawSegment(ctx.value!, new Segment(contact.position, {
      x: contact.position.x + contact.normal.x / 4,
      y: contact.position.y + contact.normal.y / 4,
    }), 2, 'white')
  }

  function drawEngineForces(calculations: VehicleCalculations) {
    drawBodyForces(
        calculations.model.state.position,
        new EngineForceCalculator().calculate(calculations, battleStore.battle!.model),
        'blue', 0.5
    )
  }

  function drawJetForces(calculations: VehicleCalculations) {
    drawBodyForces(
        calculations.model.state.position,
        new JetForceCalculator().calculate(calculations, battleStore.battle!.model),
        'red', 1.0
    )
  }

  function drawBodyForces(comPosition: Position, bodyForces: BodyForce[], color: string, coefficient: number) {
    const maxMagnitude = bodyForces
        .map(force => force.sumForceMagnitude())
        .reduce((a, b) => Math.max(a, b), 0)
    bodyForces.forEach(force => drawBodyForce(comPosition, force, color,
        coefficient * force.sumForceMagnitude() / maxMagnitude))
  }

  function drawBodyForce(comPosition: Position, bodyForce: BodyForce, color: string, coefficient?: number) {
    const forcePosition: Position = {
      x: comPosition.x + (bodyForce.radiusVector?.x || 0),
      y: comPosition.y + (bodyForce.radiusVector?.y || 0)
    }
    let forceNormalized = bodyForce.sumForce()
    VectorUtils.normalize(forceNormalized)
    if (coefficient) {
      forceNormalized = VectorUtils.multiply(forceNormalized, coefficient)
    }
    drawerBase.drawSegment(ctx.value!, new Segment(forcePosition, {
      x: forcePosition.x + forceNormalized.x,
      y: forcePosition.y + forceNormalized.y,
    }), 2, color)
  }

  return {draw}
}
