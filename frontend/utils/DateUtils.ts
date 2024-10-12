export const DateUtils = {

  getClientDate(serverDate: string, serverOffset: number) {
    const date = new Date(serverDate)
    const clientOffset = date.getTimezoneOffset() * 60
    date.setTime(date.getTime() - (serverOffset + clientOffset) * 1000)
    return date
  }
}
