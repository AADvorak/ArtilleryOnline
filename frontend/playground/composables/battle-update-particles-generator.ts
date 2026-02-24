import {type BattleModelEvents, RepairEventType} from "~/playground/data/events";
import type {BattleModel, VehicleModel} from "~/playground/data/model";
import {
  type BodyAcceleration,
  type BodyVelocity,
  Contact,
  type Position,
  ShellHitType,
  ShellType
} from "~/playground/data/common";
import {VectorUtils} from "~/playground/utils/vector-utils";
import type {VehicleState} from "~/playground/data/state";
import {BattleUtils} from "~/playground/utils/battle-utils";
import {DefaultColors} from "~/dictionary/default-colors";
import {useBattleStore} from "~/stores/battle";
import type {BattleUpdate} from "~/playground/data/battle";
import {
  type CircleShape,
  type HalfCircleShape,
  type RegularPolygonShape,
  type Shape,
  ShapeNames, type TrapezeShape
} from "~/playground/data/shapes";
import {mdiSkullCrossbones} from "@mdi/js";
import {useUserSettingsStore} from "~/stores/user-settings";
import {AppearancesNames} from "~/dictionary/appearances-names";
import {VehicleUtils} from "~/playground/utils/vehicle-utils";
import {BodyUtils} from "~/playground/utils/body-utils";
import {HalfCircle, Trapeze} from "~/playground/data/geometry";

