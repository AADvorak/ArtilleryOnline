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
