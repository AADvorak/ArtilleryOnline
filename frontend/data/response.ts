import type {VehicleSpecs} from "~/playground/data/specs";
import type {Locale} from "~/data/model";

export interface ValidationResponse {
  locale?: Locale
  field: string
  message: string
}

export interface ApiErrorResponse {
  code: string
  message?: string
  params?: object
  locale?: Locale
  validation?: ValidationResponse[]
}

export interface ErrorResponse {
  status: number
  error: ApiErrorResponse
}

export interface Validation {
  locale?: Locale
  message?: string
}

export interface FormValidation {
  [fieldName: string]: Validation[]
}

export interface FormValues {
  [fieldName: string]: string
}

export interface UserBattleQueueParams {
  selectedVehicle: string
}

export interface UserBattleQueueResponse {
  addTime: string
  params: UserBattleQueueParams
}

export interface VehicleSpecsResponse {
  [name: string]: VehicleSpecs
}

export interface PageResponse<T> {
  items: T[]
  itemsLength: number
}
