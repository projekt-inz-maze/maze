import React from 'react'

import { Button, Modal } from 'react-bootstrap'
import { connect } from 'react-redux'

import styles from './Auction.module.scss'

type AuctionProps = {
  showDetails: boolean
  onCloseDetails: () => void
  endDate: string
  points: number
}

const Auction = (props: AuctionProps) => (
  <>
    <Modal show={props.showDetails} onHide={props.onCloseDetails} size='xl' className={styles.modalContainer} centered>
      <p style={{ color: 'red', textAlign: 'center', padding: '1em' }}>
        Mockowy widok aktywności - w trakcie implementacji
      </p>
      <Modal.Header className={styles.modalHeader}>
        <Modal.Title className={styles.modalTitle}>
          <img src='/icons/Coins.png' alt='Coins icon' style={{ width: '35px', height: '35px', marginRight: '10px' }} />
          Licytacja
        </Modal.Title>
        <div className={styles.dateInfo}>
          <img src='/icons/Finish.png' alt='Finish icon' style={{ width: '30px', height: '30px' }} />
          <p>{props.endDate}</p>
        </div>
        <button type='button' className={styles.customButtonClose} onClick={props.onCloseDetails}>
          {/* Close button content */}
          <span>&times;</span>
        </button>
      </Modal.Header>
      <Modal.Body>
        <div className={styles.modalTaskDescription}>
          Ta aktywność to szansa na zdobycie dużej liczby punktów. Polega to na obstawianiu pewnej liczby zdobytych w
          innych aktywnościach przez Ciebie punktów. Z Tobą licytują się pozostali uczestnicy kursu. Jeśli w momencie
          zakończenia licytacji będziesz osobą z najwyższym zakłedem, to otrzymasz możliwość rozwiązania aktywności.
          Jeśli rozwiążesz ją poprawnie, odzyskasz zastawione punkty i zdobędziesz punkty dodatkowe. Jeśli rozwiążesz ją
          niepoprawnie, stracisz zastawione punkty. Graj ostrożnie!
        </div>
        <div className={styles.modalTaskRequirements}>
          <div className={styles.imgSection}>
            <img src='/icons/Star.png' alt='Star icon' />
            <div>
              <span>Punkty do zdobycia</span>
              <p>{props.points ?? 'zadanie jest niepunktowane'}</p>
            </div>
          </div>
          <div className={styles.imgSection}>
            <img src='/icons/Speed.png' alt='Speed icon' />
            <div>
              <span>Minimalny zakład</span>
              <p>TODO</p>
            </div>
          </div>
          <div className={styles.imgSection}>
            <img src='/icons/1st.png' alt='1st place icon' />
            <div>
              <span>Najwyższy zakład</span>
              <p>TODO</p>
            </div>
          </div>
          <div className={styles.imgSection}>
            <img src='/icons/Coins.png' alt='Coins icon' />
            <div>
              <span>Twój zakład</span>
              <p>TODO</p>
            </div>
          </div>
        </div>
      </Modal.Body>
      <Modal.Footer className={styles.modalFooter}>
        <label htmlFor='bidding'>
          <span>Punkty, które zastawiasz:</span>
          <input id='bidding' name='bidding' type='number' min='1' max='100' />
        </label>
        <Button disabled variant='primary' className={styles.bidButton}>
          Zastaw punkty
        </Button>
      </Modal.Footer>
    </Modal>
  </>
)

function mapStateToProps(state: { theme: any }) {
  const { theme } = state

  return { theme }
}
export default connect(mapStateToProps)(Auction)
