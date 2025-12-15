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

const innerToolbarClass = computed(() => {
  switch (controlButtonsAlignment.value) {
    case ControlButtonsAlignments.CENTER_VERTICAL:
      return 'd-flex flex-column align-center'
    default:
      return ''
  }
})

const leftButtonClass = computed(() => {
  switch (controlButtonsAlignment.value) {
    case ControlButtonsAlignments.BOTTOM_HORIZONTAL:
      return 'mr-2'
    case ControlButtonsAlignments.CENTER_VERTICAL:
      return 'mb-2'
    default:
      return ''
  }
})

const rightButtonClass = computed(() => {
  switch (controlButtonsAlignment.value) {
    case ControlButtonsAlignments.BOTTOM_HORIZONTAL:
      return 'ml-2'
    case ControlButtonsAlignments.CENTER_VERTICAL:
      return 'mb-2'
    default:
      return ''
  }
})
</script>

<template>
  <v-toolbar absolute :class="leftToolbarClass" color="transparent">
    <div :class="innerToolbarClass">
      <ControlButtonsMoving
          :mouse-events="props.mouseEvents"
          :button-class="leftButtonClass"
          :show-tooltip="showTooltip"
      />
      <ControlButtonsShooting
          :mouse-events="props.mouseEvents"
          :button-class="String('')"
          :show-tooltip="showTooltip"
      />
    </div>
  </v-toolbar>

  <v-toolbar absolute :class="rightToolbarClass" color="transparent">
    <div :class="innerToolbarClass">
      <ControlButtonsShooting
          :mouse-events="props.mouseEvents"
          :button-class="rightButtonClass"
          :show-tooltip="showTooltip"
      />
      <ControlButtonsJet
          :mouse-events="props.mouseEvents"
          :button-class="rightButtonClass"
          :show-tooltip="showTooltip"
      />
      <ControlButtonsGunRotating
          :mouse-events="props.mouseEvents"
          :button-class="rightButtonClass"
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
