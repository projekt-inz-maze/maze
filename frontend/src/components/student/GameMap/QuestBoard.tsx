import React from 'react'

import { Col, Container, Row } from 'react-bootstrap'

const QuestBoard = () => {
  const placeholder = 'QuestBoard placeholder'

  return (
    <Container fluid>
      <Row>
        <Col xs={12} md={3} className='p-0'>
          <img
            src='/map/questboard.png'
            alt='Image 1'
            className='img-fluid'
            style={{ position: 'absolute', width: '100%', height: 'auto' }}
          />
        </Col>
      </Row>
    </Container>
  )
}

export default QuestBoard
