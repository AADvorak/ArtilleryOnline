import type {ErrorResponse, FormValidation, FormValues} from "~/data/response";
import type {AsyncFunction} from "type-fest/source/async-return-type";
import {VALIDATION_MSG} from "~/dictionary/validation-msg";
import {useRequestErrorHandler} from "~/composables/request-error-handler";

export interface FormSubmitParams {
  form: FormValues
  validation: FormValidation,
  submitting: Ref<boolean>,
  validator?: Function
}

export function useFormSubmit(params: FormSubmitParams) {

  async function submit(requestSender: AsyncFunction) {
    clearValidation()
    if (preValidateForm()) {
      params.submitting.value = true
      try {
        await requestSender()
      } catch (e) {
        useRequestErrorHandler().handle(e as ErrorResponse, params.validation)
      } finally {
        params.submitting.value = false
      }
    }
  }

  function clearValidation() {
    Object.keys(params.validation).forEach(key => params.validation[key] = [])
  }

  function preValidateForm() {
    let valid = true
    Object.keys(params.form).forEach(key => {
      if (!params.form[key].length) {
        params.validation[key].push({
          locale: {
            code: VALIDATION_MSG.REQUIRED,
            params: {}
          }
        })
        valid = false
      }
    })
    if (params.validator) {
      valid = params.validator(params.form, params.validation)
    }
    return valid
  }

  return {submit}
}
