<script setup lang="ts">
import type {BaseModel} from "~/playground/data/model";
import type {CapturePoints} from "~/playground/data/state";
import BattleLinearProgress from "~/playground/components/BattleLinearProgress.vue";
import {DefaultColors} from "~/dictionary/default-colors";
import {useI18n} from "vue-i18n";
import {computed} from "vue";

const props = defineProps<{
  baseModel: BaseModel
  usersTeamId: number
}>()

const {t} = useI18n()

const progressValue = computed(() => {
  const maxCapturePoints = props.baseModel.specs.capturePoints
  const sumCapturePoints = Math.min(
      getSumCapturePoints(props.baseModel.state.capturePoints),
      props.baseModel.specs.capturePoints
  )
  return 100 * sumCapturePoints / maxCapturePoints
})

const progressText = computed(() => {
  return t('common.team') + ' '
      + (props.baseModel.state.capturingTeamId! + 1)
      + ' ' + t('common.capturingBase')
})

const progressColor = computed(() => {
  return props.baseModel.state.capturingTeamId === props.usersTeamId
      ? DefaultColors.ALLY_TEAM
      : DefaultColors.ENEMY_TEAM
})

function getSumCapturePoints(capturePoints: CapturePoints) {
  return Object.values(capturePoints).reduce((a, c) => a + c, 0)
}
</script>

<template>
  <div class="progress-wrapper">
    <battle-linear-progress
        :value="progressValue"
        :text="progressText"
        :color="progressColor"
    />
  </div>
</template>

<style scoped>
.progress-wrapper {
  max-width: 250px;
  min-width: 250px;
}
</style>
