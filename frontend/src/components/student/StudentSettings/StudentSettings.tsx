import React, { useEffect, useState } from 'react'

import { Button, Container, Row } from 'react-bootstrap'

import styles from './StudentSettings.module.scss'
import { useGetPersonalityQuery } from '../../../api/apiPersonality'
import { QuizResults } from '../../../api/types'
import PersonalityQuiz from '../PersonalityQuiz/PersonalityQuiz'

const StudentSettings = () => {
  const [showTest, setShowTest] = useState<boolean>(false)
  const [personality, setPersonality] = useState<QuizResults>(null)
  const { data: studentPersonality, isSuccess } = useGetPersonalityQuery()

  useEffect(() => {
    if (!isSuccess) {
      return
    }
    setPersonality(studentPersonality)
  }, [])

  return (
    <>
      <Container fluid className={styles.container}>
        <Row>
          <p className={styles.title}>Ustawienia</p>
        </Row>
        <div className={styles.items}>
          <p>{`Typ osobowości: ${
            personality === null || personality.constructor === Object ? 'nie wypełniono testu' : personality
          }`}</p>
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
