import {menu} from "~/locales/ru/menu.js";
import {appearance} from "~/locales/ru/appearance.js";
import {common} from "~/locales/ru/common.js";
import {vehicleSelector} from "~/locales/ru/vehicle-selector.js";
import {battle} from "~/locales/ru/battle.js";
import {room} from "~/locales/ru/room.js";
import {roomMembersTable} from "~/locales/ru/room-members-table.js";
import {onlineUsersTable} from "~/locales/ru/online-users-table.js";
import {controls} from "~/locales/ru/controls.js";
import {settings} from "~/locales/ru/settings.js";
import {sounds} from "~/locales/ru/sounds.js";
import {user} from "~/locales/ru/user.js";
import {commonHistory} from "~/locales/ru/common-history.js";
import {battleHistory} from "~/locales/ru/battle-history.js";
import {battleStatistics} from "~/locales/ru/battle-statistics.js";
import {battleHistoryFiltersForm} from "~/locales/ru/battle-history-filters-form.js";
import {roomInvitationCard} from "~/locales/ru/room-invitation-card.js";
import {leaveBattleDialog} from "~/locales/ru/leave-battle-dialog.js";
import {serverMessages} from "~/locales/ru/server-messages.js";
import {validationMessages} from "~/locales/ru/validation-messages.js";
import {finishBattleDialog} from "~/locales/ru/finish-battle-dialog.js";
import {vehicleConfigs} from "~/locales/ru/vehicle-configs.js";
import {vehicleSpecsDialog} from "~/locales/ru/vehicle-specs-dialog.js";
import {gunSpecsDialog} from "~/locales/ru/gun-specs-dialog.js";
import {shellSpecsDialog} from "~/locales/ru/shell-specs-dialog.js";
import {battleHeader} from "~/locales/ru/battle-header.js";

export const ru = {
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
    message: 'Соединение с сервером потеряно',
    reload: 'Перезагрузить страницу'
  },
  controls,
  finishBattleDialog,
  gunSpecsDialog,
  jetBar: {
    title: 'Джетпак'
  },
  leaveBattleDialog,
  login: {
    title: 'вход'
  },
  messagesMenu: {
    messages: 'Сообщения',
    noMessages: 'Нет сообщений'
  },
  vehicleSelector,
  vehicleConfigs,
  onlineUsersTable,
  profile: {
    title: 'пользователь / профиль'
  },
  rooms: {
    title: 'комнаты',
    createRoom: 'Создать комнату',
    backToRoom: 'Вернуться в комнату'
  },
  room,
  roomInvitationCard,
  roomMembersTable,
  shellSpecsDialog,
  settings,
  signup: {
    title: 'регистрация'
  },
  serverMessages,
  sounds,
  user,
  validationMessages,
  vehicleSpecsDialog,
}
