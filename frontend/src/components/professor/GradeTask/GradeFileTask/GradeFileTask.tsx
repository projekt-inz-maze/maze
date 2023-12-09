import React, { useState } from 'react'

import { Button, Col, Form, Modal, Row } from 'react-bootstrap'

import styles from './GradeFileTask.module.scss'
import { useGradeTaskMutation } from '../../../../api/apiGrades'
import { ActivityResponseInfo, GradeTaskRequest } from '../../../../api/types'
import { Activity, getActivityTypeName } from '../../../../utils/constants'
import { ProfessorFileService } from '../../ActivityAssessmentDetails/ProfessorFileService'
import ActivityAssessmentStudentFileService from '../../ActivityAssessmentDetails/StudentFileService'

type GradeFileTaskProps = {
  showDetails: boolean
  onCloseDetails: () => void
  activity: ActivityResponseInfo
}

const GradeFileTask = (props: GradeFileTaskProps) => {
  const [gradeValue, setGradeValue] = useState<number>(0)
  const [fileBlob, setFileBlob] = useState<Blob | null>(null)
  const [fileName, setFileName] = useState<string>('')
  const [remarks, setRemarks] = useState<string>('')

  const fileRef = React.useRef<HTMLInputElement>(null)

  const [gradeTask] = useGradeTaskMutation()

  const prepareRequest = (body: GradeTaskRequest): FormData => {
    const formData = new FormData()
    Object.keys(body).forEach((key) => {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      if (body[key]) {
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        formData.append(key, body[key])
      }
    })
    return formData
  }

  const handleSubmit = async () => {
    const requestBody = {
      fileTaskResultId: props.activity.fileTaskResponseId,
      content: remarks,
      points: gradeValue,
      file: fileBlob,
      fileName
    }
    await gradeTask(prepareRequest(requestBody)).then(() => {
      setTimeout(() => props.onCloseDetails(), 200)
      setRemarks('')
      setGradeValue(0)
      setFileName('')
      setFileBlob(null)
    })
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
                  <ProfessorFileService setFile={setFileBlob} setFileName={setFileName} fileRef={fileRef} />
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
              <span>Oceń</span>
            </Button>
          </Modal.Footer>
        </Modal>
      ) : (
        <></>
      )}
    </>
  )
}

export default GradeFileTask
