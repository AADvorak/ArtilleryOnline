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
