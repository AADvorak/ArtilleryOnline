import type {RoomModel} from "~/playground/data/model.ts";
import {test} from "@jest/globals";
import {expect} from "@jest/globals";
import roomModel from "./room-model.json";
import {Circle} from "~/playground/data/geometry";
import {GroundContactUtils} from "~/playground/utils/ground-contact-utils";
import {Constants} from "~/playground/data/constants";

const GROUND_LEVEL = 1.0
const RADIUS = 0.5
const EDGE_HEIGHT = GROUND_LEVEL + RADIUS - Constants.INTERPENETRATION_THRESHOLD
const SMALL_DELTA = 0.00001

test('circle ground contact exists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const circle = new Circle({x: 10.0, y: EDGE_HEIGHT - SMALL_DELTA}, RADIUS)
  const contact = GroundContactUtils.getCircleGroundContact(circle, model, false)
  expect(contact).toBeDefined()
})

test('circle ground contact not exists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const circle = new Circle({x: 10.0, y: EDGE_HEIGHT + SMALL_DELTA}, RADIUS)
  const contact = GroundContactUtils.getCircleGroundContact(circle, model, false)
  expect(contact).toBeNull()
})
