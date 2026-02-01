<script setup lang="ts">
import {useRoomStore} from '~/stores/room'
import {mdiCrown, mdiKnifeMilitary, mdiAccountRemove} from '@mdi/js'
import type {RoomMember} from '~/data/model'
import {ApiRequestSender} from '~/api/api-request-sender'
import {useRequestErrorHandler} from "~/composables/request-error-handler";
import {useI18n} from "vue-i18n";
import Draggable from "vuedraggable";

const {t} = useI18n()
const api = new ApiRequestSender()

const roomStore = useRoomStore()

const team1Members = ref<RoomMember[]>([])
const team2Members = ref<RoomMember[]>([])

const room = computed(() => {
  return roomStore.room
})

onMounted(() => {
  room.value && setTeamsMembers(room.value.members)
})

watch(room, value => {
  value && setTeamsMembers(value.members)
})

function setTeamsMembers(roomMembers: RoomMember[][]) {
  team1Members.value = roomMembers[0] ? roomMembers[0].sort(sortMembers) : []
  team2Members.value = roomMembers[1] ? roomMembers[1].sort(sortMembers) : []
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

async function onChange(teamId: number, event: any) {
  if (event.added) {
    const nickname = event.added.element.nickname
    try {
      await api.putJson<undefined, undefined>(`/rooms/my/members/${nickname}/change-team/${teamId}`, undefined)
    } catch (e) {
      room.value && setTeamsMembers(room.value.members)
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
</script>

<template>
  <div>
    <div v-if="team1Members.length">
      <div v-show="roomStore.room?.teamMode">
        {{ t('common.team') }} 1
      </div>
      <v-table density="compact">
        <thead>
        <tr>
          <th class="text-left">
            {{ t('common.nickname') }}
          </th>
          <th class="text-left">
            {{ t('roomMembersTable.selectedVehicle') }}
          </th>
          <th v-if="roomStore.userIsRoomOwner"></th>
        </tr>
        </thead>
        <draggable
            v-model="team1Members"
            tag="tbody"
            group="teams"
            item-key="nickname"
            @change="event => onChange(0, event)"
        >
          <template #item="{ element }">
            <tr>
              <td>
                <v-icon :icon="element.owner ? mdiCrown : mdiKnifeMilitary" />
                {{ element.nickname }}
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
      </v-table>
    </div>
    <div v-if="roomStore.room?.teamMode">
      <v-divider class="mb-4 mt-4" :thickness="2"/>
      <div>
        {{ t('common.team') }} 2
      </div>
      <v-table density="compact">
        <thead>
        <tr>
          <th class="text-left">
            {{ t('common.nickname') }}
          </th>
          <th class="text-left">
            {{ t('roomMembersTable.selectedVehicle') }}
          </th>
          <th v-if="roomStore.userIsRoomOwner"></th>
        </tr>
        </thead>
        <draggable
            v-model="team2Members"
            tag="tbody"
            group="teams"
            item-key="nickname"
            @change="event => onChange(1, event)"
        >
          <template #item="{ element }">
            <tr>
              <td>
                <v-icon :icon="element.owner ? mdiCrown : mdiKnifeMilitary" />
                {{ element.nickname }}
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
      </v-table>
    </div>
  </div>
</template>

<style scoped>
.btn-column {
  text-align: right;
}
</style>
