import React from 'react'

import { Button, Modal } from 'react-bootstrap'

import styles from './ActivityDetails.module.scss'

type ActivityDetailsProps = {
  showDetails: boolean
  onCloseDetails: () => void
  name: string
  type: string
  startDate: string
  endDate: string
  description: string
  isHazard: boolean
  numberOfAttempts: number
  maxNumberOfAttempts: number
  timeLimit: number
  points: number
  result: number
}

const ActivityDetails = (props: ActivityDetailsProps) => (
  <>
    <Modal show={props.showDetails} onHide={props.onCloseDetails} size='lg' className={styles.modalContainer}>
      <Modal.Header closeButton className={styles.modalHeader}>
        <Modal.Title className={styles.modalTitle}>Szczegóły aktywności</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <div className={styles.modalInfoSection}>
          <div>
            <p>
              <span>Nazwa:</span> {props.name}
            </p>
            <p>
              <span>Typ:</span> {props.type}
            </p>
          </div>
          <div className={styles.dateInfo}>
            <img
              src='/map/calendar_icon_activity_details.png'
              alt='calendar icon'
              style={{ width: '35px', height: '35px' }}
            />
            <p>
              <span>Data rozpoczęcia:</span> <br />
              {props.startDate}
            </p>
            <p>
              <span>Data zakończenia:</span> <br />
              {props.endDate}
            </p>
          </div>
        </div>
        <div className={styles.modalTaskDescription}>
          <p>Opis aktywności</p>
          <p>{props.description}</p>
        </div>
        <div className={styles.modalTaskRequirements}>
          <div className={styles.mainFlexContainer}>
            <div className={styles.firstRow}>
              <div className={styles.imgSection}>
                <img src='/icons/Thriller.png' alt='Hazard icon' />
                <div>
                  <span>Hazard</span>
                  <p>{props.isHazard ? 'Tak' : 'Nie'}</p>
                </div>
              </div>
              <div className={styles.imgSection}>
                <img src='/icons/Hashtag.png' alt='Hazard icon' />
                <div>
                  <span>Liczba podejść</span>
                  <p>
                    {props.numberOfAttempts}/{props.maxNumberOfAttempts}
                  </p>
                </div>
              </div>
            </div>
            <div className={styles.secondRow}>
              <div className={styles.imgSection}>
                <img src='/icons/Time.png' alt='Hazard icon' />
                <div>
                  <span>Czas na rozwiązanie</span>
                  <p>{props.timeLimit ? `${props.timeLimit} minut` : 'brak limitu czasowego'}</p>
                </div>
              </div>
              <div className={styles.imgSection}>
                <img src='/icons/Star.png' alt='Hazard icon' />
                <div>
                  <span>Liczba punktów do zdobycia</span>
                  <p>{props.points}</p>
                </div>
              </div>
            </div>
          </div>
          <div className={props.result ? styles.resultFieldCompleted : styles.resultField}>
            <span>Twój wynik:</span>
            <p>{props.result ? `${props.result}/${props.points}` : 'nie ukończono'}</p>
          </div>
        </div>
      </Modal.Body>
      <Modal.Footer className={styles.modalFooter}>
        <Button
          variant='primary'
          onClick={props.onCloseDetails}
          className={`${styles.startActivityButton} ${
            props.maxNumberOfAttempts - props.numberOfAttempts ? '' : 'disabled'
          }`}
        >
          Rozpocznij aktywność
        </Button>
      </Modal.Footer>
    </Modal>
  </>
)

export default ActivityDetails
