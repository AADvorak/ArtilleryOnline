import type {DroneSpecs, GunSpecs, JetSpecs} from "@/playground/data/specs";
import type {Ammo, Missiles} from "@/playground/data/common";

export interface VehicleConfig {
  gun: GunSpecs
  jet: JetSpecs
  drone?: DroneSpecs
  ammo: Ammo
  missiles: Missiles
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
}

export interface BoxConfig {
  color?: string
}
