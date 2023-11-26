import React from 'react'

import { Button, Col, Form, Modal, Row } from 'react-bootstrap'

import styles from './GradeFileTaskModal.module.scss'
import { ActivityResponseInfo } from '../../../../api/types'
import ProfessorService from '../../../../services/professor.service'
import { Activity, getActivityTypeName } from '../../../../utils/constants'
import { ActivityAssessmentProfessorFileService } from '../../ActivityAssessmentDetails/ActivityAssessmentProfessorFileService'
import ActivityAssessmentStudentFileService from '../../ActivityAssessmentDetails/ActivityAssessmentStudentFileService'

type GradeFileTaskModalProps = {
  showDetails: boolean
  onCloseDetails: () => void
  activity: ActivityResponseInfo
}

const GradeFileTaskModal = (props: GradeFileTaskModalProps) => {
  const [gradeValue, setGradeValue] = React.useState<number>(0)
  const [fileBlob, setFileBlob] = React.useState<Blob | null>(null)
  const [fileName, setFileName] = React.useState<string>('')
  const [remarks, setRemarks] = React.useState<string>('')

  const fileRef = React.useRef<HTMLInputElement>(null)

  const handleSubmit = () => {
    ProfessorService.sendTaskEvaluation(
      props.activity.fileTaskResponseId,
      remarks,
      gradeValue,
      fileBlob,
      fileName
    ).then(() => {
      setTimeout(() => props.onCloseDetails(), 500)
    })

  }

  return (
    <>
      <Modal
        fullscreen
        show={props.showDetails}
        onHide={props.onCloseDetails}
        size='xl'
        className={styles.modalContainer}
        centered
      >
        <Modal.Header className={styles.modalHeader}>
          <Modal.Title className={styles.modalTitle}>{`${getActivityTypeName(Activity.TASK)} - ${
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
            <span>Treść zadania:</span>
            <p>{props.activity.activityDetails}</p>
            <span>Odpowiedź:</span>
            <p>{props.activity.userAnswer}</p>
            <ActivityAssessmentStudentFileService activity={props.activity} />
          </div>
          <Form className={styles.formContainer}>
            <Row>
              <Form.Group className='mb-3' controlId='exampleForm.ControlTextarea1'>
                <Form.Label>
                  <span>Uwagi do zadania</span>
                </Form.Label>
                <Form.Control as='textarea' rows={3} required onChange={(event) => setRemarks(event.target.value)} />
              </Form.Group>
            </Row>
            <Row className={styles.form}>
              <Col xs={3}>
                <ActivityAssessmentProfessorFileService
                  setFile={setFileBlob}
                  setFileName={setFileName}
                  fileRef={fileRef}
                />
              </Col>
            </Row>
          </Form>
        </Modal.Body>
        <Modal.Footer className={styles.modalFooter}>
          <label htmlFor='grade'>
            <span>Punkty:</span>
            <input
              id='grade'
              name='grade'
              type='number'
              min={0}
              max={props.activity.maxPoints}
              onChange={(e) => setGradeValue(parseInt(e.target.value, 10))}
            />
            <span>{` / ${props.activity.maxPoints}`}</span>
          </label>
          <Button variant='primary' type='submit' className={styles.gradeButton} onClick={handleSubmit}>
            <span>Prześlij i przejdź dalej</span>
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  )
}

export default GradeFileTaskModal
