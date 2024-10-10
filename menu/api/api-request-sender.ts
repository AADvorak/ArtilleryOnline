export class ApiRequestSender {

  BASE_PATH = '/api'

  async getJson<Response>(path: string): Promise<Response> {
    const response = await fetch(this.BASE_PATH + path)
    return this.processResponseJson(response)
  }

  async putJson<Request, Response>(path: string, body: Request): Promise<Response> {
    const response = await fetch(this.BASE_PATH + path, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(body ? body : {})
    })
    return this.processResponseJson(response)
  }

  async postJson<Request, Response>(path: string, body: Request): Promise<Response> {
    const response = await fetch(this.BASE_PATH + path, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(body)
    })
    return this.processResponseJson(response)
  }

  async delete(path: string): Promise<void> {
    return fetch(this.BASE_PATH + path, {
      method: 'DELETE'
    }).then()
  }

  async processResponseJson(response: Response) {
    const isJson = response.headers.get('content-type')?.includes('application/json')
    const json = isJson ? await response.json() : null
    if (response.ok) {
      return json
    }
    throw {
      status: response.status,
      error: json
    }
  }
}
