import type {Validation} from "~/data/response";

export function useValidationLocaleUtil(t: (arg: string) => string) {

  function localize(validation: Validation[]) {
    return validation.map(item => {
      if (item.locale) {
        const localeKey = 'validationMessages.' + item.locale.code
        const localeMsg = t(localeKey)
        return localeMsg === localeKey ? item.message || '' : localeMsg
      } else if (item.message) {
        return item.message
      } else {
        return ''
      }
    })
  }

  return {localize}
}
