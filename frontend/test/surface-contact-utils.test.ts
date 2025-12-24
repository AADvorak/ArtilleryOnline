import type {RoomModel} from "~/playground/data/model.ts"
import {test} from "@jest/globals"
import {expect} from "@jest/globals"
import roomModel from "./room-model.json"
import {Circle, HalfCircle, Trapeze} from "~/playground/data/geometry"
import {SurfaceContactUtils} from "~/playground/utils/surface-contact-utils"
import {Constants} from "~/playground/data/constants"
import type {SurfaceState} from "~/playground/data/state"
import type {TrapezeShape} from "~/playground/data/shapes"

const BODY_X = 10.0
const BODY_Y = 10.0
const RADIUS = 1.0
const SURFACE_WIDTH = 0.1
const EDGE_DISTANCE = SURFACE_WIDTH / 2 - Constants.INTERPENETRATION_THRESHOLD
const EDGE_CIRCLE_DISTANCE = RADIUS + EDGE_DISTANCE
const SMALL_DELTA = 0.00001

const BODY_POSITION = {x: BODY_X, y: BODY_Y, angle: 0}

const CIRCLE = new Circle({x: BODY_X, y: BODY_Y}, RADIUS)

const HALF_CIRCLE = new HalfCircle({x: BODY_X, y: BODY_Y}, RADIUS, 0)

const TRAPEZE_SHAPE: TrapezeShape = {
  name: 'Trapeze',
  bottomRadius: RADIUS,
  topRadius: RADIUS,
  height: RADIUS
}
const TRAPEZE = new Trapeze(BODY_POSITION, TRAPEZE_SHAPE)

test('circleBottomContactExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y - EDGE_CIRCLE_DISTANCE + SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, false)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(0.0, 4)
  expect(contact!.normal.y).toBeCloseTo(-1.0, 4)
})

test('circleBottomContactNotExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y - EDGE_CIRCLE_DISTANCE - SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, false)
  expect(contacts.size).toBe(0)
})

test('circleBottomContactExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y - EDGE_CIRCLE_DISTANCE + SMALL_DELTA + model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(0.0, 4)
  expect(contact!.normal.y).toBeCloseTo(-1.0, 4)
})

test('circleBottomContactNotExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y - EDGE_CIRCLE_DISTANCE - SMALL_DELTA + model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, true)
  expect(contacts.size).toBe(0)
})

test('circleTopContactExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE - SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, false)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(0.0, 4)
  expect(contact!.normal.y).toBeCloseTo(1.0, 4)
})

test('circleTopContactNotExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE + SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, false)
  expect(contacts.size).toBe(0)
})

test('circleTopContactExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE - SMALL_DELTA - model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(0.0, 4)
  expect(contact!.normal.y).toBeCloseTo(1.0, 4)
})

test('circleTopContactNotExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE + SMALL_DELTA - model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, true)
  expect(contacts.size).toBe(0)
})

test('circleRightContactExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE - SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, false)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(1.0, 4)
  expect(contact!.normal.y).toBeCloseTo(0.0, 4)
})

test('circleRightContactNotExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE + SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, false)
  expect(contacts.size).toBe(0)
})

test('circleRightContactExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE - SMALL_DELTA - model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(1.0, 4)
  expect(contact!.normal.y).toBeCloseTo(0.0, 4)
})

test('circleRightContactNotExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE + SMALL_DELTA - model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, true)
  expect(contacts.size).toBe(0)
})

test('circleLeftContactExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE + SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, false)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(-1.0, 4)
  expect(contact!.normal.y).toBeCloseTo(0.0, 4)
})

test('circleLeftContactNotExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE - SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, false)
  expect(contacts.size).toBe(0)
})

test('circleLeftContactExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE + SMALL_DELTA + model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(-1.0, 4)
  expect(contact!.normal.y).toBeCloseTo(0.0, 4)
})

test('circleLeftContactNotExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE - SMALL_DELTA + model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(CIRCLE, model, true)
  expect(contacts.size).toBe(0)
})

test('halfCircleBottomContactExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y - EDGE_DISTANCE + SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, false)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(0.0, 4)
  expect(contact!.normal.y).toBeCloseTo(-1.0, 4)
})

// todo
/*test('halfCircleBottomContactNotExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y - EDGE_DISTANCE - SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, false)
  expect(contacts.size).toBe(0)
})*/

