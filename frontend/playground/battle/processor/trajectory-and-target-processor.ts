import {useBattleStore} from "~/stores/battle";
import {useUserStore} from "~/stores/user";
import {BattleCalculations} from "~/playground/data/calculations";
import {VehicleUtils} from "~/playground/utils/vehicle-utils";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {Contact, type Position, type TargetData} from "~/playground/data/common";
import {Segment} from "~/playground/data/geometry";
import {TrajectoryContactUtils} from "~/playground/utils/trajectory-contact-utils";
import {VectorUtils} from "~/playground/utils/vector-utils";

const battleStore = useBattleStore()
const userStore = useUserStore()

export const TrajectoryAndTargetProcessor = {
  process(battle: BattleCalculations) {
    const userVehicle = Object.keys(battleStore.vehicles || [])
        .filter(key => key === userStore.user!.nickname)
        .map(key => battleStore.vehicles![key])[0]
    if (!userVehicle) {
      this.clearTrajectoryAndTargetData()
      return
    }
    const selectedShell = userVehicle.state.gunState.selectedShell
    if (!selectedShell) {
      this.clearTrajectoryAndTargetData()
      return
    }
    const shellTrajectory: Position[] = []
    const shellSpecs = userVehicle.config.gun.availableShells[selectedShell]!
    const startPosition = VehicleUtils.getGunEndPosition(userVehicle)
    const angle = userVehicle.state.position.angle + userVehicle.state.gunState.angle
    const directionSign = Math.sign(Math.cos(angle))
    const maxX = BattleUtils.getRoomWidth(battle.model.room.specs)
    let x = startPosition.x
    let y = startPosition.y
    let previous: Position = {x, y}
    shellTrajectory.push({x, y})
    const step = 0.1
    let targetData: TargetData | undefined = undefined
    while (x > 0 && x < maxX && y > 0) {
      x += step * directionSign
      const x1 = x - startPosition.x
      y = startPosition.y + x1 * Math.tan(angle) - battle.model.room.specs.gravityAcceleration * x1 * x1
          / (2 * shellSpecs.velocity * shellSpecs.velocity * Math.cos(angle) * Math.cos(angle))
      const trajectory = new Segment(previous, {x, y})
      const hitNormal = VectorUtils.vectorFromTo(previous, {x, y})
      VectorUtils.normalize(hitNormal)
      const groundPosition = BattleUtils.getFirstPointUnderGround(trajectory, battle.model.room)
      if (groundPosition) {
        const contact = Contact.withAngleUncheckedDepth(0, 0, groundPosition)
        targetData = {
          contact,
          hitNormal
        }
      }
      if (!targetData) {
        for (const vehicle of battle.vehicles) {
          const cnt = TrajectoryContactUtils.detectWithVehicle(trajectory, vehicle)
          if (cnt) {
            targetData = {
              contact: cnt.contact,
              hitNormal,
              penetration: shellSpecs.penetration,
              armor: vehicle.model.specs.armor[cnt.hitSurface]
            }
            break
          }
          const rwc = TrajectoryContactUtils.detectWithWheel(trajectory, vehicle.rightWheel)
          if (rwc) {
            targetData = {
              contact: rwc,
              hitNormal
            }
            break
          }
          const lwc = TrajectoryContactUtils.detectWithWheel(trajectory, vehicle.leftWheel)
          if (lwc) {
            targetData = {
              contact: lwc,
              hitNormal
            }
            break
          }
        }
      }
      if (targetData) {
        shellTrajectory.push(targetData.contact.position)
        break
      }
      shellTrajectory.push({x, y})
      previous = {x, y}
    }
    battleStore.targetData = targetData
    battleStore.shellTrajectory = shellTrajectory
  },

  clearTrajectoryAndTargetData() {
    battleStore.shellTrajectory = []
    battleStore.targetData = undefined
  }
}
