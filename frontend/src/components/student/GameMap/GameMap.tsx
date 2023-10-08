import React from 'react'

import { Container, Row, Col, Button } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'

import styles from './GameMap.module.scss'
import ActivityDetails from '../../../common/components/ActivityDetails/ActivityDetails'

const GameMap = () => {
  const navigate = useNavigate()

  return (
    <Container fluid style={{ height: '100%' }}>
      <ActivityDetails
        name='Jungla kabli'
        type='Ekspedycja'
        startDate='20:00, 10.01.2024'
        endDate='22:00, 12.01.2024'
        description='Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et
          dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex
          ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu
          fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt
          mollit anim id est laborum.'
        isHazard={false}
        numberOfAttempts={0}
        maxNumberOfAttempts={1}
        timeLimit={60}
        points={100}
        result={0}
      />
      <Row style={{ height: '100%' }}>
        <Col xs={12} md={9} className='p-0 w-100 h-100'>
          <div className='image-container' style={{ width: '100%', height: '100%', position: 'relative' }}>
            <img
              src='/map/forest.png'
              alt='Chapter image'
              className='img-fluid'
              style={{ width: '100%', height: '100%' }}
            />
            <img src='/map/map-cut.png' alt='Course legend' className={`img-fluid ${styles.legend}`} />
            <Button onClick={() => navigate('/map/quests')} className={styles.questBoardButton} />
            {/* <img src='/map/map-btn.png' alt='To quests' className={styles.questBoardButton} /> */}
          </div>
        </Col>
      </Row>
    </Container>
  )
}

export default GameMap
