import React, { useEffect, useState } from 'react'

import { Button, Modal } from 'react-bootstrap'

import styles from './PersonalityQuiz.module.scss'
import PersonalityQuizContent from './PersonalityQuizContent/PersonalityQuizContent'
import PersonalityQuizIntro from './PersonalityQuizIntro/PersonalityQuizIntro'
import { useGetPersonalityQuizQuery } from '../../../api/apiPersonality'
import { QuizQuestion } from '../../../api/types'

type PersonalityQuizProps = {
  showModal: boolean
  setShowModal: (arg: boolean) => void
}

const PersonalityQuiz = (props: PersonalityQuizProps) => {
  const [quiz, setQuiz] = useState<QuizQuestion[]>([])
  const [showQuestions, setShowQuestions] = useState<boolean>(false)

  const { data: questions, isSuccess } = useGetPersonalityQuizQuery()

  useEffect(() => {
    if (!isSuccess) {
      return
    }
    setQuiz(questions)
  }, [questions, isSuccess])

  const handleTestProceed = () => {
    setShowQuestions(!showQuestions)
  }

  return (
    <Modal
      show={props.showModal}
      onHide={() => props.setShowModal(false)}
      size='xl'
      className={styles.modalContainer}
      centered
      fullscreen
    >
      <Modal.Header className={styles.modalHeader}>
        <Modal.Title className={styles.modalTitle}>Test osobowości</Modal.Title>
        <button
          type='button'
          className={styles.customButtonClose}
          onClick={() => {
            props.setShowModal(false)
          }}
        >
          {/* Close button content */}
          <span>&times;</span>
        </button>
      </Modal.Header>
      <Modal.Body>
        {showQuestions ? (
          <PersonalityQuizContent quiz={quiz} setShowQuestions={() => props.setShowModal(false)} />
        ) : (
          <PersonalityQuizIntro />
        )}
      </Modal.Body>
      <Modal.Footer className={styles.modalFooter}>
        {!showQuestions && (
          <>
            <p>
              Pamiętaj, że wynik testu nie jest ostateczny. Jeśli uznasz, że bliżej Ci do innego typu gracza, możesz go
              zmienić w Ustawieniach.
            </p>
            <Button variant='primary' type='submit' className={styles.acceptButton} onClick={() => handleTestProceed()}>
              <span>Przejdź do testu</span>
            </Button>
          </>
        )}
      </Modal.Footer>
    </Modal>
  )
}

export default PersonalityQuiz
