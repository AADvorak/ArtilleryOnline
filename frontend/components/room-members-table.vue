<script setup lang="ts">
import TeamMembersRows from "~/components/team-members-rows.vue";
import {useI18n} from "vue-i18n";
import {useRoomStore} from "~/stores/room";

const {t} = useI18n()

const roomStore = useRoomStore()

const team1MembersRows = ref<InstanceType<typeof TeamMembersRows> | undefined>()
const team2MembersRows = ref<InstanceType<typeof TeamMembersRows> | undefined>()

function onResetTeams() {
  team1MembersRows.value?.resetTeamMembers()
  team2MembersRows.value?.resetTeamMembers()
}
</script>

<template>
  <div class="mb-4">
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
      <team-members-rows
          ref="team1MembersRows"
          :team-id="0"
          @reset="onResetTeams"
      />
      <template v-if="roomStore.room?.teamMode">
        <tbody>
        <tr>
          <td colspan="3" style="padding: 0;">
            <v-divider :thickness="2"/>
          </td>
        </tr>
        </tbody>
        <team-members-rows
            ref="team2MembersRows"
            :team-id="1"
            @reset="onResetTeams"
        />
      </template>
    </v-table>
    <div v-if="roomStore.room?.teamMode" class="mt-4">
      {{ roomStore.userIsRoomOwner ? t('roomMembersTable.movePlayersTip') : t('roomMembersTable.moveYourselfTip') }}
    </div>
  </div>
</template>
