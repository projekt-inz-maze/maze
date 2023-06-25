import { forwardRef, useImperativeHandle } from 'react'

import { useFormikContext } from 'formik'

const FormikContext = forwardRef((props, ref) => {
  const formikContext = useFormikContext()

  const getValue = (valueId) => formikContext.values[valueId]

  const getValues = () => formikContext.values

  useImperativeHandle(ref, () => ({
    getValues,
    getValue
  }))

  return null
})

FormikContext.displayName = 'FormikContext'

export default FormikContext
