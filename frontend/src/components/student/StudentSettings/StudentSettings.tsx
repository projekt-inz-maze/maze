import React, { useState } from 'react'

import { Button, Container, ListGroup, Modal, Row } from 'react-bootstrap'

import styles from './StudentSettings.module.scss'
import PersonalityQuiz from '../PersonalityQuiz/PersonalityQuiz'

const StudentSettings = () => {
  const [showTest, setShowTest] = useState<boolean>(false)
  const placeholder = 'brak'

  return (
    <>
      <Container fluid className={styles.container}>
        <Row>
          <p className={styles.title}>Ustawienia</p>
        </Row>
        <div className={styles.items}>
          <p>{`Typ osobowości: ${placeholder}`}</p>
          <Button className={styles.testButton} onClick={() => setShowTest(true)}>
            Wypełnij test!
          </Button>
        </div>
      </Container>
      <PersonalityQuiz showModal={showTest} setShowModal={setShowTest} />
    </>
  )
}

export default StudentSettings
