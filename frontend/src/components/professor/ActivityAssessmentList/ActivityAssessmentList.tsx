import { useEffect, useState } from 'react'

import { Container } from 'react-bootstrap'

import styles from './ActivityAssessmentList.module.scss'
import ActivityListItem from './ActivityListItem'
import { useGetTasksToGradeQuery } from '../../../api/apiGrades'
import { ActivityToGrade } from '../../../api/types'
import { useAppSelector } from '../../../hooks/hooks'

// note: currently the list assumes we can only manually grade File Tasks - this is due to the way our DB currently works,
// an ID is unique only in the task group. we might need to add a field that lets us know which task type it is
// on the backend if we want to check other types of activities in the future

const emptyActivityList: ActivityToGrade[] = [{ activityId: -1, activityType: 'TASK', toGrade: 0 }]

const ActivityAssessmentList = () => {
  const [activityList, setActivityList] = useState<ActivityToGrade[]>(emptyActivityList)

  const courseId = useAppSelector((state) => state.user.courseId)

  const { data: activities, isSuccess: isActivitiesSuccess } = useGetTasksToGradeQuery(courseId)

  useEffect(() => {
    if (!isActivitiesSuccess) {
      return
    }
    setActivityList(activities)
  }, [activities, isActivitiesSuccess])

  return (
    <Container fluid className={styles.container}>
      <p className={styles.listTitle}>Aktywności do sprawdzenia</p>
      <div className={styles.listItem}>
        {isActivitiesSuccess &&
          activityList.map((activity) => (
            <ActivityListItem
              key={activity.activityId}
              activityId={activity.activityId}
              activityType={activity.activityType}
              toGrade={activity.toGrade}
            />
          ))}
      </div>
    </Container>
  )
}

export default ActivityAssessmentList
