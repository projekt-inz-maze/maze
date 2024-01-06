import React, { useEffect, useMemo, useRef, useState } from 'react'

import { faHourglass } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { debounce } from 'lodash'
import { Fade } from 'react-awesome-reveal'
import { Button, Col, Container, Form, Modal, Row, Spinner } from 'react-bootstrap'
import { connect } from 'react-redux'
import { useLocation, useNavigate } from 'react-router-dom'

import styles from './CombatTask.module.scss'
import { SendTaskButton } from './CombatTaskStyles'
import FeedbackFileService from './FeedbackFileService'
import FileService from './FileService'
import { StudentRoutes } from '../../../../routes/PageRoutes'
import CombatTaskService from '../../../../services/combatTask.service'
import { Activity } from '../../../../utils/constants'
import { isMobileView } from '../../../../utils/mobileHelper'
import GoBackButton from '../../../general/GoBackButton/GoBackButton'
import { ActivityDetails, Header, HorizontalSpacer, VerticalSpacer } from '../../../general/TaskSharedComponents'
import { RemarksTextArea } from '../../../professor/ActivityAssessmentDetails/ActivityAssesmentDetailsStyles'
import AttachedFiles from '../AttachedFiles/AttachedFiles'

const FIELD_DELAY = 600
const emptyTask = {
  name: '',
  description: '',
  answer: '',
  taskFiles: [],
  files: [],
  feedbackFile: {
    id: 1,
    name: ''
  },
  fileTaskId: 1
}

