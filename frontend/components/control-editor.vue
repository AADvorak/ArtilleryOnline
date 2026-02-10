<script setup lang="ts">
import type {UserSetting} from "~/data/model";

const props = defineProps<{
  control: UserSetting
}>()

const emit = defineEmits(['edit-start', 'edit-end'])

const editing = ref<boolean>(false)

const buttonText = computed(() => {
  if (editing.value) {
    return 'Press key...'
  }
  return removeKeyStr(props.control.value).toLowerCase()
})

function edit() {
  emit('edit-start')
  editing.value = true
  addEventListener('keyup', keyPressed)
}

function keyPressed(e) {
  emit('edit-end', {
    name: props.control.name,
    value: e.code
  })
  editing.value = false
  removeEventListener('keyup', keyPressed)
}

function removeKeyStr(str: string) {
  return str.replace('Key', '')
}
</script>

<template>
  <span v-if="editing">{{ buttonText }}</span>
  <v-hotkey v-else style="cursor: pointer;" :keys="buttonText" @click="edit"/>
</template>
