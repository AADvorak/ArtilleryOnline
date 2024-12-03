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
  },

  getISOString(date: Date) {
    return date.toISOString().split('.')[0]
  },

  getPlusDay(date: Date) {
    const datePlusDay = new Date(date)
    datePlusDay.setDate(datePlusDay.getDate() + 1)
    return datePlusDay
  },

  getMinusDays(date: Date, days: number) {
    const datePlusDay = new Date(date)
    datePlusDay.setDate(datePlusDay.getDate() - days)
    return datePlusDay
  },

  getTodayBegin() {
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    return today
  }
}