function CombatTask(props) {
  const isMobileDisplay = isMobileView()

  const navigate = useNavigate()
  const location = useLocation()
  const { activityId: taskState } = location.state

  const MD_WHEN_TASK_NOT_SENT = 12
  const MD_WHEN_TASK_SENT = 6

  const [task, setTask] = useState(emptyTask)
  const [fileBlob, setFileBlob] = useState()
  const [fileName, setFileName] = useState()
  const [answer, setAnswer] = useState('')
  const [isFetching, setIsFetching] = useState(false)
  const [answerWasSentNow, setAnswerWasSentNow] = useState(false)

  const textAreaRef = useRef(null)
  const isReviewed = () => task.points

  const resetStates = () => {
    setIsFetching(false)
    setFileBlob(null)
    setFileName(null)
    setAnswer('')
    setAnswerWasSentNow(true)
    textAreaRef.current.value = ''
  }

  useEffect(() => {
    CombatTaskService.getCombatTask(taskState)
      .then((response) => {
        setTask(response)
      })
      .catch(() => {
        setTask(null)
      })
  }, [isFetching, taskState])

  const sendAnswer = () => {
    setIsFetching(true)

    CombatTaskService.saveCombatTaskAnswer(taskState, answer, fileName, fileBlob)
      .then(() => {
        resetStates()
        setTimeout(() => navigate(StudentRoutes.NEW_GAME_MAP.MAIN), 1000)
      })
      .catch((error) => {
        // if there is an error from the 5XX group, the object can be added anyway, so we do resetState,
        // and when an error from the 4XX group occurs, we do not clean to send again
        if (error.response?.status < 400 || error.response?.status >= 500) {
          resetStates()
        }
        setIsFetching(false)
      })
  }

  const handleAnswerChange = useMemo(
    () =>
      debounce((event) => {
        setAnswer(event.target.value)
      }, 200),
    []
  )

  function AwaitingFeedbackField() {
    return (
      <Col md={6} className='text-center p-4 my-auto'>
        <Fade delay={FIELD_DELAY}>
          <FontAwesomeIcon className='m-2' icon={faHourglass} size='5x' spin />
          <h2 className='m-2'>Odpowiedź została przesłana, oczekiwanie na sprawdzenie przez prowadzącego</h2>
        </Fade>
      </Col>
    )
  }

  const contentBody = () => (
    <>
      <HorizontalSpacer height='3vh' />
      <Col
        className='pt-4 mx-auto'
        style={{
          height: isMobileDisplay ? 'auto' : '86vh',
          width: '90%',
          backgroundColor: props.theme.secondary,
          margin: isMobileDisplay ? '0 0 85px 0' : 0
        }}
      >
        <Row
          className='p-2 rounded mx-2'
          style={{ backgroundColor: props.theme.primary, height: isMobileDisplay ? 'auto' : '6vh' }}
        >
          <Header activityName={task.name} activityType={Activity.TASK} />
        </Row>
        <VerticalSpacer height='2vh' />
        <Row
          className='p-2 rounded mx-2 overflow-auto'
          style={{ backgroundColor: props.theme.primary, height: '25vh' }}
        >
          <ActivityDetails description={task.description} />
          <AttachedFiles files={task.files} />
        </Row>
        <VerticalSpacer height='2vh' />
        <Row
          className='p-2 rounded mx-2'
          style={{ backgroundColor: props.theme.primary, height: isMobileDisplay ? 'auto' : '46vh' }}
        >
          <Col
            md={task.answer || answerWasSentNow ? MD_WHEN_TASK_SENT : MD_WHEN_TASK_NOT_SENT}
            className='h-100 overflow-auto'
          >
            <Fade delay={FIELD_DELAY}>
              <>
                <h4>Odpowiedź:</h4>
                {isReviewed() ? (
                  <Col>
                    <h4>Twoja odpowiedź</h4>
                    <p>{task.answer}</p>
                  </Col>
                ) : (
                  <>
                    {task.answer && (
                      <Col>
                        <h5>Twoja obecna odpowiedź</h5>
                        <p>{task.answer}</p>
                      </Col>
                    )}
                    <RemarksTextArea
                      $fontColor={props.theme.font}
                      $background={props.theme.secondary}
                      $borderColor={props.theme.warning}
                      ref={textAreaRef}
                      disabled={isReviewed()}
                      onChange={handleAnswerChange}
                    />
                  </>
                )}
                <Col className='text-center'>
                  <FileService
                    task={task}
                    setFile={setFileBlob}
                    setFileName={setFileName}
                    setIsFetching={setIsFetching}
                    isFetching={isFetching}
                    isReviewed={isReviewed()}
                  />
                </Col>
                <Col className='w-100 text-center'>
                  <SendTaskButton $background={props.theme.success} disabled={task.points != null} onClick={sendAnswer}>
                    {isFetching ? (
                      <Spinner animation='border' size='sm' />
                    ) : isReviewed() ? (
                      <span>Aktywność została oceniona</span>
                    ) : (
                      <span>Wyślij</span>
                    )}
                  </SendTaskButton>
                </Col>
              </>
            </Fade>
          </Col>

          {task.answer != null || task.files != null || answerWasSentNow ? (
            !isReviewed() ? (
              <AwaitingFeedbackField />
            ) : (
              <Col className='border-left border-warning overflow-auto'>
                <h4>Aktywność została oceniona</h4>
                <VerticalSpacer height='2vh' />
                <Col
                  className='text-center mx-auto border p-1 rounded'
                  style={{ width: '20%', borderColor: props.theme.warning }}
                >
                  <h5>Punkty </h5>
                  <p>{task.points}</p>
                </Col>
                <VerticalSpacer height='2vh' />
                <h5>Uwagi:</h5>
                {task.remarks ? <p>{task.remarks}</p> : 'Brak uwag'}
                <FeedbackFileService feedbackFile={task.feedbackFile} />
              </Col>
            )
          ) : (
            <></>
          )}
        </Row>
      </Col>
      <GoBackButton goTo={StudentRoutes.GAME_MAP.MAIN} />
      <HorizontalSpacer height='3vh' />
    </>
  )

  return (
    <Container fluid>
      <Modal show size='xl' centered fullscreen className={styles.modalContainer}>
        <Modal.Header className={styles.modalHeader}>
          <Modal.Title className={styles.modalTitle}>{`Zadanie bojowe: ${task.name}`}</Modal.Title>
          <button
            type='button'
            className={styles.customButtonClose}
            onClick={() => navigate(StudentRoutes.NEW_GAME_MAP.MAIN)}
          >
            {/* Close button content */}
            <span>&times;</span>
          </button>
        </Modal.Header>
        <Modal.Body>
          <div className={styles.modalTaskDescription}>
            <p>
              <span>Treść:</span>
            </p>
            <p className={`text-justify ${styles.lastP}`}>{task.description ?? 'treść oczywista :))'}</p>
            <AttachedFiles files={task.files} />
            <Form.Group className='mb-3' controlId='exampleForm.ControlTextarea1'>
              <Form.Control
                as='textarea'
                rows={3}
                placeholder={`${task.answer ? `Udzieliłeś odpowiedzi: ${task.answer}` : 'Twoja odpowiedź'}`}
                disabled={task.answer}
                ref={textAreaRef}
                onChange={handleAnswerChange}
                className='mt-3'
              />
            </Form.Group>
            <FileService
              task={task}
              setFile={setFileBlob}
              setFileName={setFileName}
              setIsFetching={setIsFetching}
              isFetching={isFetching}
              isReviewed={isReviewed()}
            />
          </div>
          <div className={styles.grade}>
            {isReviewed() && (
              <div className={styles.resultFieldCompleted}>
                <span>Zdobyte punkty:</span>
                {task.points ? <p className={styles.lastP}>{task.points}pkt</p> : 'Zadanie niepunktowane'}
                <span>Uwagi do zadania:</span>
                {task.remarks ? <p className={styles.lastP}>{task.remarks}</p> : 'Brak uwag'}
                <FeedbackFileService feedbackFile={task.feedbackFile} />
              </div>
            )}
          </div>
        </Modal.Body>
        <Modal.Footer className={styles.modalFooter}>
          <Button
            type='button'
            className={styles.rejectButton}
            onClick={() => navigate(StudentRoutes.NEW_GAME_MAP.MAIN)}
          >
            Powrót
          </Button>
          <Button className={`${styles.acceptButton} ${task.answer ? 'disabled' : ''}`} onClick={sendAnswer}>
            <span>Wyślij</span>
          </Button>
        </Modal.Footer>
      </Modal>
      {/* {task === undefined ? <Loader /> : task == null ? ERROR_OCCURRED : contentBody()} */}
    </Container>
  )
}

function mapStateToProps(state) {
  const { theme } = state

  return { theme }
}

export default connect(mapStateToProps)(CombatTask)
