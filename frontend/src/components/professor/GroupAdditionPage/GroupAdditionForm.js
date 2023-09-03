import React from 'react'

import { Formik } from 'formik'
import { Button, Col, Container, Form, Row, Spinner } from 'react-bootstrap'
import { connect } from 'react-redux'

import { useAppSelector } from '../../../hooks/hooks'
import GroupService from '../../../services/group.service'
import { FIELD_REQUIRED } from '../../../utils/constants'
import { FormCol } from '../../general/LoginAndRegistrationPage/FormCol'


function GroupAdditionForm(props) {
  const courseId = useAppSelector((state) => state.user.courseId)

  return (
    // todo: think about general Form component that can be extended
    <Formik
      initialValues={{
        name: '',
        code: ''
      }}
      validate={(values) => {
        const errors = {}
        if (!values.name) errors.name = FIELD_REQUIRED
        if (!values.code) errors.code = FIELD_REQUIRED
        return errors
      }}
      onSubmit={(values, { setSubmitting }) => {
        GroupService.addGroup({ groupName: values.name, groupKey: values.code, courseId }).then(() => {
          props.refreshFunction()
          props.setModalOpen(false)
        })

        setSubmitting(false)
      }}
    >
      {({ isSubmitting, values, errors, handleSubmit }) => (
        <Form onSubmit={handleSubmit}>
          <Container>
            <Row
              className='mx-auto'
              style={{
                width: '70%'
              }}
            >
              {FormCol('Nazwa grupy', 'text', 'name', 12, { errorColor: props.theme.danger })}
              {FormCol('Kod grupy', 'text', 'code', 12, { errorColor: props.theme.danger })}
            </Row>
            <Row className='mt-4 d-flex justify-content-center'>
              <Col sm={12} className='d-flex justify-content-center mb-2'>
                <Button
                  style={{ backgroundColor: props.theme.danger, borderColor: props.theme.danger }}
                  className='me-3'
                  onClick={() => props.setModalOpen(false)}
                >
                  Anuluj
                </Button>
                <Button
                  style={{ backgroundColor: props.theme.success, border: 'none' }}
                  type='submit'
                  disabled={isSubmitting}
                >
                  {isSubmitting ? <Spinner as='span' animation='border' size='sm' role='status' /> : <span>Dodaj</span>}
                </Button>
              </Col>
            </Row>
          </Container>
        </Form>
      )}
    </Formik>
  )
}

function mapStateToProps(state) {
  const {theme} = state
  return {
    theme
  }
}
export default connect(mapStateToProps)(GroupAdditionForm)
