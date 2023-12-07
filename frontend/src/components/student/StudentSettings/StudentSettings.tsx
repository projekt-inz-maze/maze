import React, { useEffect, useState } from 'react'

import { Button, Container, Row } from 'react-bootstrap'

import styles from './StudentSettings.module.scss'
import { useGetPersonalityQuery } from '../../../api/apiPersonality'
import { QuizResults } from '../../../api/types'
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

  const getMaxValuePersonality = () => {
    const values = [
      { name: 'Zabójca', type: personality?.KILLER },
      { name: 'Odkrywca', type: personality?.EXPLORER },
      { name: 'Zdobywca', type: personality?.ACHIEVER },
      { name: 'Towarzyski / Społecznościowiec', type: personality?.SOCIALIZER }
    ]

    const maxEntry = values.reduce((max, current) => ((current.type ?? 0) > (max.type ?? 0) ? current : max), {
      name: '',
      type: -Infinity
    })

    return maxEntry?.name
  }

  return (
    <>
      <Container fluid className={styles.container}>
        <Row>
          <p className={styles.title}>Ustawienia</p>
        </Row>
        <div className={styles.items}>
          <p>{`Typ osobowości: ${
            personality?.ACHIEVER || personality?.EXPLORER || personality?.KILLER || personality?.SOCIALIZER
              ? getMaxValuePersonality()
              : 'nie wypełniono testu'
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
