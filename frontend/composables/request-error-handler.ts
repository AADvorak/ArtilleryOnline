import type {ErrorResponse, FormValidation, ValidationResponse} from "~/data/response";
import {useRouter} from "#app";

export function useRequestErrorHandler() {

  const router = useRouter()

  function handle(errorResponse: ErrorResponse, formValidation: FormValidation) {
    if (errorResponse.status === 401) {
      router.push('/sign-in').then()
      return
    }
    if (errorResponse.status === 400 && errorResponse.error.validation) {
      parseValidation(errorResponse.error.validation, formValidation)
    }
    // todo message
    console.log(errorResponse)
  }

  function parseValidation(validation: ValidationResponse[], formValidation: FormValidation) {
    for (const item of validation) {
      if (formValidation[item.field]) {
        formValidation[item.field].push(item.message)
      }
    }
  }

  return { handle }
}
