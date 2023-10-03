import React from 'react'

import { Container, Row, Col, Button } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'

import styles from './GameMap.module.scss'

const GameMap = () => {
  const navigate = useNavigate()

  return (
    <Container fluid style={{ height: '100%' }}>
      <Row style={{ height: '100%' }}>
        <Col xs={12} md={9} className='p-0 w-100 h-100'>
          <div className='image-container' style={{ width: '100%', height: '100%', position: 'relative' }}>
            <img
              src='/map/forest.png'
              alt='Chapter image'
              className='img-fluid'
              style={{ width: '100%', height: '100%' }}
            />
            <img src='/map/map-left.png' alt='Course legend' className={`img-fluid ${styles.legend}`} />
            <Button onClick={() => navigate('/map/quests')} className={styles.questBoardButton}>
              Las Krwiopijcy
            </Button>
          </div>
        </Col>
      </Row>
    </Container>
  )
}

export default GameMap
