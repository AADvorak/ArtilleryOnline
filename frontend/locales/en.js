import {menu} from "~/locales/en/menu.js";
import {appearance} from "~/locales/en/appearance.js";
import {common} from "~/locales/en/common.js";
import {vehicleSelector} from "~/locales/en/vehicle-selector.js";
import {battle} from "~/locales/en/battle.js";
import {room} from "~/locales/en/room.js";
import {roomMembersTable} from "~/locales/en/room-members-table.js";
import {onlineUsersTable} from "~/locales/en/online-users-table.js";
import {controls} from "~/locales/en/controls.js";
import {settings} from "~/locales/en/settings.js";
import {sounds} from "~/locales/en/sounds.js";
import {user} from "~/locales/en/user.js";
import {commonHistory} from "~/locales/en/common-history.js";
import {battleHistory} from "~/locales/en/battle-history.js";
import {battleStatistics} from "~/locales/en/battle-statistics.js";
import {battleHistoryFiltersForm} from "~/locales/en/battle-history-filters-form.js";
import {roomInvitationCard} from "~/locales/en/room-invitation-card.js";
import {leaveBattleDialog} from "~/locales/en/leave-battle-dialog.js";
import {serverMessages} from "~/locales/en/server-messages.js";
import {validationMessages} from "~/locales/en/validation-messages.js";
import {finishBattleDialog} from "~/locales/en/finish-battle-dialog.js";
import {vehicleConfigs} from "~/locales/en/vehicle-configs.js";
import {vehicleSpecsDialog} from "~/locales/en/vehicle-specs-dialog.js";
import {gunSpecsDialog} from "~/locales/en/gun-specs-dialog.js";
import {shellSpecsDialog} from "~/locales/en/shell-specs-dialog.js";
import {battleHeader} from "~/locales/en/battle-header.js";
import {names} from "~/locales/en/names.js";
import {descriptions} from "~/locales/en/descriptions.js";
import {serverCounts} from "~/locales/en/server-counts.js";

export const en = {
  menu,
  appearance,
  battle,
  battleHeader,
  battleHistory,
  battleHistoryFiltersForm,
  battleStatistics,
  common,
  commonHistory,
  connectionLostDialog: {
    message: 'Connection to server lost',
    reload: 'Reload page'
  },
  controls,
  descriptions,
  finishBattleDialog,
  fullScreenBtn: {
    enter: 'Enter full screen',
    exit: 'Exit full screen',
    error: 'Unable to enter full screen mode'
  },
  gunSpecsDialog,
  jetBar: {
    title: 'Jet'
  },
  leaveBattleDialog,
  login: {
    title: 'login'
  },
  messagesMenu: {
    messages: 'Messages',
    noMessages: 'You have no messages'
  },
  names,
  vehicleSelector,
  vehicleConfigs,
  onlineUsersTable,
  profile: {
    title: 'user / profile'
  },
  rooms: {
    title: 'rooms',
    createRoom: 'Create room',
    backToRoom: 'Back to room'
  },
  room,
  roomInvitationCard,
  roomMembersTable,
  shellSpecsDialog,
  serverCounts,
  settings,
  signup: {
    title: 'signup'
  },
  serverMessages,
  sounds,
  user,
  validationMessages,
  vehicleSpecsDialog,
}
