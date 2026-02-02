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
    <div>
      <div v-show="roomStore.room?.teamMode">
        {{ t('common.team') }} 1
      </div>
      <team-members-table
          ref="team1MembersTable"
          :team-id="0"
          @reset="onResetTeams"
      />
    </div>
    <div v-if="roomStore.room?.teamMode">
      <v-divider class="mb-4 mt-4" :thickness="2"/>
      <div>
        {{ t('common.team') }} 2
      </div>
      <team-members-table
          ref="team2MembersTable"
          :team-id="1"
          @reset="onResetTeams"
      />
    </div>
  </div>
</template>