export function useBattleUpdateParticlesGenerator() {

  const MIN_HIT_VELOCITY = 5
  const MAX_HIT_VELOCITY = 15
  const MSG_PARTICLE_LIFETIME = 1.0
  const HIT_PARTICLE_LIFETIME = 0.25
  const HIT_GROUND_PARTICLE_LIFETIME = 0.35
  const HIT_PARTICLE_LIFETIME_RELATIVE_DEVIATION = 0.2
  const HIT_PARTICLES_ANGLE_DEVIATION = Math.PI / 16
  const HIT_GROUND_PARTICLES_ANGLE_DEVIATION = Math.PI / 3
  const HIT_GROUND_PARTICLES_ANGLE_VELOCITY_DEVIATION = Math.PI / 2
  const CALIBER_PARTICLES_NUMBER_COEFFICIENT = 150
  const GROUND_PARTICLES_NUMBER = 40
  const MIN_BODY_PARTICLE_SIZE = 0.04
  const DESTROY_PARTICLES_ACCELERATION = 20
  const DESTROY_PARTICLES_ACCELERATION_DEVIATION = 5
  const DESTROY_PARTICLES_ANGLE_ACCELERATION_DEVIATION = Math.PI / 2

  const battleStore = useBattleStore()

  const userSettingsStore = useUserSettingsStore()

  const appearances = computed(() => userSettingsStore.appearancesOrDefaultsNameValueMapping)

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
    const destroyedVehicleKeys = battleUpdate.updates?.removed?.vehicleKeys
    if (destroyedVehicleKeys) {
      destroyedVehicleKeys.forEach(key => {
        const model = battleModel.vehicles[key]
        if (model) {
          showDestroy(model)
          addVehicleExplosionParticles(model)
        }
      })
    }
  }

  function generateForEvents(events: BattleModelEvents, battleModel: BattleModel) {
    const hits = events.hits
    if (hits) {
      hits
          .filter(hit => !!hit.object)
          .forEach(hit => {
            const shell = battleModel.shells[hit.shellId]
            if (shell && shell.specs.type === ShellType.AP) {
              hit.object.type === ShellHitType.GROUND
                  ? addHitGroundParticles(hit.contact, shell.specs.caliber, hit.closingVelocity)
                  : addHitParticles(hit.contact, shell.specs.caliber, hit.closingVelocity, true)
            }
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
    if (Math.abs(hpDiff) >= 1) {
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

  function showDestroy(model: VehicleModel) {
    const nicknameAbove = appearances.value[AppearancesNames.NICKNAMES_ABOVE] === '1'
    const position = BattleUtils.shiftedPosition(
        model.state.position,
        1.5 * model.preCalc.maxRadius + (nicknameAbove ? 0.3 : 0.15),
        Math.PI / 2
    )
    battleStore.addParticle(BattleUtils.generateParticle(position, MSG_PARTICLE_LIFETIME),
        {color: DefaultColors.BRIGHT_RED, icon: mdiSkullCrossbones})
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
          * (1 + HIT_PARTICLE_LIFETIME_RELATIVE_DEVIATION * (Math.random() - 0.5))
      if (i >= particlesNumber) {
        angle += Math.PI
      }
      battleStore.addParticle(BattleUtils.generateParticle(contact.position, lifeTime, angle, magnitude), {})
    }
  }

  function addHitGroundParticles(contact: Contact, caliber: number, velocityMagnitude: number) {
    for (let i = 0; i < GROUND_PARTICLES_NUMBER; i++) {
      let angle = contact.angle + HIT_GROUND_PARTICLES_ANGLE_DEVIATION * (Math.random() - 0.5) + Math.PI / 2
      const magnitude = Math.random() * velocityMagnitude / 5
      const lifeTime = HIT_GROUND_PARTICLE_LIFETIME * velocityMagnitude / MAX_HIT_VELOCITY
          * (1 + HIT_PARTICLE_LIFETIME_RELATIVE_DEVIATION * (Math.random() - 0.5))
      const size = caliber * (Math.random() + 0.5)
      if (size < MIN_BODY_PARTICLE_SIZE) {
        battleStore.addParticle(
            BattleUtils.generateParticle(contact.position, lifeTime, angle, magnitude),
            {size, groundTexture: true}
        )
      } else {
        const shape: RegularPolygonShape = {
          name: ShapeNames.REGULAR_POLYGON,
          radius: size / 2,
          sidesNumber: 5
        }
        const angleVelocity = HIT_GROUND_PARTICLES_ANGLE_VELOCITY_DEVIATION * (Math.random() - 0.5)
        battleStore.addBodyParticle(
            BattleUtils.generateBodyParticle({...contact.position, angle: 0}, lifeTime, angle, magnitude, angleVelocity),
            {shape, groundTexture: true}
        )
      }
    }
  }

  function addVehicleExplosionParticles(vehicleModel: VehicleModel) {
    const wheelShape: CircleShape = {
      name: ShapeNames.CIRCLE,
      radius: vehicleModel.specs.wheelRadius,
    }
    addVehicleExplosionParticle(VehicleUtils.getRightWheelPosition(vehicleModel), wheelShape, vehicleModel)
    addVehicleExplosionParticle(VehicleUtils.getLeftWheelPosition(vehicleModel), wheelShape, vehicleModel)
    const smallWheelShape: CircleShape = {
      name: ShapeNames.CIRCLE,
      radius: vehicleModel.specs.wheelRadius / 2,
    }
    VehicleUtils.getSmallWheels(vehicleModel).forEach(position =>
        addVehicleExplosionParticle(position, smallWheelShape, vehicleModel))
    const gridStep = 0.05
    const gridParticleShape: RegularPolygonShape = {
      name: ShapeNames.REGULAR_POLYGON,
      radius: gridStep * (1 + Math.random() / 2),
      sidesNumber: 3
    }
    getTurretGrid(vehicleModel, gridStep).forEach(position => {
      for (let i = 0; i < 2; i++) {
        gridParticleShape.sidesNumber = getRandomInt(3, 6)
        addVehicleExplosionParticle(position, gridParticleShape, vehicleModel)
      }
    })
  }

  function addVehicleExplosionParticle(position: Position, shape: Shape, vehicleModel: VehicleModel) {
    const movingVelocity = BodyUtils.getVelocityAt(vehicleModel.state, position)
    const vectorFromCOM = VectorUtils.vectorFromTo(vehicleModel.state.position, position)
    const distanceCoefficient = BattleUtils.distance(vehicleModel.state.position, position)
        / vehicleModel.preCalc.maxRadius
    const remainTime = 0.3 + 0.2 * (Math.random() - 0.5)
    const explosionVelocityMagnitude = distanceCoefficient * (DESTROY_PARTICLES_ACCELERATION
        + DESTROY_PARTICLES_ACCELERATION_DEVIATION * 2 * (Math.random() - 0.5)) / remainTime
    VectorUtils.normalize(vectorFromCOM)
    const velocity: BodyVelocity = {
      x: movingVelocity.x,
      y: movingVelocity.y,
      angle: 0
    }
    const acceleration: BodyAcceleration = {
      x: vectorFromCOM.x * explosionVelocityMagnitude,
      y: vectorFromCOM.y * explosionVelocityMagnitude + 10.0,
      angle: distanceCoefficient * DESTROY_PARTICLES_ANGLE_ACCELERATION_DEVIATION * 2 * (Math.random() - 0.5) / remainTime
    }
    battleStore.addBodyParticle(
        {position: {...position, angle: 0}, velocity, acceleration, remainTime},
        {shape, color: vehicleModel.config.color}
    )
  }

  function getTurretGrid(vehicleModel: VehicleModel, step: number): Position[] {
    const turretShape = vehicleModel.specs.turretShape
    switch (turretShape.name) {
      case ShapeNames.HALF_CIRCLE:
        return getHalfCircleGrid(vehicleModel, turretShape as HalfCircleShape, step)
      case ShapeNames.TRAPEZE:
        return getTrapezeGrid(vehicleModel, turretShape as TrapezeShape, step)
    }
    return []
  }

  function getHalfCircleGrid(vehicleModel: VehicleModel, turretShape: HalfCircleShape, step: number): Position[] {
    return new HalfCircle(BodyUtils.getGeometryPosition(vehicleModel), turretShape.radius,
        vehicleModel.state.position.angle).grid(step)
  }

  function getTrapezeGrid(vehicleModel: VehicleModel, turretShape: TrapezeShape, step: number): Position[] {
    return new Trapeze(BodyUtils.getGeometryBodyPosition(vehicleModel), turretShape).grid(step)
  }

  function getRandomInt(min: number, max: number): number {
    min = Math.ceil(min)
    max = Math.floor(max)
    return Math.floor(Math.random() * (max - min + 1)) + min
  }

  return { generate }
}
