import type {ErrorResponse, FormValidation, ValidationResponse} from "~/data/response";
import {useRouter} from "#app";
import {useErrorsStore} from "~/stores/errors";

export function useRequestErrorHandler() {

  const router = useRouter()
  const errorsStore = useErrorsStore()
  const userStore = useUserStore()

  function handle(errorResponse: ErrorResponse, formValidation?: FormValidation) {
    if (errorResponse.status === 401) {
      userStore.user = undefined
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
      formValidation[item.field]?.push({
        message: item.message,
        locale: item.locale
      })
    }
  }

  return { handle }
}
