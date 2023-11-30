import { useEffect, useState } from 'react'

import styles from './ActivityListItem.module.scss'
import { useGetFirstTaskToGradeQuery } from '../../../api/apiGrades'
import { ActivityResponseInfo, ActivityType } from '../../../api/types'
import { getActivityTypeName } from '../../../utils/constants'
import GradeFileTask from '../GradeTask/GradeFileTask/GradeFileTask'

const emptyActivity: ActivityResponseInfo = {
  userEmail: '',
  fileTaskResponseId: 0,
  firstName: '',
  lastName: '',
  activityName: '',
  isLate: false,
  activityDetails: '',
  userAnswer: '',
  file: [],
  maxPoints: 0,
  fileTaskId: 0,
  remaining: 0
}

type ActivityListItemProps = {
  activityId: number
  activityType: ActivityType
  toGrade: number
}

const ActivityListItem = (props: ActivityListItemProps) => {
  const [showModal, setShowModal] = useState(false)

  const [activity, setActivity] = useState<ActivityResponseInfo>(emptyActivity)

  const { data: activityToGrade, isSuccess } = useGetFirstTaskToGradeQuery(props.activityId, {
    skip: props.activityId === -1
  })

  useEffect(() => {
    if (!isSuccess) {
      return
    }
    setActivity(activityToGrade)
  }, [activityToGrade, isSuccess, activity])

  return (
    <>
      {activity ? (
        <div className={styles.activityItem} onClick={() => setShowModal(true)}>
          <>
            <div>{`${getActivityTypeName(props.activityType)} - ${activity.activityName}`}</div>
            <div>{`Do sprawdzenia: ${props.toGrade}`}</div>
          </>
        </div>
      ) : (
        <div>Brak zadań do sprawdzenia</div>
      )}
      <GradeFileTask showDetails={showModal} onCloseDetails={() => setShowModal(false)} activity={activity} />
    </>
  )
}

export default ActivityListItem
