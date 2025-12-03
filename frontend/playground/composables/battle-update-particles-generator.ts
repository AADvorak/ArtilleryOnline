import {type BattleModelEvents, RepairEventType} from "~/playground/data/events";
import type {BattleModel, VehicleModel} from "~/playground/data/model";
import {Contact, ShellHitType, ShellType} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";
import type {VehicleState} from "~/playground/data/state";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {DefaultColors} from "~/dictionary/default-colors";
import {useBattleStore} from "~/stores/battle";
import type {BattleUpdate} from "~/playground/data/battle";

export function useBattleUpdateParticlesGenerator() {

  const MIN_HIT_VELOCITY = 5
  const MAX_HIT_VELOCITY = 15
  const MSG_PARTICLE_LIFETIME = 1.0
  const HIT_PARTICLE_LIFETIME = 0.25
  const HIT_PARTICLES_ANGLE_DEVIATION = Math.PI / 16
  const CALIBER_PARTICLES_NUMBER_COEFFICIENT = 150

  const battleStore = useBattleStore()

  function generate(battleUpdate: BattleUpdate, battleModel: BattleModel) {
    battleUpdate.events && generateForEvents(battleUpdate.events, battleModel)
    const vehicles = battleUpdate.state?.vehicles
    if (vehicles) {
      Object.keys(vehicles).forEach(key => {
        const model = battleModel.vehicles[key]
        if (model) {
          const newState = vehicles[key]!
          showChangeHp(model, model.state, newState)
        }
      })
    }
  }

  function generateForEvents(events: BattleModelEvents, battleModel: BattleModel) {
    const hits = events.hits
    if (hits) {
      hits
          .filter(hit => !!hit.object && hit.object.type !== ShellHitType.GROUND)
          .forEach(hit => {
            const shell = battleModel.shells[hit.shellId]
            shell && shell.specs.type === ShellType.AP && addHitParticles(hit.contact, shell.specs.caliber,
                VectorUtils.getMagnitude(shell.state.velocity), true)
          })
    }
    const ricochets = events.ricochets
    if (ricochets) {
      ricochets
          .forEach(ricochet => {
            const shell = battleModel.shells[ricochet.shellId]
            shell && addHitParticles(ricochet.contact, shell.specs.caliber,
                VectorUtils.getMagnitude(shell.state.velocity), false)
          })
    }
    const repairs = events.repairs
    if (repairs) {
      repairs
          .filter(repair => repair.type === RepairEventType.REFILL_AMMO)
          .forEach(repair => {
            const vehicleModel = Object.values(battleModel.vehicles)
                .filter(vehicle => vehicle.id === repair.vehicleId)[0]
            vehicleModel && showChangeAmmo(vehicleModel)
          })
    }
  }

  function showChangeHp(model: VehicleModel, oldState: VehicleState, newState: VehicleState) {
    const hpDiff = newState.hitPoints - oldState.hitPoints
    if (Math.abs(hpDiff) > 1) {
      const position = BattleUtils.shiftedPosition(model.state.position, model.preCalc.maxRadius, Math.PI / 2)
      const color = hpDiff > 0 ? DefaultColors.BRIGHT_GREEN : DefaultColors.BRIGHT_RED
      const hpDiffToFixed = hpDiff.toFixed(0)
      const text = hpDiff > 0 ? '+' + hpDiffToFixed : hpDiffToFixed
      battleStore.addParticle(BattleUtils.generateParticle(position, MSG_PARTICLE_LIFETIME), {color, text})
    }
  }

  function showChangeAmmo(model: VehicleModel) {
    const position = BattleUtils.shiftedPosition(model.state.position, model.preCalc.maxRadius, Math.PI / 2)
    battleStore.addParticle(BattleUtils.generateParticle(position, MSG_PARTICLE_LIFETIME),
        {color: DefaultColors.BRIGHT_GREEN, text: '+ammo'})
  }

  function addHitParticles(contact: Contact, caliber: number, velocityMagnitude: number, isHit: boolean) {
    if (velocityMagnitude < MIN_HIT_VELOCITY) {
      return
    } else if (velocityMagnitude > MAX_HIT_VELOCITY) {
      velocityMagnitude = MAX_HIT_VELOCITY
    }
    const particlesNumber = caliber * CALIBER_PARTICLES_NUMBER_COEFFICIENT
    for (let i = 0; i < (isHit ? 2 : 1) * particlesNumber; i++) {
      let angle = contact.angle + HIT_PARTICLES_ANGLE_DEVIATION * (Math.random() - 0.5)
      const magnitude = Math.random() * velocityMagnitude / 2
      const lifeTime = HIT_PARTICLE_LIFETIME * velocityMagnitude / MAX_HIT_VELOCITY
      if (i >= particlesNumber) {
        angle += Math.PI
      }
      battleStore.addParticle(BattleUtils.generateParticle(contact.position, lifeTime, angle, magnitude), {})
    }
  }

  return { generate }
}
