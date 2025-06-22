import type {RoomModel} from "~/playground/data/model.ts";
import {test} from "@jest/globals";
import {expect} from "@jest/globals";
import roomModel from "./room-model.json";
import {Circle} from "~/playground/data/geometry";
import {GroundContactUtils} from "~/playground/utils/ground-contact-utils";

test('circle ground contact exists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const circle = new Circle({x: 10.0, y: 1.5}, 0.6)
  const contact = GroundContactUtils.getCircleGroundContact(circle, model, false)
  expect(contact).toBeDefined()
})

test('circle ground contact not exists', () => {
  // @ts-ignore
  const model = roomModel as RoomModel
  const circle = new Circle({x: 10.0, y: 1.5}, 0.5)
  const contact = GroundContactUtils.getCircleGroundContact(circle, model, false)
  expect(contact).toBeNull()
})
