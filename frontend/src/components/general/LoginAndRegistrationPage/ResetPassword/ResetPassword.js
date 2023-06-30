import React from 'react'

import { faFire } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { Col, Container, Row } from 'react-bootstrap'
import { connect } from 'react-redux'

import ResetPasswordForm from './ResetPasswordForm'
import { Logo } from '../AuthStyle'
import Carousel from '../CharactersCarousel/Carousel'

function ResetPassword(props) {
  return (
    <Container fluid className="p-0">
      <Row className='w-100 h-100 align-items-center m-0'>
        <Carousel />
        <Col style={{ backgroundColor: props.theme.primary }} md={6} className="p-0 vh-100">
          <Logo $logoColor={props.theme.font} className="p-5">
            <FontAwesomeIcon icon={faFire} />
            <br />
            Systematic Chaos
          </Logo>

          <ResetPasswordForm />
        </Col>
      </Row>
    </Container>
  )
}

function mapStateToProps(state) {
  const {theme} = state

  return { theme }
}
export default connect(mapStateToProps)(ResetPassword)
