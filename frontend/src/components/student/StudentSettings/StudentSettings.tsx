import React, { useState } from 'react'

import { Button, Container, ListGroup, Modal, Row } from 'react-bootstrap'

import styles from './StudentSettings.module.scss'

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
      <Modal
        fullscreen
        show={showTest}
        onHide={() => setShowTest(false)}
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
              setShowTest(false)
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
    </>
  )
}

export default StudentSettings
