import {useUserStore} from "~/stores/user";
import {ApiRequestSender} from "~/api/api-request-sender";
import type {User} from "~/data/model";

const UNSIGNED_PATHS = ['/', '/sign-in', 'sign-up']

export default defineNuxtRouteMiddleware(async (to) => {
  const userStore = useUserStore()
  if (!userStore.user) {
    try {
      userStore.user = await new ApiRequestSender().getJson<User>('/users/me')
    } catch (e) {
      console.log(e)
    }
  }
  if (!userStore.user && !UNSIGNED_PATHS.includes(to.path)) {
    return navigateTo('/')
  }
  if (!!userStore.user && UNSIGNED_PATHS.includes(to.path)) {
    return navigateTo('/menu')
  }
})
