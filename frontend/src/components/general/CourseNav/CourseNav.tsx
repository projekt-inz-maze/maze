import React from 'react'

import { faFire } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { Col } from 'react-bootstrap'
import Row from 'react-bootstrap/Row'
import { connect } from 'react-redux'
import { useNavigate } from 'react-router-dom'

import styles from './CourseNav.module.scss'
import { logout } from '../../../actions/auth'

const CourseNav = (props: any): JSX.Element => {
  const navigate = useNavigate()
  const logOut = () => props.dispatch(logout(navigate))

  return (
    <Row className={styles.topRow}>
      <Col xs={1} className='d-flex'>
        <FontAwesomeIcon icon={faFire} size='3x' />
        <span className={styles.appTitle}>Maze</span>
      </Col>
      <Col xs={10}>
        <div className={styles.links}>
          <button type='button' className={styles.actionButton}>
            + Dodaj kurs
          </button>
          <button type='button'>Ustawienia</button>
          <button type='button' onClick={logOut}>
            Wyloguj
          </button>
        </div>
      </Col>
    </Row>
  )
}

function mapStateToProps(state: any) {
  const { user } = state.auth
  return { user }
}

export default connect(mapStateToProps)(CourseNav)