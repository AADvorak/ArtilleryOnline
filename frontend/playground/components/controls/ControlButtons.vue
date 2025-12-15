<script setup lang="ts">
import {useGlobalStateStore} from "~/stores/global-state";
import {VerticalTooltipLocation} from "~/data/model";
import {useUserSettingsStore} from "~/stores/user-settings";
import {ControlButtonsAlignments} from "~/dictionary/control-buttons-alignments";
import {AppearancesNames} from "~/dictionary/appearances-names";
import ControlButtonsMoving from "~/playground/components/controls/ControlButtonsMoving.vue";
import ControlButtonsGunRotating from "~/playground/components/controls/ControlButtonsGunRotating.vue";
import ControlButtonsShooting from "~/playground/components/controls/ControlButtonsShooting.vue";
import ControlButtonsJet from "~/playground/components/controls/ControlButtonsJet.vue";

const props = defineProps<{
  mouseEvents: boolean
}>()

const globalStateStore = useGlobalStateStore()
const userSettingsStore = useUserSettingsStore()

const showTooltip = computed(() => globalStateStore.showHelp === VerticalTooltipLocation.TOP)

const controlButtonsAlignment = computed(() =>
    userSettingsStore.appearancesOrDefaultsNameValueMapping[AppearancesNames.CONTROL_BUTTONS_ALIGNMENT])

const isVertical = computed(() => controlButtonsAlignment.value === ControlButtonsAlignments.CENTER_VERTICAL)

const leftToolbarClass = computed(() => {
  switch (controlButtonsAlignment.value) {
    case ControlButtonsAlignments.BOTTOM_HORIZONTAL:
      return 'bottom-left-controls'
    case ControlButtonsAlignments.CENTER_VERTICAL:
      return 'left-controls'
    default:
      return ''
  }
})

const rightToolbarClass = computed(() => {
  switch (controlButtonsAlignment.value) {
    case ControlButtonsAlignments.BOTTOM_HORIZONTAL:
      return 'bottom-right-controls'
    case ControlButtonsAlignments.CENTER_VERTICAL:
      return 'right-controls'
    default:
      return ''
  }
})
</script>

<template>
  <v-toolbar absolute :class="leftToolbarClass" color="transparent">
    <div v-if="isVertical" class="d-flex flex-column align-center">
      <ControlButtonsShooting
          :mouse-events="props.mouseEvents"
          :show-tooltip="showTooltip"
      />
      <div>
        <ControlButtonsMoving
            :mouse-events="props.mouseEvents"
            :show-tooltip="showTooltip"
        />
      </div>
    </div>
    <div v-else>
      <ControlButtonsMoving
          :mouse-events="props.mouseEvents"
          :show-tooltip="showTooltip"
      />
      <ControlButtonsShooting
          :mouse-events="props.mouseEvents"
          :show-tooltip="showTooltip"
      />
    </div>
  </v-toolbar>

  <v-toolbar absolute :class="rightToolbarClass" color="transparent">
    <div v-if="isVertical" class="d-flex flex-column align-center">
      <ControlButtonsShooting
          :mouse-events="props.mouseEvents"
          :show-tooltip="showTooltip"
      />
      <div>
        <ControlButtonsGunRotating
            :mouse-events="props.mouseEvents"
            :show-tooltip="showTooltip"
        />
      </div>
      <ControlButtonsJet
          :mouse-events="props.mouseEvents"
          :show-tooltip="showTooltip"
      />
    </div>
    <div v-else>
      <ControlButtonsShooting
          :mouse-events="props.mouseEvents"
          :show-tooltip="showTooltip"
      />
      <ControlButtonsJet
          :mouse-events="props.mouseEvents"
          :show-tooltip="showTooltip"
      />
      <ControlButtonsGunRotating
          :mouse-events="props.mouseEvents"
          :show-tooltip="showTooltip"
      />
    </div>
  </v-toolbar>
</template>

<style scoped>
.bottom-left-controls {
  bottom: 0;
  left: 0;
  width: auto;
}

.bottom-right-controls {
  bottom: 0;
  right: 0;
  width: auto;
}

.left-controls {
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: auto;
  height: auto;
  background: transparent !important;
}

.right-controls {
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: auto;
  height: auto;
  background: transparent !important;
}

.left-controls :deep(.v-toolbar__content),
.right-controls :deep(.v-toolbar__content) {
  padding: 0;
  height: auto !important;
  display: flex;
  flex-direction: column;
}

.d-flex.flex-column.align-center {
  gap: 8px;
}
</style>
