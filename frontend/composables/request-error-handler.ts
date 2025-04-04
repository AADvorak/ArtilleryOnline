import type {ErrorResponse, FormValidation, ValidationResponse} from "~/data/response";
import {useRouter} from "#app";
import {useErrorsStore} from "~/stores/errors";

export function useRequestErrorHandler() {

  const router = useRouter()
  const errorsStore = useErrorsStore()

  function handle(errorResponse: ErrorResponse, formValidation?: FormValidation) {
    if (errorResponse.status === 401) {
      router.push('/login').then()
      return
    }
    if (errorResponse.status === 400 && errorResponse.error.validation && formValidation) {
      parseValidation(errorResponse.error.validation, formValidation)
      return
    }
    errorsStore.errors.push(errorResponse)
  }

  function parseValidation(validation: ValidationResponse[], formValidation: FormValidation) {
    for (const item of validation) {
      if (formValidation[item.field]) {
        formValidation[item.field].push({
          message: item.message,
          locale: item.locale
        })
      }
    }
  }

  return { handle }
}
