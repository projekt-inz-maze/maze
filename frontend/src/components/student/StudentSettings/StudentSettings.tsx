import React, { useEffect, useState } from 'react'

import { Button, Container, Dropdown, Row } from 'react-bootstrap'

import styles from './StudentSettings.module.scss'
import { useGetPersonalityQuery, useSendPersonalityQuizResultsMutation } from '../../../api/apiPersonality'
import { PersonalityType, QuizResults } from '../../../api/types'
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
  const [setNewPersonality] = useSendPersonalityQuizResultsMutation()

  useEffect(() => {
    if (!isSuccess) {
      return
    }
    setPersonality(studentPersonality)
  }, [studentPersonality, isSuccess, personality])

  const handleChangeType = async (type: PersonalityType) => {
    const newPersonality: QuizResults = {
      SOCIALIZER: 0,
      KILLER: 0,
      ACHIEVER: 0,
      EXPLORER: 0
    }

    newPersonality[type] = 1

    await setNewPersonality(newPersonality)
  }

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
          <div className={styles.testButtonsContainer}>
            <Dropdown>
              <Dropdown.Toggle variant='success' id='dropdown-basic' className={styles.testDropdown}>
                Zmień typ
              </Dropdown.Toggle>

              <Dropdown.Menu>
                <Dropdown.Item href='#/action-1' onClick={() => handleChangeType('KILLER')}>
                  Zabójca
                </Dropdown.Item>
                <Dropdown.Item href='#/action-2' onClick={() => handleChangeType('EXPLORER')}>
                  Odkrywca
                </Dropdown.Item>
                <Dropdown.Item href='#/action-3' onClick={() => handleChangeType('ACHIEVER')}>
                  Zdobywca
                </Dropdown.Item>
                <Dropdown.Item href='#/action-4' onClick={() => handleChangeType('SOCIALIZER')}>
                  Towarzyski
                </Dropdown.Item>
              </Dropdown.Menu>
            </Dropdown>
            <Button className={styles.testButton} onClick={() => setShowTest(true)}>
              Wypełnij test!
            </Button>
          </div>
        </div>
      </Container>
      <PersonalityQuiz showModal={showTest} setShowModal={setShowTest} />
    </>
  )
}

export default StudentSettings
