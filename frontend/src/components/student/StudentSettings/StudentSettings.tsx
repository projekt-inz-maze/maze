import React, { useEffect, useState } from 'react'

import { Button, Container, Row } from 'react-bootstrap'

import styles from './StudentSettings.module.scss'
import { useGetPersonalityQuery } from '../../../api/apiPersonality'
import { QuizResults } from '../../../api/types'
import { getMaxValuePersonality } from '../../../utils/formatters'
import PersonalityQuiz from '../PersonalityQuiz/PersonalityQuiz'

const emptyQuizResults: QuizResults = {
  SOCIALIZER: 0,
  ACHIEVER: 0,
  KILLER: 0,
  EXPLORER: 0
}

const StudentSettings = () => {
  const [showTest, setShowTest] = useState<boolean>(false)
  const [personality, setPersonality] = useState<QuizResults>(emptyQuizResults)
  const { data: studentPersonality, isSuccess } = useGetPersonalityQuery()

  useEffect(() => {
    if (!isSuccess) {
      return
    }
    setPersonality(studentPersonality)
  }, [studentPersonality, isSuccess, personality])

  return (
    <>
      <Container fluid className={styles.container}>
        <Row>
          <p className={styles.title}>Ustawienia</p>
        </Row>
        <div className={styles.items}>
          <p>
            <span>Typ osobowości: </span>
            {`${
              personality?.ACHIEVER || personality?.EXPLORER || personality?.KILLER || personality?.SOCIALIZER
                ? getMaxValuePersonality(personality)
                : 'nie wypełniono testu'
            }`}
          </p>
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
