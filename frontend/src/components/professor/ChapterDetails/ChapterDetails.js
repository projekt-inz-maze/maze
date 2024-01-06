import React, { useCallback, useEffect, useLayoutEffect, useRef, useState } from 'react'

import { faArrowDown, faArrowUp, faPaperclip, faPenToSquare, faTrash } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {
  Accordion,
  Button,
  Card,
  Col,
  Collapse,
  Container,
  ListGroup,
  ListGroupItem,
  OverlayTrigger,
  Row,
  Spinner,
  Table
} from 'react-bootstrap'
import { connect } from 'react-redux'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'

import AddActivityModal from './AddActivityModal'
import AddPhotoModal from './AddPhotoModal/AddPhotoModal'
import { ActivitiesCard, ButtonsCol, CustomTooltip, MapCard, SummaryCard, TableRow } from './ChapterDetailsStyles'
import DeletionModal from './DeletionModal'
import EditActivityModal from './EditActivityModal'
import { TeacherRoutes } from '../../../routes/PageRoutes'
import ActivityService from '../../../services/activity.service'
import ChapterService from '../../../services/chapter.service'
import { ERROR_OCCURRED, getActivityImg, getActivityTypeName } from '../../../utils/constants'
import { isMobileView } from '../../../utils/mobileHelper'
import { successToast } from '../../../utils/toasts'
import Loader from '../../general/Loader/Loader'
import ChapterMap from '../../student/GameMapPage/Map/ChapterMap'
import ChapterModal from '../GameManagement/ChapterModal/ChapterModal'