test('halfCircleBottomContactExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y - EDGE_DISTANCE + SMALL_DELTA + model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(0.0, 4)
  expect(contact!.normal.y).toBeCloseTo(-1.0, 4)
})

// todo
/*test('halfCircleBottomContactNotExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y - EDGE_DISTANCE - SMALL_DELTA + model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, true)
  expect(contacts.size).toBe(0)
})*/

test('halfCircleTopContactExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE - SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, false)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(0.0, 4)
  expect(contact!.normal.y).toBeCloseTo(1.0, 4)
})

test('halfCircleTopContactNotExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE + SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, false)
  expect(contacts.size).toBe(0)
})

test('halfCircleTopContactExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE - SMALL_DELTA - model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(0.0, 4)
  expect(contact!.normal.y).toBeCloseTo(1.0, 4)
})

test('halfCircleTopContactNotExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE + SMALL_DELTA - model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: BODY_X - RADIUS, y: surfaceY},
    end: {x: BODY_X + RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, true)
  expect(contacts.size).toBe(0)
})

test('halfCircleRightContactExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE - SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, false)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(1.0, 4)
  expect(contact!.normal.y).toBeCloseTo(0.0, 4)
})

test('halfCircleRightContactNotExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE + SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, false)
  expect(contacts.size).toBe(0)
})

test('halfCircleRightContactExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE - SMALL_DELTA - model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(1.0, 4)
  expect(contact!.normal.y).toBeCloseTo(0.0, 4)
})

test('halfCircleRightContactNotExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE + SMALL_DELTA - model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, true)
  expect(contacts.size).toBe(0)
})

test('halfCircleLeftContactExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE + SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, false)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(-1.0, 4)
  expect(contact!.normal.y).toBeCloseTo(0.0, 4)
})

test('halfCircleLeftContactNotExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE - SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, false)
  expect(contacts.size).toBe(0)
})

test('halfCircleLeftContactExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE + SMALL_DELTA + model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(-1.0, 4)
  expect(contact!.normal.y).toBeCloseTo(0.0, 4)
})

test('halfCircleLeftContactNotExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE - SMALL_DELTA + model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - RADIUS},
    end: {x: surfaceX, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(HALF_CIRCLE, model, true)
  expect(contacts.size).toBe(0)
})

test('trapezeBottomContactExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y - EDGE_DISTANCE + SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: BODY_X - 2 * RADIUS, y: surfaceY},
    end: {x: BODY_X + 2 * RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, false)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(0.0, 4)
  expect(contact!.normal.y).toBeCloseTo(-1.0, 4)
})

test('trapezeBottomContactNotExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y - EDGE_DISTANCE - SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: BODY_X - 2 * RADIUS, y: surfaceY},
    end: {x: BODY_X + 2 * RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, false)
  expect(contacts.size).toBe(0)
})

test('trapezeBottomContactExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y - EDGE_DISTANCE + SMALL_DELTA + model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: BODY_X - 2 * RADIUS, y: surfaceY},
    end: {x: BODY_X + 2 * RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(0.0, 4)
  expect(contact!.normal.y).toBeCloseTo(-1.0, 4)
})

test('trapezeBottomContactNotExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y - EDGE_DISTANCE - SMALL_DELTA + model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: BODY_X - 2 * RADIUS, y: surfaceY},
    end: {x: BODY_X + 2 * RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, true)
  expect(contacts.size).toBe(0)
})

test('trapezeTopContactExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE - SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: BODY_X - 2 * RADIUS, y: surfaceY},
    end: {x: BODY_X + 2 * RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, false)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(0.0, 4)
  expect(contact!.normal.y).toBeCloseTo(1.0, 4)
})

test('trapezeTopContactNotExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE + SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: BODY_X - 2 * RADIUS, y: surfaceY},
    end: {x: BODY_X + 2 * RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, false)
  expect(contacts.size).toBe(0)
})

test('trapezeTopContactExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE - SMALL_DELTA - model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: BODY_X - 2 * RADIUS, y: surfaceY},
    end: {x: BODY_X + 2 * RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(0.0, 4)
  expect(contact!.normal.y).toBeCloseTo(1.0, 4)
})

