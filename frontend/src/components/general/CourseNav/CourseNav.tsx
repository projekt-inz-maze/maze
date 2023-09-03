import React, { useState } from 'react'

import { faFire } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { Col } from 'react-bootstrap'
import Row from 'react-bootstrap/Row'
import { connect } from 'react-redux'
import { useNavigate } from 'react-router-dom'

import styles from './CourseNav.module.scss'
import { logout } from '../../../actions/auth'
import { AddCourseRequest } from '../../../api/types'
import CustomModal from '../../../common/components/CustomModal/CustomModal'
import { joinGroupRequest } from '../../../services/types/serviceTypes'
import { Role } from '../../../utils/userRole'

type CourseNavProps = {
  dispatch: any
  userRole: number
  onAddCourse: (props: AddCourseRequest) => void
  onJoinCourse: (props: joinGroupRequest) => void
}

const CourseNav = (props: CourseNavProps): JSX.Element => {
  const navigate = useNavigate()
  const [showModal, setShowModal] = useState(false)
  const logOut = () => props.dispatch(logout(navigate))

  const handleModalDisplay = () => {
    setShowModal(!showModal)
  }

  return (
    <Row className={styles.topRow}>
      <Col xs={1} className='d-flex'>
        <FontAwesomeIcon icon={faFire} size='3x' />
        <span className={styles.appTitle}>Maze</span>
      </Col>
      <Col xs={10}>
        <div className={styles.links}>
          <button type='button' className={styles.actionButton} onClick={handleModalDisplay}>
            {props.userRole === Role.LOGGED_IN_AS_STUDENT ? 'Dołącz do kursu' : '+ Dodaj kurs'}
          </button>
          <button type='button'>Ustawienia</button>
          <button type='button' onClick={logOut}>
            Wyloguj
          </button>
        </div>
      </Col>
      <CustomModal
        userRole={props.userRole}
        isModalVisible={showModal}
        onCloseModal={handleModalDisplay}
        onAddCourse={props.onAddCourse}
        onJoinCourse={props.onJoinCourse}
      />
    </Row>
  )
}

function mapStateToProps(state: any) {
  const { user } = state.auth
  return { user }
}

export default connect(mapStateToProps)(CourseNav)
