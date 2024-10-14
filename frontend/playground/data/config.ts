import type {GunSpecs, JetSpecs} from "@/playground/data/specs";
import type {Ammo} from "@/playground/data/common";

export interface VehicleConfig {
  gun: GunSpecs
  jet: JetSpecs
  ammo: Ammo
}