test('trapezeTopContactNotExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceY = BODY_Y + EDGE_CIRCLE_DISTANCE + SMALL_DELTA - model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: BODY_X - 2 * RADIUS, y: surfaceY},
    end: {x: BODY_X + 2 * RADIUS, y: surfaceY},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, true)
  expect(contacts.size).toBe(0)
})

test('trapezeRightContactExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE - SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - 2 * RADIUS},
    end: {x: surfaceX, y: BODY_Y + 2 * RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, false)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(1.0, 4)
  expect(contact!.normal.y).toBeCloseTo(0.0, 4)
})

test('trapezeRightContactNotExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE + SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - 2 * RADIUS},
    end: {x: surfaceX, y: BODY_Y + 2 * RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, false)
  expect(contacts.size).toBe(0)
})

test('trapezeRightContactExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE - SMALL_DELTA - model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - 2 * RADIUS},
    end: {x: surfaceX, y: BODY_Y + 2 * RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(1.0, 4)
  expect(contact!.normal.y).toBeCloseTo(0.0, 4)
})

test('trapezeRightContactNotExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X + EDGE_CIRCLE_DISTANCE + SMALL_DELTA - model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - 2 * RADIUS},
    end: {x: surfaceX, y: BODY_Y + 2 * RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, true)
  expect(contacts.size).toBe(0)
})

test('trapezeLeftContactExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE + SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - 2 * RADIUS},
    end: {x: surfaceX, y: BODY_Y + 2 * RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, false)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(-1.0, 4)
  expect(contact!.normal.y).toBeCloseTo(0.0, 4)
})

test('trapezeLeftContactNotExists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE - SMALL_DELTA
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - 2 * RADIUS},
    end: {x: surfaceX, y: BODY_Y + 2 * RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, false)
  expect(contacts.size).toBe(0)
})

test('trapezeLeftContactExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE + SMALL_DELTA + model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - 2 * RADIUS},
    end: {x: surfaceX, y: BODY_Y + 2 * RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(-1.0, 4)
  expect(contact!.normal.y).toBeCloseTo(0.0, 4)
})

test('trapezeLeftContactNotExistsWithMaxDepth', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surfaceX = BODY_X - EDGE_CIRCLE_DISTANCE - SMALL_DELTA + model.specs.surfaceMaxDepth
  const surface: SurfaceState = {
    begin: {x: surfaceX, y: BODY_Y - 2 * RADIUS},
    end: {x: surfaceX, y: BODY_Y + 2 * RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, true)
  expect(contacts.size).toBe(0)
})

test('trapezeBottomRightContactWith45DegreesSurface', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surface: SurfaceState = {
    begin: {x: BODY_X, y: BODY_Y - RADIUS},
    end: {x: BODY_X + 2 * RADIUS, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(Math.SQRT1_2, 4)
  expect(contact!.normal.y).toBeCloseTo(-Math.SQRT1_2, 4)
})

test('trapezeBottomLeftContactWith135DegreesSurface', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surface: SurfaceState = {
    begin: {x: BODY_X, y: BODY_Y - RADIUS},
    end: {x: BODY_X - 2 * RADIUS, y: BODY_Y + RADIUS},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(-Math.SQRT1_2, 4)
  expect(contact!.normal.y).toBeCloseTo(-Math.SQRT1_2, 4)
})

test('trapezeTopRightContactWith135DegreesSurface', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surface: SurfaceState = {
    begin: {x: BODY_X, y: BODY_Y + 2 * RADIUS},
    end: {x: BODY_X + 2 * RADIUS, y: BODY_Y},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(Math.SQRT1_2, 4)
  expect(contact!.normal.y).toBeCloseTo(Math.SQRT1_2, 4)
})

test('trapezeTopLeftContactWith45DegreesSurface', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const surface: SurfaceState = {
    begin: {x: BODY_X, y: BODY_Y + 2 * RADIUS},
    end: {x: BODY_X - 2 * RADIUS, y: BODY_Y},
    width: SURFACE_WIDTH
  }
  model.state.surfaces = [surface]
  const contacts = SurfaceContactUtils.getContacts(TRAPEZE, model, true)
  expect(contacts.size).toBe(1)
  const contact = Array.from(contacts)[0]
  expect(contact!.normal.x).toBeCloseTo(-Math.SQRT1_2, 4)
  expect(contact!.normal.y).toBeCloseTo(Math.SQRT1_2, 4)
})
