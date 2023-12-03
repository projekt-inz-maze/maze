import React, { useRef, useState } from 'react'

import { Button, Modal } from 'react-bootstrap'
import JSONInput from 'react-json-editor-ajrm'
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import locale from 'react-json-editor-ajrm/locale/en'

import styles from './GradeSubmitTask.module.scss'
import { useGradeSubmitTaskMutation } from '../../../../api/apiGrades'
import { ActivityResponseInfo } from '../../../../api/types'
import CombatTaskService from '../../../../services/combatTask.service'
import { Activity, getActivityTypeName } from '../../../../utils/constants'
import ActivityAssessmentStudentFileService from '../../ActivityAssessmentDetails/ActivityAssessmentStudentFileService'

type GradeSubmitTaskProps = {
  showDetails: boolean
  onCloseDetails: () => void
  activity: ActivityResponseInfo
}

const GradeSubmitTask = (props: GradeSubmitTaskProps) => {
  const [isAddActivityModalOpen, setIsAddActivityModalOpen] = useState<boolean>(false)

  const [chapterId, setChapterId] = useState<string>('1')
  const [jsonData, setJsonData] = useState<ActivityResponseInfo | undefined>(props.activity)
  const [jsonResult, setJsonResult] = useState<ActivityResponseInfo>(props.activity)
  const jsonRef = useRef<JSONInput>(null)
  const [gradeSubmitTask] = useGradeSubmitTaskMutation()

  const handleSubmit = async (accepted: boolean) => {
    try {
      const response = await gradeSubmitTask({
        id: props.activity.fileTaskResponseId,
        accepted
      })

      // Check if 'data' property exists before accessing it
      if ('data' in response) {
        setJsonData(response.data)
        setJsonResult(response.data)
      }

      setIsAddActivityModalOpen(true)
    } catch (error) {
      console.error('Error submitting task:', error)
    }
    // props.onCloseDetails()
  }

  const handleCreateTask = () => {
    CombatTaskService.setFileTaskJson({ chapterId, form: jsonResult })
      .then(() => {
        props.onCloseDetails()
        setIsAddActivityModalOpen(false)
      })
      .catch((response) => {
        console.error(response)
      })
  }

  const handleJsonInput = (event: any) => {
    try {
      setJsonResult(JSON.parse(event.json))
    } catch (error) {
      console.error(error)
    }
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
      <Modal show={isAddActivityModalOpen} onHide={() => setIsAddActivityModalOpen(false)} size='xl' centered>
        <Modal.Header style={{ fontWeight: 'bold', fontSize: '1.5rem' }}>Dodawanie nowej aktywności</Modal.Header>
        <Modal.Body className={styles.editorContainer}>
          <label htmlFor='chapterId' className={styles.chapterForm}>
            Do którego rozdziału przypisać aktywność?
            <input
              id='chapterId'
              name='chapterId'
              type='text'
              value={chapterId}
              required
              onChange={(event) => setChapterId(event.target.value)}
            />
          </label>
          <JSONInput
            ref={jsonRef}
            placeholder={jsonData}
            locale={locale}
            height='100%'
            width='100%'
            style={{ body: { fontSize: '15px' }, outerBox: { maxHeight: '60vh', overflowY: 'auto' } }}
            onChange={handleJsonInput}
            waitAfterKeyPress={400}
          />
          <Button type='submit' className={styles.editorButton} onClick={handleCreateTask}>
            Dodaj aktywność
          </Button>
        </Modal.Body>
      </Modal>
    </>
  )
}

export default GradeSubmitTask
