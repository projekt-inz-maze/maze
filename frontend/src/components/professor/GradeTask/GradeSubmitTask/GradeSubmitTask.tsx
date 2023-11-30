import React from 'react'

import { Button, Modal } from 'react-bootstrap'

import styles from './GradeSubmitTask.module.scss'
import { useGradeSubmitTaskMutation } from '../../../../api/apiGrades'
import { ActivityResponseInfo } from '../../../../api/types'
import { Activity, getActivityTypeName } from '../../../../utils/constants'
import ActivityAssessmentStudentFileService from '../../ActivityAssessmentDetails/ActivityAssessmentStudentFileService'

type GradeSubmitTaskProps = {
  showDetails: boolean
  onCloseDetails: () => void
  activity: ActivityResponseInfo
}

const GradeSubmitTask = (props: GradeSubmitTaskProps) => {
  const [gradeSubmitTask] = useGradeSubmitTaskMutation()

  const handleSubmit = async (accepted: boolean) => {
    await gradeSubmitTask({ id: props.activity.fileTaskResponseId, accepted })
    props.onCloseDetails()
  }

  return (
    <>
      {props.activity ? (
        <Modal
          fullscreen
          show={props.showDetails}
          onHide={props.onCloseDetails}
          size='xl'
          className={styles.modalContainer}
          centered
        >
          <Modal.Header className={styles.modalHeader}>
            <Modal.Title className={styles.modalTitle}>{`${getActivityTypeName(Activity.SUBMIT)} - ${
              props.activity.activityName
            }`}</Modal.Title>
            <button
              type='button'
              className={styles.customButtonClose}
              onClick={() => {
                props.onCloseDetails()
              }}
            >
              {/* Close button content */}
              <span>&times;</span>
            </button>
          </Modal.Header>
          <Modal.Body>
            <div className={styles.modalInfoSection}>
              <div>
                <span>Autor: </span>
                {`${props.activity.firstName} ${props.activity.lastName}`}
              </div>
              <span>{props.activity.isLate ? 'Zadanie spóźnione' : 'Zadanie oddane w terminie'}</span>
            </div>
            <div className={styles.modalTaskDescription}>
              <p>{`${props.activity.userTitle} - ${props.activity.userContent}`}</p>
              <ActivityAssessmentStudentFileService activity={props.activity} />
            </div>
          </Modal.Body>
          <Modal.Footer className={styles.modalFooter}>
            <Button
              variant='secondary'
              type='submit'
              className={styles.rejectButton}
              onClick={() => handleSubmit(false)}
            >
              <span>Odrzuć</span>
            </Button>
            <Button variant='primary' type='submit' className={styles.acceptButton} onClick={() => handleSubmit(true)}>
              <span>Stwórz zadanie</span>
            </Button>
          </Modal.Footer>
        </Modal>
      ) : (
        <></>
      )}
    </>
  )
}

export default GradeSubmitTask
