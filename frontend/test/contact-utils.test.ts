import {ContactUtils} from "~/playground/utils/contact-utils"
import {Trapeze, Circle, HalfCircle} from "~/playground/data/geometry"
import {test, expect} from "@jest/globals"

const SQRT_05 = Math.sqrt(0.5)

const RECT_TRAPEZE_SHAPE = {
  name: "Trapeze",
  bottomRadius: 1.0,
  topRadius: 1.0,
  height: 1.0
}

const SMALLER_TOP_TRAPEZE_SHAPE = {
  name: "Trapeze",
  bottomRadius: 1.0,
  topRadius: 0.4,
  height: 1.0
}

test('trapezesBottomWithBottomLeft', () => {
  const trapeze = new Trapeze(
    {
      x: -0.5,
      y: -0.5,
      angle: -3 * Math.PI / 4
    },
    RECT_TRAPEZE_SHAPE
  )
  const otherTrapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(0.0, 5)
    expect(contact.normal.y).toBeCloseTo(1.0, 5)
  }
})

test('trapezesRightWithTopLeft', () => {
  const trapeze = new Trapeze(
    {
      x: 2.2,
      y: 0,
      angle: Math.PI / 8
    },
    RECT_TRAPEZE_SHAPE
  )
  const otherTrapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(-1.0, 5)
    expect(contact.normal.y).toBeCloseTo(0.0, 5)
  }
})

test('trapezesTopWithBottomRight', () => {
  const trapeze = new Trapeze(
    {
      x: 0,
      y: 1.2,
      angle: -Math.PI / 8
    },
    RECT_TRAPEZE_SHAPE
  )
  const otherTrapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(0.0, 5)
    expect(contact.normal.y).toBeCloseTo(-1.0, 5)
  }
})

test('trapezesLeftWithTopRight', () => {
  const trapeze = new Trapeze(
    {
      x: -2.2,
      y: 0,
      angle: -Math.PI / 8
    },
    RECT_TRAPEZE_SHAPE
  )
  const otherTrapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(1.0, 5)
    expect(contact.normal.y).toBeCloseTo(0.0, 5)
  }
})

test('trapezesBottomWithTop', () => {
  const trapeze = new Trapeze(
    {
      x: 0,
      y: -0.9,
      angle: 0
    },
    SMALLER_TOP_TRAPEZE_SHAPE
  )
  const otherTrapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(0.0, 5)
    expect(contact.normal.y).toBeCloseTo(1.0, 5)
    expect(contact.depth).toBeCloseTo(0.1, 5)
  }
})

test('trapezesRightWithTop', () => {
  const trapeze = new Trapeze(
    {
      x: 1.9,
      y: 0.5,
      angle: Math.PI / 2
    },
    SMALLER_TOP_TRAPEZE_SHAPE
  )
  const otherTrapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(-1.0, 5)
    expect(contact.normal.y).toBeCloseTo(0.0, 5)
    expect(contact.depth).toBeCloseTo(0.1, 5)
  }
})

