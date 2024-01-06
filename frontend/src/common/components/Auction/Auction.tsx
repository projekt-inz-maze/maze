import React, { useState } from 'react'

import { Button, Modal } from 'react-bootstrap'
import { connect } from 'react-redux'

import styles from './Auction.module.scss'
import { useGetAuctionByIdQuery, useSetBidMutation } from '../../../api/apiAuctions'

type AuctionProps = {
  activityId: number
  showDetails: boolean
  onCloseDetails: () => void
  points: number
  endDate: number
}

const Auction = (props: AuctionProps) => {
  const { data: auction } = useGetAuctionByIdQuery(props.activityId)
  const [setBid] = useSetBidMutation()

  const [bidValue, setBidValue] = useState<number>(0)
  const [errorMessage, setErrorMessage] = useState<string>('')
  const date: Date = new Date(auction?.endDateEpochSeconds ? auction?.endDateEpochSeconds * 1000 : 0)

  const handleSetBid = (bid: number) => {
    setErrorMessage('')
    setBid({ auctionId: props.activityId, bidValue: bid })
      .unwrap()
      .then(() => {
        setTimeout(() => props.onCloseDetails(), 500)
      })
      .catch((error) => {
        setErrorMessage(error.data?.message ?? 'Nieznany błąd')
      })
    setBidValue(bid)
  }

  return (
    <>
      <Modal
        show={props.showDetails}
        onHide={props.onCloseDetails}
        size='xl'
        className={styles.modalContainer}
        centered
      >
        <Modal.Header className={styles.modalHeader}>
          <Modal.Title className={styles.modalTitle}>
            <img
              src='/icons/Coins.png'
              alt='Coins icon'
              style={{ width: '35px', height: '35px', marginRight: '10px' }}
            />
            Licytacja
          </Modal.Title>
          <div className={styles.dateInfo}>
            <img src='/icons/Finish.png' alt='Finish icon' style={{ width: '30px', height: '30px' }} />
            <p>
              {auction?.endDateEpochSeconds !== 0
                ? date.toLocaleString('en-GB')
                : props.endDate !== 0
                ? new Date(props.endDate).toLocaleString('en-GB')
                : 'nie określono'}
            </p>
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
            Jeśli rozwiążesz ją poprawnie, odzyskasz zastawione punkty i zdobędziesz punkty dodatkowe. Jeśli rozwiążesz
            ją niepoprawnie, stracisz zastawione punkty. Graj ostrożnie!
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
                <p>{auction?.lowestAllowedBid ?? 0}</p>
              </div>
            </div>
            <div className={styles.imgSection}>
              <img src='/icons/1st.png' alt='1st place icon' />
              <div>
                <span>Najwyższy zakład</span>
                <p>{auction?.highestAllowedBid ?? 'brak limitu'}</p>
              </div>
            </div>
            <div className={styles.imgSection}>
              <img src='/icons/Coins.png' alt='Coins icon' />
              <div>
                <span>Twój zakład</span>
                <p>{auction?.userBid ?? '0'}</p>
              </div>
            </div>
          </div>
        </Modal.Body>
        <Modal.Footer className={styles.modalFooter}>
          <label htmlFor='bidding'>
            <span>Punkty, które zastawiasz:</span>
            <input
              id='bidding'
              name='bidding'
              type='number'
              min={auction?.lowestAllowedBid ?? 0}
              max={auction?.highestAllowedBid ?? 1000}
              onChange={(e) => setBidValue(parseInt(e.target.value, 10))}
            />
          </label>
          <Button variant='primary' className={styles.bidButton} onClick={() => handleSetBid(bidValue)}>
            Zastaw punkty
          </Button>
        </Modal.Footer>
        {errorMessage && <p className={styles.error}>Wystąpił błąd: {errorMessage}</p>}
      </Modal>
    </>
  )
}
function mapStateToProps(state: { theme: any }) {
  const { theme } = state
  return { theme }
}
export default connect(mapStateToProps)(Auction)
