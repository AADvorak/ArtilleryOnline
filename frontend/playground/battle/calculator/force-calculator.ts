import type {BodyForce} from "~/playground/battle/calculator/body-force";
import type {BodyCalculations} from "~/playground/data/calculations";
import type {BattleModel} from "~/playground/data/model";

export interface ForceCalculator {
  calculate: (calculations: BodyCalculations, battleModel: BattleModel) => BodyForce[]
}
