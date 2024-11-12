export interface User {
  id: number
  email: string
  nickname: string
  token: string
}

export interface RoomMember {
  nickname: string
  selectedVehicle?: string
  owner: boolean
}

export interface Room {
  members: RoomMember[]
  inBattle: boolean
  deleted: boolean
}

export interface Message {
  id: string
  text: string
  time: string
}

export interface RoomInvitation {
  id: string
  inviterNickname: string
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

export enum UserSettingsGroup {
  CONTROLS = 'controls',
  APPEARANCES = 'appearances',
  SOUNDS = 'sounds'
}

export interface UserSettingsGroupsMap {
  [groupKey: string]: UserSetting[]
}

export interface UserSettingsNameValueMapping {
  [name: string]: string
}

export interface UserSettingsValueNameMapping {
  [value: string]: string
}
