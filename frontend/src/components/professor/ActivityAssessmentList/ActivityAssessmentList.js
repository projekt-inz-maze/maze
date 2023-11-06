import { useEffect, useState, useMemo } from 'react'

import {Col, Container} from 'react-bootstrap'
import { connect } from 'react-redux'

import ActivityListItem from './ActivityListItem'
import { useAppSelector } from '../../../hooks/hooks'
import ProfessorService from '../../../services/professor.service'
import { ERROR_OCCURRED } from '../../../utils/constants'
import Loader from '../../general/Loader/Loader'


// note: currently the list assumes we can only manually grade File Tasks - this is due to the way our DB currently works,
// an ID is unique only in the task group. we might need to add a field that lets us know which task type it is
// on the backend if we want to check other types of activities in the future

function ActivityAssessmentList(props) {
  const [activityList, setActivityList] = useState(undefined)

  const courseId = useAppSelector((state) => state.user.courseId)

  useEffect(() => {
    ProfessorService.getTasksToEvaluateList(courseId)
      .then((activityList) => {
        Promise.allSettled(
          activityList
            ?.filter((activity) => activity.toGrade !== 0)
            .map((activity) => ProfessorService.getFirstTaskToEvaluate(activity.activityId).then((response) => ({
                  activity: response,
                  toGrade: activity.toGrade
                })))
        ).then((response) => {
          setActivityList(
            response[0]?.status === 'rejected'
              ? null
              : response?.map((activity) => ({
                    activity: activity.value
                  }))
          )
        })
      })
      .catch(() => {
        setActivityList(null)
      })
  }, [])

  const colContent = useMemo(() => {
    if (activityList === undefined) {
      return <Loader />
    }
    if (activityList === null) {
      return (
        <p className="text-center h4" style={{ color: props.theme.danger }}>
          {ERROR_OCCURRED}
        </p>
      )
    }
    if (activityList.length > 0 && activityList.filter((activity) => activity.toGrade > 0)) {
      return activityList.map((activity) => {
        const listActivity = activity.activity.activity
        const {toGrade} = activity.activity
        return <ActivityListItem key={listActivity.activityName} activity={listActivity} toGrade={toGrade} />
      })
    }
    return <p className="text-center">Brak aktywności do sprawdzenia!</p>
  }, [activityList, props.theme.danger])

  return (
    <Container fluid>
      <h1 style={{ marginLeft: '20px', paddingTop: '20px' }}>Aktywności do sprawdzenia</h1>
      <Col style={{ paddingTop: '50px' }}>{colContent}</Col>
    </Container>
  )
}

function mapStateToProps(state) {
  const {theme} = state

  return { theme }
}
export default connect(mapStateToProps)(ActivityAssessmentList)
