import {useSettingsStore} from '~/stores/settings'

export const DateUtils = {

  getClientDate(serverDate: string) {
    const settingsStore = useSettingsStore()
    const date = new Date(serverDate)
    const serverOffset = settingsStore.timeZoneOffset
    const clientOffset = date.getTimezoneOffset() * 60
    date.setTime(date.getTime() - (serverOffset + clientOffset) * 1000)
    return date
  },

  getClientDateLocaleString(serverDate: string) {
    return this.getClientDate(serverDate).toLocaleString()
  }
}
