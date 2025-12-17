import {BattlefieldAlignments} from "~/dictionary/battlefield-alignments";
import {ControlButtonsAlignments} from "~/dictionary/control-buttons-alignments";
import {ControlsTypes} from "~/dictionary/controls-types";

export const DefaultAppearances = [
  {name: 'language', value: 'en'},
  {name: 'vehicleColor', value: '', description: 'Vehicle color'},
  {name: 'showNicknamesAboveVehicles', value: '1', description: 'Show nicknames above vehicles'},
  {name: 'showHpBarsAboveVehicles', value: '1', description: 'Show HP bars above vehicles'},
  {name: 'showAllPlayersHpBarsInTopBar', value: '0', description: 'Show all players HP bars in top bar'},
  {name: 'showGroundTextureAndBackground', value: '0', description: 'Show ground texture and background'},
  {name: 'showControlButtons', value: '0', description: 'Show control buttons'},
  {name: 'battlefieldAlignment', value: BattlefieldAlignments.BY_SCREEN_SIZE, description: 'Battlefield alignment'},
  {name: 'controlButtonsAlignment', value: ControlButtonsAlignments.BOTTOM},
  {name: 'controlsType', value: ControlsTypes.JOYSTICKS},
]
