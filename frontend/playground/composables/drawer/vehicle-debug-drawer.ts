import type {DrawerBase} from '@/playground/composables/drawer/drawer-base'
import type {Ref} from 'vue'
import {useBattleStore} from '~/stores/battle'
import {VehicleUtils} from '@/playground/utils/vehicle-utils'
import type {VehicleModel} from "~/playground/data/model";
import type {Contact} from "~/playground/data/common";
import {useSettingsStore} from "~/stores/settings";
import type {VehicleCalculations} from "~/playground/data/calculations";

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
      drawGroundContacts(calculations)
    }
  }

  function drawGroundContacts(calculations: VehicleCalculations) {
    const leftWheelContact = calculations.leftWheel.groundContact
    leftWheelContact && drawGroundContact(leftWheelContact)
    const rightWheelContact = calculations.rightWheel.groundContact
    rightWheelContact && drawGroundContact(rightWheelContact)
  }

  function drawGroundContact(contact: Contact) {
    const begin = drawerBase.transformPosition(contact.position)
    const end = drawerBase.transformPosition({
      x: contact.position.x + contact.normal.x / 4,
      y: contact.position.y + contact.normal.y / 4,
    })
    ctx.value!.beginPath()
    ctx.value!.lineWidth = 2
    ctx.value!.strokeStyle = 'white'
    ctx.value!.moveTo(begin.x, begin.y)
    ctx.value!.lineTo(end.x, end.y)
    ctx.value!.stroke()
    ctx.value!.closePath()
  }

  return {draw}
}
