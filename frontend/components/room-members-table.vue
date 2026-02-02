<script setup lang="ts">
import TeamMembersTable from "~/components/team-members-table.vue";
import {useI18n} from "vue-i18n";
import {useRoomStore} from "~/stores/room";

const {t} = useI18n()

const roomStore = useRoomStore()

const team1MembersTable = ref<InstanceType<typeof TeamMembersTable> | undefined>()
const team2MembersTable = ref<InstanceType<typeof TeamMembersTable> | undefined>()

function onResetTeams() {
  team1MembersTable.value?.resetTeamMembers()
  team2MembersTable.value?.resetTeamMembers()
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
      <team-members-table
          ref="team1MembersTable"
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
        <team-members-table
            ref="team2MembersTable"
            :team-id="1"
            @reset="onResetTeams"
        />
      </template>
    </v-table>
  </div>
</template>
