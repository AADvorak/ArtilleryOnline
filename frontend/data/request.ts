import type {BattleType} from "~/playground/data/battle";

export interface RegisterRequest {
  email: string
  nickname: string
  password: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface EditUserRequest {
  email: string
  nickname: string
}

export interface SortRequest {
  by: string
  dir: string
}

export interface PageRequest<FiltersRequest> {
  page: number
  size: number
  sort?: SortRequest
  filters?: FiltersRequest
}

export interface UserBattleHistoryFiltersRequest {
  battleType?: BattleType
  vehicleName?: string
}
