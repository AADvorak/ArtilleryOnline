import {BattlefieldAlignments} from "~/dictionary/battlefield-alignments";
import {ScreenControlsAlignments} from "~/dictionary/screen-controls-alignments";
import {ControlsTypes} from "~/dictionary/controls-types";
import {AppearancesNames} from "~/dictionary/appearances-names";

export const DefaultAppearances = [
  {name: AppearancesNames.LANGUAGE, value: 'en'},
  {name: AppearancesNames.VEHICLE_COLOR, value: ''},
  {name: AppearancesNames.NICKNAMES_ABOVE, value: '1'},
  {name: AppearancesNames.HP_ABOVE, value: '1'},
  {name: AppearancesNames.ALL_HP_TOP, value: '0'},
  {name: AppearancesNames.GROUND_TEXTURE_BACKGROUND, value: '1'},
  {name: AppearancesNames.SHOW_SCREEN_CONTROLS, value: '0'},
  {name: AppearancesNames.BATTLEFIELD_ALIGNMENT, value: BattlefieldAlignments.BY_SCREEN_SIZE},
  {name: AppearancesNames.SCREEN_CONTROLS_ALIGNMENT, value: ScreenControlsAlignments.BOTTOM},
  {name: AppearancesNames.CONTROLS_TYPE, value: ControlsTypes.JOYSTICKS},
]
