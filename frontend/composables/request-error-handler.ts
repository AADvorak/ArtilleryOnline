import type {ErrorResponse, FormValidation, ValidationResponse} from "~/data/response";

export function useRequestErrorHandler() {

  function handle(errorResponse: ErrorResponse, formValidation: FormValidation) {
    console.log(errorResponse)
    if (errorResponse.status === 400 && errorResponse.error.validation) {
      parseValidation(errorResponse.error.validation, formValidation)
    }
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
