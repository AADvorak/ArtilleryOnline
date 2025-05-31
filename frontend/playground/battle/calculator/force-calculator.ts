import type {BodyForce} from "~/playground/battle/calculator/body-force";
import type {BodyCalculations} from "~/playground/data/calculations";
import type {BattleModel} from "~/playground/data/model";

export interface ForceCalculator<C extends BodyCalculations> {
  calculate: (calculations: C, battleModel: BattleModel) => BodyForce[]
}
