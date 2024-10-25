export interface User {
  id: number
  email: string
  nickname: string
  token: string
}

export interface RoomMember {
  nickname: string
  selectedVehicle?: string
}

export interface CsrfToken {
  headerName: string
  token: string
}

export interface UserSetting {
  name: string
  value: string
  description?: string
}

export interface UserSettingsNameValueMapping {
  [name: string]: string
}

export interface UserSettingsValueNameMapping {
  [value: string]: string
}
