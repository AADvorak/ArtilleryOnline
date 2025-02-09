import type {GunSpecs, JetSpecs} from "@/playground/data/specs";
import type {Ammo, Missiles} from "@/playground/data/common";

export interface VehicleConfig {
  gun: GunSpecs
  jet: JetSpecs
  ammo: Ammo
  missiles: Missiles
  color?: string
}

export interface RoomConfig {
  background: number
  groundTexture: number
}