function ChapterDetails(props) {
  const { id: chapterId } = useParams()
  const navigate = useNavigate()
  const location = useLocation()

  const mapCardBody = useRef()

  const [openActivitiesDetailsList, setOpenActivitiesDetailsList] = useState(false)

  const [isDeletionModalOpen, setDeletionModalOpen] = useState(false)
  const [isEditChapterModalOpen, setEditChapterModalOpen] = useState(false)
  const [chosenActivityData, setChosenActivityData] = useState(null)
  const [isEditActivityModalOpen, setIsEditActivityModalOpen] = useState(false)
  const [isDeleteActivityModalOpen, setIsDeleteActivityModalOpen] = useState(false)
  const [isAddActivityModalOpen, setIsAddActivityModalOpen] = useState(false)
  const [shouldLoadEditChapterModal, setShouldLoadEditChapterModal] = useState(false)
  const [isPhotoModalOpen, setIsPhotoModalOpen] = useState(false)

  const [chapterDetails, setChapterDetails] = useState(undefined)
  const [mapContainerSize, setMapContainerSize] = useState({ x: 0, y: 0 })

  const [reloadMapNeeded, setReloadMapNeeded] = useState(false)

  const [deleteActivityError, setDeleteActivityError] = useState(undefined)
  const [deleteChapterError, setDeleteChapterError] = useState(undefined)

  useLayoutEffect(() => {
    setMapContainerSize({
      x: mapCardBody.current?.offsetWidth ?? 0,
      y: mapCardBody.current?.offsetHeight ?? 0
    })
  }, [])

  useEffect(() => {
    const updateContainerSize = () =>
      setMapContainerSize({
        x: mapCardBody.current?.offsetWidth ?? 0,
        y: mapCardBody.current?.offsetHeight ?? 0
      })

    window.addEventListener('resize', updateContainerSize)

    return () => {
      window.removeEventListener('resize', updateContainerSize)
    }
  }, [])

  const getChapterDetails = useCallback(() => {
    setReloadMapNeeded(false)
    ChapterService.getChapterDetails(chapterId)
      .then((response) => {
        setChapterDetails(response)
        setReloadMapNeeded(true)
      })
      .catch(() => {
        setChapterDetails(null)
      })
  }, [chapterId])

  useEffect(() => {
    getChapterDetails()
  }, [getChapterDetails])

  useEffect(() => {
    if (chosenActivityData?.jsonConfig) {
      setIsEditActivityModalOpen(true)
    }
  }, [chosenActivityData?.jsonConfig])

  const getActivityInfo = useCallback((activityId) => {
    ActivityService.getActivityInfo(activityId)
      .then((response) =>
        setChosenActivityData((prevState) => ({
          ...prevState,
          jsonConfig: response.activityBody
        }))
      )
      .catch(() => {
        setChosenActivityData((prevState) => ({ ...prevState, jsonConfig: null }))
      })
  }, [])

  const goToChapterDetails = (activityName, activityId, activityType) => {
    navigate(`${location.pathname}/activity/${activityName}`, {
      state: {
        activityId,
        activityType,
        chapterName: chapterDetails.name,
        chapterId
      }
    })
  }

  const startActivityEdition = (activity) => {
    setReloadMapNeeded(false)
    setChosenActivityData({
      activityId: activity.id,
      activityType: activity.type,
      activityName: activity.title
    })
    getActivityInfo(activity.id)
  }

  const startActivityDeletion = (activity) => {
    setChosenActivityData({
      activityId: activity.id,
      activityType: getActivityTypeName(activity.type),
      activityName: activity.title
    })
    setIsDeleteActivityModalOpen(true)
  }

  const startAddingPhoto = (activity) => {
    setChosenActivityData({
      activityId: activity.id,
      activityType: getActivityTypeName(activity.type),
      activityName: activity.title
    })
    setIsPhotoModalOpen(true)
  }

  const deleteChapter = () => {
    ChapterService.deleteChapter(chapterId)
      .then(() => {
        successToast('Rozdział usunięty pomyślnie.')
        setDeletionModalOpen(false)
        navigate(TeacherRoutes.GAME_MANAGEMENT.MAIN)
      })
      .catch((error) => setDeleteChapterError(error.response?.data?.message ?? ERROR_OCCURRED))
  }

  const deleteActivity = () => {
    ActivityService.deleteActivity(chosenActivityData.activityId)
      .then(() => {
        successToast(
          <p>
            Aktywność <strong>{chosenActivityData.activityName}</strong> usunięta pomyślnie.
          </p>
        )
        setIsDeleteActivityModalOpen(false)
        getChapterDetails()
      })
      .catch((error) => {
        setDeleteActivityError(error.response?.data?.message ?? ERROR_OCCURRED)
      })
  }

  const goToRequirements = () => {
    navigate(TeacherRoutes.GAME_MANAGEMENT.CHAPTER.REQUIREMENTS, {
      state: { chapterId, chapterName: chapterDetails.name }
    })
  }

  return (
    <Container fluid style={{ overflowX: 'hidden', marginBottom: isMobileView() ? 60 : 0 }}>
      <Row className='px-0 m-0'>
        <Col className='m-0 h-100' md={6}>
          <Col md={12}>
            <Accordion style={{ paddingTop: '2em' }} onClick={() => setReloadMapNeeded(true)}>
              <Accordion.Item eventKey='0'>
                <Accordion.Header>
                  <span style={{ fontWeight: 'bold', color: '#071542' }}>Mapa rozdziału</span>
                </Accordion.Header>
                <Accordion.Body>
                  <MapCard
                    $bodyColor={props.theme.secondary}
                    $headerColor={props.theme.primary}
                    $fontColor={props.theme.font}
                    className='mt-2'
                  >
                    <Card.Body ref={mapCardBody}>
                      <ChapterMap
                        chapterId={chapterId}
                        marginNeeded
                        parentSize={mapContainerSize}
                        reload={reloadMapNeeded}
                      />
                    </Card.Body>
                  </MapCard>
                </Accordion.Body>
              </Accordion.Item>
            </Accordion>
          </Col>
          <Col md={12} style={{ height: '25%' }}>
            <Accordion style={{ paddingTop: '1em' }}>
              <Accordion.Item eventKey='0'>
                <Accordion.Header>
                  <span style={{ fontWeight: 'bold', color: '#071542' }}>Podsumowanie rozdziału</span>
                </Accordion.Header>
                <Accordion.Body>
                  <SummaryCard
                    $bodyColor={props.theme.secondary}
                    $headerColor={props.theme.primary}
                    $fontColor={props.theme.font}
                    className='h-100'
                  >
                    <Card.Body className='p-0'>
                      {chapterDetails === undefined ? (
                        <Loader />
                      ) : chapterDetails == null ? (
                        <p>{ERROR_OCCURRED}</p>
                      ) : (
                        <ListGroup>
                          <ListGroupItem>
                            <span style={{ fontWeight: 'bold' }}>Nazwa rozdziału: </span>
                            {chapterDetails.name}
                          </ListGroupItem>
                          <ListGroupItem>
                            <Row className='d-flex align-items-center'>
                              <Col xs={10}>
                                <span style={{ fontWeight: 'bold' }}>Liczba dodanych aktywności: </span>
                                {chapterDetails.noActivities}
                              </Col>
                              <Col xs={2} className='text-end'>
                                <FontAwesomeIcon
                                  icon={openActivitiesDetailsList ? faArrowUp : faArrowDown}
                                  onClick={() => setOpenActivitiesDetailsList(!openActivitiesDetailsList)}
                                  aria-controls='activities'
                                  aria-expanded={openActivitiesDetailsList}
                                />
                              </Col>
                            </Row>
                            <Collapse in={openActivitiesDetailsList}>
                              <div id='activities'>
                                <div>
                                  <span style={{ fontWeight: 'bold' }}>Ekspedycje: </span>
                                  {chapterDetails.noGraphTasks}
                                </div>
                                <div>
                                  <span style={{ fontWeight: 'bold' }}>Zadania bojowe: </span>
                                  {chapterDetails.noFileTasks}
                                </div>
                                <div>
                                  <span style={{ fontWeight: 'bold' }}>Wytyczne: </span>
                                  {chapterDetails.noInfoTasks}
                                </div>
                                <div>
                                  <span style={{ fontWeight: 'bold' }}>Wywiady: </span>
                                  {chapterDetails.noSurveyTasks}
                                </div>
                              </div>
                            </Collapse>
                          </ListGroupItem>
                          <ListGroupItem>
                            <span style={{ fontWeight: 'bold' }}>Suma punktów możliwych do zdobycia w rozdziale: </span>
                            {chapterDetails.maxPoints}
                          </ListGroupItem>
                          <ListGroupItem>
                            <span style={{ fontWeight: 'bold' }}>Aktualny rozmiar mapy: </span> {chapterDetails.mapSize}
                          </ListGroupItem>
                        </ListGroup>
                      )}
                    </Card.Body>
                  </SummaryCard>
                </Accordion.Body>
              </Accordion.Item>
            </Accordion>
          </Col>
          <Col md={12} style={{ height: '20%' }} className='my-2'>
            <Accordion defaultActiveKey='0' style={{ paddingTop: '1em' }}>
              <Accordion.Item eventKey='0'>
                <Accordion.Header>
                  <span style={{ fontWeight: 'bold', color: '#071542' }}>Wymagania </span>
                </Accordion.Header>
                <Accordion.Body
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center'
                  }}
                >
                  <p className='text-center'>
                    Edycja wymagań rozdziału, które musi spełnić student, aby zobaczyć rozdział na mapie gry.
                  </p>
                  <Button
                    type='button'
                    onClick={goToRequirements}
                    style={{
                      width: '200px',
                      margin: '0.5em',
                      backgroundColor: '#FFB21C',
                      color: 'black',
                      border: 'none',
                      cursor: 'pointer'
                    }}
                  >
                    Przejdź do wymagań
                  </Button>
                </Accordion.Body>
              </Accordion.Item>
            </Accordion>
          </Col>
        </Col>
        <Col className='m-0' md={6}>
          <Col md={12}>
            <ActivitiesCard
              $bodyColor={props.theme.secondary}
              $headerColor={props.theme.primary}
              $fontColor={props.theme.font}
              style={{ paddingTop: '2em', borderWidth: '0', margin: 'O !important' }}
            >
              <Card.Header>Lista aktywności</Card.Header>
              <Card.Body className='p-0 mx-100' style={{ overflow: 'auto' }}>
                <Table style={{ width: isMobileView() ? '200%' : '100%' }}>
                  <tbody>
                    {chapterDetails === undefined ? (
                      <tr>
                        <td colSpan='100%' className='text-center'>
                          <Spinner animation='border' />
                        </td>
                      </tr>
                    ) : chapterDetails == null || chapterDetails.mapTasks.length === 0 ? (
                      <tr>
                        <td colSpan='100%' className='text-center'>
                          <p>{chapterDetails == null ? ERROR_OCCURRED : 'Lista aktywności jest pusta'}</p>
                        </td>
                      </tr>
                    ) : (
                      chapterDetails.mapTasks.map((activity, index) => (
                        <OverlayTrigger
                          key={activity.title + index}
                          placement='top'
                          overlay={
                            activity.isActivityBlocked ? (
                              <CustomTooltip style={{ position: 'fixed' }}>
                                Aktywność została zablokowana. Studenci nie mogą jej zobaczyć. Żeby była odblokowana
                                musisz zmienić to w zakładce &quot;Wymagania&quot;.
                              </CustomTooltip>
                            ) : (
                              <></>
                            )
                          }
                        >
                          <TableRow
                            $background={props.theme.primary}
                            onClick={() => goToChapterDetails(activity.title, activity.id, activity.type)}
                            style={{ opacity: activity.isActivityBlocked ? 0.4 : 1 }}
                          >
                            <td>
                              <img
                                src={getActivityImg(activity.type)}
                                width={32}
                                height={32}
                                alt='activity img'
                                style={{ padding: '0.2em' }}
                              />
                            </td>
                            <td>{getActivityTypeName(activity.type)}</td>
                            <td>{activity.title}</td>
                            <td>
                              ({activity.posX}, {activity.posY})
                            </td>
                            <td style={{ minWidth: '70px' }}>Pkt: {activity.points ?? '-'}</td>
                            <td>
                              <FontAwesomeIcon
                                icon={faPaperclip}
                                size='lg'
                                style={{ padding: '0.25em' }}
                                onClick={(e) => {
                                  e.stopPropagation()
                                  startAddingPhoto(activity)
                                }}
                              />
                            </td>
                            <td>
                              <FontAwesomeIcon
                                icon={faPenToSquare}
                                size='lg'
                                style={{ padding: '0.25em' }}
                                onClick={(e) => {
                                  e.stopPropagation()
                                  startActivityEdition(activity)
                                }}
                              />
                            </td>
                            <td>
                              <FontAwesomeIcon
                                icon={faTrash}
                                size='lg'
                                style={{ padding: '0.25em' }}
                                onClick={(e) => {
                                  e.stopPropagation()
                                  startActivityDeletion(activity)
                                }}
                              />
                            </td>
                          </TableRow>
                        </OverlayTrigger>
                      ))
                    )}
                  </tbody>
                </Table>
              </Card.Body>
            </ActivitiesCard>
          </Col>
          <ButtonsCol md={12}>
            <Link to={TeacherRoutes.GAME_MANAGEMENT.MAIN}>
              <Button
                style={{
                  backgroundColor: props.theme.warning,
                  borderColor: props.theme.warning,
                  color: 'black'
                }}
              >
                Wyjdź
              </Button>
            </Link>
            <Button
              style={{ backgroundColor: props.theme.secondary, borderColor: props.theme.secondary }}
              onClick={() => {
                setEditChapterModalOpen(true)
                setShouldLoadEditChapterModal(true)
              }}
            >
              Edytuj rozdział
            </Button>
            <Button
              style={{ backgroundColor: props.theme.danger, borderColor: props.theme.danger }}
              onClick={() => setDeletionModalOpen(true)}
            >
              Usuń rozdział
            </Button>
            <Button
              style={{ backgroundColor: props.theme.success, borderColor: props.theme.success }}
              onClick={() => setIsAddActivityModalOpen(true)}
            >
              Dodaj aktywność
            </Button>
          </ButtonsCol>
        </Col>
      </Row>

      <AddPhotoModal
        showModal={isPhotoModalOpen}
        setShowModal={setIsPhotoModalOpen}
        activityId={chosenActivityData?.activityId}
        activityName={chosenActivityData?.activityName}
      />

      <DeletionModal
        showModal={isDeletionModalOpen}
        setModalOpen={setDeletionModalOpen}
        modalTitle='Usunięcie rozdziału'
        modalBody={
          <>
            <div>
              Czy na pewno chcesz usunąć rozdział: <br />
              <strong>{chapterDetails?.name}</strong>?
            </div>
            {deleteChapterError && <p style={{ color: props.theme.danger }}>{deleteChapterError}</p>}
          </>
        }
        chapterId={chapterId}
        onClick={deleteChapter}
      />

      <ChapterModal
        showModal={isEditChapterModalOpen}
        setShowModal={setEditChapterModalOpen}
        isLoaded={shouldLoadEditChapterModal}
        chapterDetails={chapterDetails}
        onSuccess={getChapterDetails}
      />

      <EditActivityModal
        setShowModal={setIsEditActivityModalOpen}
        showModal={isEditActivityModalOpen}
        activityId={chosenActivityData?.activityId}
        activityType={chosenActivityData?.activityType}
        jsonConfig={chosenActivityData?.jsonConfig}
        modalHeader={`Edycja aktywności: ${chosenActivityData?.activityName}`}
        successModalBody={
          <p>
            Twoje zmiany wprowadzone dla aktywności typu:{' '}
            <strong>{getActivityTypeName(chosenActivityData?.activityType)}</strong>
            <br /> o nazwie: <strong>{chosenActivityData?.activityName}</strong> zakończyła się pomyślnie.
          </p>
        }
        onSuccess={() => {
          getChapterDetails()
          setReloadMapNeeded(true)
        }}
      />

      <DeletionModal
        showModal={isDeleteActivityModalOpen}
        setModalOpen={setIsDeleteActivityModalOpen}
        modalTitle='Usunięcie aktywności'
        modalBody={
          <>
            <div>
              Czy na pewno chcesz usunąć aktywność typu: <strong>{chosenActivityData?.activityType}</strong>
              <br />o nazwie: <strong>{chosenActivityData?.activityName}</strong>?
            </div>
            {deleteActivityError && <p style={{ color: props.theme.danger }}>{deleteActivityError}</p>}
          </>
        }
        onClick={deleteActivity}
      />

      <AddActivityModal
        showModal={isAddActivityModalOpen}
        setShow={setIsAddActivityModalOpen}
        chapterId={chapterId}
        onSuccess={getChapterDetails}
      />
    </Container>
  )
}

function mapStateToProps(state) {
  const { theme } = state

  return { theme }
}

export default connect(mapStateToProps)(ChapterDetails)
