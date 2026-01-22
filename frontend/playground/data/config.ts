import type {DroneSpecs, GunSpecs, JetSpecs, MissileLauncherSpecs} from "@/playground/data/specs";
import type {Ammo} from "@/playground/data/common";
import type {Shape} from "~/playground/data/shapes";

export interface VehicleConfig {
  gun: GunSpecs
  jet: JetSpecs
  drone?: DroneSpecs
  ammo: AmmoConfig[]
  missileLauncher?: MissileLauncherSpecs
  color?: string
}

export interface RoomConfig {
  background: number
  groundTexture: number
}

export interface DroneConfig {
  gun: GunSpecs
  ammo: Ammo
  color?: string
}

export interface ParticleConfig {
  color?: string
  text?: string
  size?: number
  groundTexture?: boolean
}

export interface BodyParticleConfig {
  color?: string
  groundTexture?: boolean
  shape: Shape
}

export interface BoxConfig {
  color?: string
  amount: number
}

export interface AmmoConfig {
  name: string
  amount: number
}
