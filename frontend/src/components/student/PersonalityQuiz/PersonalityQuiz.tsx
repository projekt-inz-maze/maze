import React, { useEffect, useState } from 'react'

import { Button, Modal } from 'react-bootstrap'

import styles from './PersonalityQuiz.module.scss'
import { useGetPersonalityQuizQuery } from '../../../api/apiPersonality'
import { QuizQuestion } from '../../../api/types'

type PersonalityQuizProps = {
  showModal: boolean
  setShowModal: (arg: boolean) => void
}

const PersonalityQuiz = (props: PersonalityQuizProps) => {
  const [quiz, setQuiz] = useState<QuizQuestion[]>([])

  const { data: questions, isSuccess } = useGetPersonalityQuizQuery()

  useEffect(() => {
    if (!isSuccess) {
      return
    }
    setQuiz(questions)
  }, [questions, isSuccess])

  return (
    <Modal
      fullscreen
      show={props.showModal}
      onHide={() => props.setShowModal(false)}
      size='xl'
      className={styles.modalContainer}
      centered
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
        <div className={styles.modalTaskDescription}>
          <p>
            Aby umilić Ci rozgrywkę, przygotowaliśmy dla Ciebie test osobowości gracza. Po uzupełnieniu testu,
            przydzielimy Cię do jednego z czterech typów graczy wg. taksonomii Bartle&apos;a:
          </p>
          <ol>
            <li>
              <p>
                <span>Zabójców</span> (Killers)
              </p>
              <p>Gracze, którzy największą przyjemność czerpią z pokonywania innych graczy.</p>
            </li>
            <li>
              <p>
                <span>Zdobywców</span> (Achievers)
              </p>
              <p>
                Gracze, którzy największą przyjemność czerpią z osiągania różnych sukcesów (przejście do kolejnego
                poziomu, zdobycie wyższej rangi, znalezienie wszystkich &quot;znajdziek&quot;).
              </p>
            </li>
            <li>
              <p>
                <span>Odkrywców</span> (Explorers)
              </p>
              <p>
                Gracze, którzy lubią odkrywać świat i mechaniki gry - lubią szukać ukrytych przejść, easter eggów oraz
                ograniczeń.
              </p>
            </li>
            <li>
              <p>
                <span>Towarzyskich / Społecznościowców</span> (Socializers)
              </p>
              <p>Gracze, którzy najwięcej przyjemności czerpią z interakcji z innymi graczami.</p>
            </li>
          </ol>
        </div>
      </Modal.Body>
      <Modal.Footer className={styles.modalFooter}>
        <p>
          Pamiętaj, że wynik testu nie jest ostateczny. Jeśli uznasz, że bliżej Ci do innego typu gracza, możesz go
          zmienić w Ustawieniach.
        </p>
        <Button variant='primary' type='submit' className={styles.acceptButton}>
          <span>Przejdź do testu</span>
        </Button>
      </Modal.Footer>
    </Modal>
  )
}

export default PersonalityQuiz
