import React from 'react'

import { faFire } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { Col } from 'react-bootstrap'
import Row from 'react-bootstrap/Row'

import styles from './CourseNav.module.scss'

const CourseNav = (): JSX.Element => (
    <Row className={styles.topRow}>
      <Col xs={1} className='d-flex'>
        <FontAwesomeIcon icon={faFire} size='3x' />
        <span className={styles.appTitle}>Maze</span>
      </Col>
      <Col xs={10}>
        <div className={styles.links}>
          <button type="button" className={styles.actionButton}>+ Dodaj kurs</button>
          <button type="button">Ustawienia</button>
          <button type="button">Wyloguj</button>
        </div>
      </Col>
    </Row>
  )

export default CourseNav