test('trapezesTopRightWithBottomLeft', () => {
  const trapeze = new Trapeze(
    {
      x: -1.9,
      y: -0.9,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const otherTrapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(SQRT_05, 5)
    expect(contact.normal.y).toBeCloseTo(SQRT_05, 5)
  }
})

test('trapezesTopLeftWithBottomRight', () => {
  const trapeze = new Trapeze(
    {
      x: 1.9,
      y: -0.9,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const otherTrapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(-SQRT_05, 5)
    expect(contact.normal.y).toBeCloseTo(SQRT_05, 5)
  }
})

test('trapezesBottomLeftWithTopRight', () => {
  const trapeze = new Trapeze(
    {
      x: 1.9,
      y: 0.9,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const otherTrapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(-SQRT_05, 5)
    expect(contact.normal.y).toBeCloseTo(-SQRT_05, 5)
  }
})

test('trapezesBottomWithTop1', () => {
  const trapeze = new Trapeze(
    {
      x: 1.7,
      y: 0.9,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const otherTrapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(0.0, 5)
    expect(contact.normal.y).toBeCloseTo(-1.0, 5)
    expect(contact.depth).toBeCloseTo(0.1, 5)
  }
})

test('trapezesLeftWithRight', () => {
  const trapeze = new Trapeze(
    {
      x: 1.9,
      y: 0.7,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const otherTrapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(-1.0, 5)
    expect(contact.normal.y).toBeCloseTo(0.0, 5)
    expect(contact.depth).toBeCloseTo(0.1, 5)
  }
})

test('trapezesBottomRightWithTopLeft', () => {
  const trapeze = new Trapeze(
    {
      x: -1.9,
      y: 0.9,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const otherTrapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const contact = ContactUtils.getTrapezesContact(trapeze, otherTrapeze)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(SQRT_05, 5)
    expect(contact.normal.y).toBeCloseTo(-SQRT_05, 5)
  }
})

test('trapezeBottomRightWithCircle', () => {
  const trapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const circle = new Circle({x: 1.5, y: -0.5}, 1.0)
  const contact = ContactUtils.getTrapezeCircleContact(trapeze, circle)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(SQRT_05, 5)
    expect(contact.normal.y).toBeCloseTo(-SQRT_05, 5)
  }
})

test('trapezeBottomWithCircle', () => {
  const trapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const circle = new Circle({x: 0, y: -0.5}, 1.0)
  const contact = ContactUtils.getTrapezeCircleContact(trapeze, circle)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(0.0, 5)
    expect(contact.normal.y).toBeCloseTo(-1.0, 5)
  }
})

test('trapezeBottomWithCircleCenterInTrapeze', () => {
  const trapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const circle = new Circle({x: 0, y: 0.1}, 0.2)
  const contact = ContactUtils.getTrapezeCircleContact(trapeze, circle)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(0.0, 5)
    expect(contact.normal.y).toBeCloseTo(-1.0, 5)
    expect(contact.depth).toBeCloseTo(0.3, 5)
  }
})

test('trapezeBottomWithCircleFullInTrapeze', () => {
  const trapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const circle = new Circle({x: 0, y: 0.11}, 0.1)
  const contact = ContactUtils.getTrapezeCircleContact(trapeze, circle)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(0.0, 5)
    expect(contact.normal.y).toBeCloseTo(-1.0, 5)
    expect(contact.depth).toBeCloseTo(0.21, 5)
  }
})

test('trapezeWithCircleNoContact', () => {
  const trapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const circle = new Circle({x: 0, y: -0.11}, 0.1)
  const contact = ContactUtils.getTrapezeCircleContact(trapeze, circle)
  expect(contact).toBeNull()
})

test('trapezeWithHalfCircleNoContact', () => {
  const trapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const halfCircle = HalfCircle.of({x: 0, y: RECT_TRAPEZE_SHAPE.height, angle: 0}, 1.0)
  const contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle)
  expect(contact).toBeNull()
})

test('trapezeTopWithHalfCircleBottom', () => {
  const depth = 0.1
  const trapeze = new Trapeze(
    {
      x: 0,
      y: depth,
      angle: 0
    },
    SMALLER_TOP_TRAPEZE_SHAPE
  )
  const halfCircle = HalfCircle.of({x: 0, y: RECT_TRAPEZE_SHAPE.height, angle: 0}, 1.0)
  const contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(0.0, 5)
    expect(contact.normal.y).toBeCloseTo(1.0, 5)
    expect(contact.depth).toBeCloseTo(depth, 5)
  }
})

test('trapezeTopWithHalfCircleTop', () => {
  const depth = 0.1
  const trapeze = new Trapeze(
    {
      x: 0,
      y: depth,
      angle: 0
    },
    SMALLER_TOP_TRAPEZE_SHAPE
  )
  const halfCircle = HalfCircle.of({x: 0, y: RECT_TRAPEZE_SHAPE.height + 1.0, angle: Math.PI}, 1.0)
  const contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(0.0, 5)
    expect(contact.normal.y).toBeCloseTo(1.0, 5)
    expect(contact.depth).toBeCloseTo(depth, 5)
  }
})

test('trapezeBottomWithHalfCircleTop', () => {
  const depth = 0.1
  const trapeze = new Trapeze(
    {
      x: 0,
      y: -depth,
      angle: 0
    },
    SMALLER_TOP_TRAPEZE_SHAPE
  )
  const halfCircle = HalfCircle.of({x: 0, y: -1.0, angle: 0}, 1.0)
  const contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(0.0, 5)
    expect(contact.normal.y).toBeCloseTo(-1.0, 5)
    expect(contact.depth).toBeCloseTo(depth, 5)
  }
})

test('trapezeBottomRightWithHalfCircleTop', () => {
  const trapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const halfCircle = HalfCircle.of({x: 1.5, y: -0.5, angle: Math.PI / 4}, 1.0)
  const contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(SQRT_05, 5)
    expect(contact.normal.y).toBeCloseTo(-SQRT_05, 5)
  }
})

test('trapezeTopRightWithHalfCircleBottom', () => {
  const trapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const halfCircle = HalfCircle.of({x: 0.9, y: 0.9, angle: -Math.PI / 4}, 1.0)
  const contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(SQRT_05, 5)
    expect(contact.normal.y).toBeCloseTo(SQRT_05, 5)
  }
})

test('trapezeRightWithHalfCircleLeftBottom', () => {
  const trapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const halfCircle = HalfCircle.of({x: 1.9, y: 0.5, angle: 0}, 1.0)
  const contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(1.0, 5)
    expect(contact.normal.y).toBeCloseTo(0.0, 5)
  }
})

test('trapezeTopRightWithHalfCircleLeftBottom', () => {
  const trapeze = new Trapeze(
    {
      x: 0,
      y: 0,
      angle: 0
    },
    RECT_TRAPEZE_SHAPE
  )
  const halfCircle = HalfCircle.of({x: 1.9, y: 0.9, angle: 0}, 1.0)
  const contact = ContactUtils.getTrapezeHalfCircleContact(trapeze, halfCircle)
  expect(contact).not.toBeNull()
  if (contact) {
    expect(contact.normal.x).toBeCloseTo(SQRT_05, 5)
    expect(contact.normal.y).toBeCloseTo(SQRT_05, 5)
  }
})