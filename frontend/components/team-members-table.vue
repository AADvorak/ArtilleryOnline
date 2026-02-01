<script setup lang="ts">
import {mdiAccountRemove, mdiCrown, mdiKnifeMilitary} from "@mdi/js";
import Draggable from "vuedraggable";
import {useI18n} from "vue-i18n";
import {ApiRequestSender} from "~/api/api-request-sender";
import {useRoomStore} from "~/stores/room";
import type {RoomMember} from "~/data/model";
import {useRequestErrorHandler} from "~/composables/request-error-handler";

const props = defineProps<{
  teamId: number
}>()

const {t} = useI18n()
const api = new ApiRequestSender()

const roomStore = useRoomStore()

const teamMembers = ref<RoomMember[]>([])

const room = computed(() => {
  return roomStore.room
})

onMounted(() => {
  room.value && setTeamMembers(room.value.members)
})

watch(room, value => {
  value && setTeamMembers(value.members)
})

function setTeamMembers(roomMembers: RoomMember[][]) {
  teamMembers.value = roomMembers[props.teamId] ? roomMembers[props.teamId].sort(sortMembers) : []
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
      // todo return element to another component
      room.value && setTeamMembers(room.value.members)
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
        v-model="teamMembers"
        tag="tbody"
        group="teams"
        item-key="nickname"
        @change="onChange"
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
</template>

<style scoped>
.btn-column {
  text-align: right;
}
</style>
