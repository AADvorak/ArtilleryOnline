export class ApiRequestSender {

  BASE_PATH = '/api'

  async getJson<Response>(path: string): Promise<Response> {
    const response = await fetch(this.BASE_PATH + path)
    return this.processResponseJson(response)
  }

  async getBytes(path: string): Promise<ArrayBuffer> {
    const response = await fetch(this.BASE_PATH + path)
    return this.processResponseBinary(response)
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
      body: JSON.stringify(body ? body : {})
    })
    return this.processResponseJson(response)
  }

  async postJsonForBinary<Request>(path: string, body: Request): Promise<ArrayBuffer> {
    const response = await fetch(this.BASE_PATH + path, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(body ? body : {})
    })
    return this.processResponseBinary(response)
  }

  async delete(path: string): Promise<void> {
    return fetch(this.BASE_PATH + path, {
      method: 'DELETE'
    }).then()
  }

  async processResponseJson(response: Response) {
    const json = await this.getJsonFromResponse(response)
    if (response.ok) {
      return json
    }
    throw {
      status: response.status,
      error: json
    }
  }

  async processResponseBinary(response: Response) {
    const arrayBuffer = await this.getBinaryFromResponse(response)
    if (response.ok && arrayBuffer) {
      return arrayBuffer
    }
    const error = await this.getJsonFromResponse(response)
    throw {
      status: response.status,
      error
    }
  }

  async getJsonFromResponse(response: Response) {
    const isJson = response.headers.get('content-type')?.includes('application/json')
    return isJson ? await response.json() : null
  }

  async getBinaryFromResponse(response: Response) {
    const isBinary = response.headers.get('content-type')?.includes('application/octet-stream')
    return isBinary ? await response.arrayBuffer() : null
  }
}
