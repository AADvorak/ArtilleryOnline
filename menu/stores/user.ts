import {ref} from 'vue'
import {defineStore} from 'pinia'
import type {User} from "~/data/model";

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>()

  return {user}
})
