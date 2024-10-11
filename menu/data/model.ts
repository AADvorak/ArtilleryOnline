export interface User {
  id: number
  email: string
  nickname: string
  token: string
}

export interface CsrfToken {
  headerName: string
  token: string
}
