import React from 'react'

import { faFire } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { Row } from 'react-bootstrap'
import { connect } from 'react-redux'

import { AuthFormContainer, LoginContainer, Logo } from './AuthStyle'
import AuthTabs from './AuthTabs'
import Carousel from './CharactersCarousel/Carousel'

function LoginAndRegistration(props) {
  return (
    <LoginContainer>
      <Row className='w-100 h-100 align-items-center m-0'>
        <Carousel />
        <AuthFormContainer $background={props.theme.primary} md={6} className='p-0'>
          <Logo $logoColor={props.theme.font}>
            <FontAwesomeIcon icon={faFire} />
            <br />
            Maze
          </Logo>
          <AuthTabs />
        </AuthFormContainer>
      </Row>
    </LoginContainer>
  )
}

function mapStateToProps(state) {
  const { theme } = state

  return { theme }
}
export default connect(mapStateToProps)(LoginAndRegistration)
