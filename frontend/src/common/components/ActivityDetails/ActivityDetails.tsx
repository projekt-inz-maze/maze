import React, { useEffect, useState } from 'react'

import { Button, Modal } from 'react-bootstrap'
import { connect } from 'react-redux'

import styles from './ActivityDetails.module.scss'
import ExpeditionService from '../../../services/expedition.service'

type ActivityDetailsProps = {
  activityId: number
  showDetails: boolean
  onCloseDetails: () => void
  onStartActivity: () => void
  isActivityCompleted: boolean
  name: string
  type: string
  startDate: string
  endDate: string
  description: string
  numberOfAttempts: number
  maxNumberOfAttempts: number
  timeLimit: number
  points: number
}

const ActivityDetails = (props: ActivityDetailsProps) => {
  const [activityScore, setActivityScore] = useState<number>(0)

  useEffect(() => {
    if (props.type === 'EXPEDITION') {
      // TODO: Fix this after fetching results from different activities is ready
      ExpeditionService.getExpeditionScore(props.activityId)
        .then((response) => {
          setActivityScore(response)
        })
        .catch(() => {
          setActivityScore(0)
        })
    }
  }, [props.activityId])

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
          <Modal.Title className={styles.modalTitle}>Szczegóły aktywności</Modal.Title>
          <button type='button' className={styles.customButtonClose} onClick={props.onCloseDetails}>
            {/* Close button content */}
            <span>&times;</span>
          </button>
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
                <span>Data otwarcia:</span> <br />
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
              {/* <div className={styles.firstRow}> */}
              <div className={styles.imgSection}>
                {/* <img src='/icons/Thriller.png' alt='Hazard icon' /> */}
                {/* <div> */}
                {/*  <span>Hazard</span> */}
                {/*  <p className={`${!props.isWager ?? styles.hazardText}`}>{props.isWager ? 'Tak' : 'Nie'}</p> */}
                {/* </div> */}
              </div>
              <div className={styles.imgSection}>
                <img src='/icons/Hashtag.png' alt='Hashtag icon' />
                <div>
                  <span>Liczba podejść</span>
                  <p>
                    {props.numberOfAttempts}/{props.maxNumberOfAttempts}
                  </p>
                </div>
              </div>
              {/* </div> */}
              {/* <div className={styles.secondRow}> */}
              <div className={styles.imgSection}>
                <img src='/icons/Time.png' alt='Time icon' />
                <div>
                  <span>Czas na rozwiązanie</span>
                  <p>{props.timeLimit ? `${props.timeLimit} minut` : 'brak limitu czasowego'}</p>
                </div>
              </div>
              <div className={styles.imgSection}>
                <img src='/icons/Star.png' alt='Star icon' />
                <div>
                  <span>Liczba punktów do zdobycia</span>
                  <p>{props.points ?? 'zadanie jest niepunktowane'}</p>
                </div>
              </div>
              {/* </div> */}
            </div>
            <div className={activityScore > 0 ? styles.resultFieldCompleted : styles.resultField}>
              <span>Twój wynik:</span>
              <p>{activityScore ? `${activityScore}/${props.points}` : 'nie ukończono'}</p>
            </div>
          </div>
        </Modal.Body>
        <Modal.Footer className={styles.modalFooter}>
          {props.isActivityCompleted ? (
            <Button variant='primary' onClick={props.onCloseDetails} className={styles.startActivityButton}>
              Powrót do mapy
            </Button>
          ) : (
            <Button
              variant='primary'
              onClick={props.onStartActivity}
              className={`${styles.startActivityButton} ${
                props.maxNumberOfAttempts - props.numberOfAttempts ? '' : 'disabled'
              }`}
            >
              Rozpocznij aktywność
            </Button>
          )}
        </Modal.Footer>
      </Modal>
    </>
  )
}

function mapStateToProps(state: { theme: any }) {
  const { theme } = state

  return { theme }
}
export default connect(mapStateToProps)(ActivityDetails)
