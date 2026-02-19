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
import {names} from "~/locales/ru/names.js";
import {descriptions} from "~/locales/ru/descriptions.js";
import {serverCounts} from "~/locales/ru/server-counts.js";
import {navigation} from "~/locales/ru/navigation.js";
import {messenger} from "~/locales/ru/messenger.js";

export const ru = {
  menu,
  appearance,
  battle,
  battleHeader,
  battleHistory,
  battleHistoryFiltersForm,
  battleStatistics,
  battleTimer: {
    battleStartsIn: 'До начала боя',
    battleEndsIn: 'До конца боя',
  },
  common,
  commonHistory,
  connectionLostDialog: {
    message: 'Соединение с сервером потеряно',
    reload: 'Перезагрузить страницу'
  },
  controls,
  descriptions,
  finishBattleDialog,
  fullScreenBtn: {
    enter: 'На полный экран',
    exit: 'Полный экран - выход',
    error: 'Не удалось перейти в полноэкранный режим'
  },
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
  messenger,
  names,
  navigation,
  vehicleSelector,
  vehicleConfigs,
  onlineUsersTable,
  profile: {
    title: 'пользователь / профиль',
    cannotBeChanged: 'Нельзя изменить находясь в комнате или в бою'
  },
  rooms: {
    createRoom: 'Создать комнату',
    backToRoom: 'Вернуться в комнату',
    owner: 'Владелец',
    membersCount: 'Кол-во участников',
    inBattle: 'В бою',
    enter: 'Войти',
    noRooms: 'Нет открытых комнат'
  },
  room,
  roomInvitationCard,
  roomMembersTable,
  shellSpecsDialog,
  serverCounts,
  settings,
  signup: {
    title: 'регистрация'
  },
  serverMessages,
  sounds,
  unavailable: {
    title: 'игра недоступна',
    message: 'Сервер не отвечает. Повторите попытку позже. Просим прощения за неудобства.'
  },
  user,
  validationMessages,
  vehicleSpecsDialog,
}
