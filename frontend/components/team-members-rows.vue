<script setup lang="ts">
import {mdiAccountRemove, mdiCrown, mdiAccount, mdiRobot} from "@mdi/js";
import Draggable from "vuedraggable";
import {useI18n} from "vue-i18n";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRoomStore} from "~/stores/room";
import type {RoomMember} from "~/data/model";
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import {useUserStore} from "~/stores/user";
import {computed} from "vue";
import {DefaultColors} from "~/dictionary/default-colors";

const props = defineProps<{
  teamId: number
}>()

const emit = defineEmits(['reset'])

const {t} = useI18n()
const api = new ApiRequestSender()

const roomStore = useRoomStore()
const userStore = useUserStore()

const teamMembers = ref<RoomMember[]>([])

const room = computed(() => {
  return roomStore.room
})

const teamColor = computed<string>(() => {
  if (room.value!.members.length <= 1) {
    return ''
  }
  const isUsersTeam = teamMembers.value.filter(member => member.nickname === userStore.user!.nickname).length > 0
  return isUsersTeam ? DefaultColors.ALLY_TEAM : DefaultColors.ENEMY_TEAM
})

onMounted(() => {
  resetTeamMembers()
})

watch(room, value => {
  resetTeamMembers()
})

function resetTeamMembers() {
  room.value && setTeamMembers(room.value.members)
}

function setTeamMembers(roomMembers: RoomMember[][]) {
  const members = roomMembers[props.teamId]
  teamMembers.value = members ? members.sort(sortMembers) : []
}

function sortMembers(a: RoomMember, b: RoomMember) {
  if (a.owner) {
    return -1
  }
  if (b.owner) {
    return 1
  }
  return a.nickname > b.nickname ? 1 : -1
}

async function onChange(event: any) {
  if (event.added) {
    const nickname = event.added.element.nickname
    try {
      await api.putJson<undefined, undefined>(`/rooms/my/members/${nickname}/change-team/${props.teamId}`, undefined)
    } catch (e) {
      emit('reset')
      useRequestErrorHandler().handle(e)
    }
  }
}

async function removeUserFromRoom(nickname: string) {
  try {
    await api.delete(`/rooms/my/members/${nickname}`)
  } catch (e) {
    useRequestErrorHandler().handle(e)
  }
}

function getMembersIcon(member: RoomMember) {
  if (member.owner) {
    return mdiCrown
  }
  if (member.bot) {
    return mdiRobot
  }
  return mdiAccount
}

defineExpose({
  resetTeamMembers
})
</script>

<template>
  <tbody v-if="!teamMembers.length">
  <tr>
    <td colspan="3" style="padding: 0;">
      {{ t('room.noPlayersInTeam') }}
    </td>
  </tr>
  </tbody>
  <draggable
      v-model="teamMembers"
      tag="tbody"
      group="teams"
      item-key="nickname"
      @change="onChange"
  >
    <template #item="{ element }">
      <tr>
        <td class="icon-column">
          <v-icon
              :style="teamColor ? 'color: ' + teamColor : ''"
              :icon="getMembersIcon(element)"
          />
        </td>
        <td>
          <span :class="element.nickname === userStore.user!.nickname ? 'players-nickname' : ''">
            {{ element.nickname }}
          </span>
        </td>
        <td>{{ element.selectedVehicle ? t(`names.vehicles.${element.selectedVehicle}`) : '' }}</td>
        <td v-if="roomStore.userIsRoomOwner" class="btn-column">
          <icon-btn
              v-if="!element.owner"
              color="error"
              :icon="mdiAccountRemove"
              :tooltip="t('roomMembersTable.removeFromRoom')"
              @click="removeUserFromRoom(element.nickname)"
          />
        </td>
      </tr>
    </template>
  </draggable>
</template>

<style scoped>
.icon-column {
  max-width: 36px;
}

.btn-column {
  text-align: right;
}

.players-nickname {
  color: #fb8c00;
}
</style>
