import type {VehicleSpecs} from "~/playground/data/specs";

export interface ValidationResponse {
  code: string
  field: string
  message: string
}

export interface ApiErrorResponse {
  code: string
  message: string
  params: object
  validation: ValidationResponse[]
}

export interface ErrorResponse {
  status: number
  error: ApiErrorResponse
}

export interface FormValidation {
  [fieldName: string]: string[]
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
