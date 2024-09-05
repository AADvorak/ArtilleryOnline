export class ApiRequestSender {

  // todo same origin
  BASE_PATH = 'http://localhost:8080/api'

  async getJson<Response>(path: string, userKey: string): Promise<Response> {
    return fetch(this.BASE_PATH + path, {
      headers: {
        UserKey: userKey
      }
    }).then((response) => {
      if (!response.ok) {
        return null
      }
      return response.json()
    })
  }

  async putJson<Request, Response>(path: string, userKey: string, body: Request): Promise<Response> {
    return fetch(this.BASE_PATH + path, {
      method: 'PUT',
      headers: {
        'UserKey': userKey,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(body ? body : {})
    }).then()
  }

  async postJson<Request, Response>(path: string, userKey: string, body: Request): Promise<Response> {
    return fetch(this.BASE_PATH + path, {
      method: 'POST',
      headers: {
        'UserKey': userKey,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(body)
    }).then()
  }
}
