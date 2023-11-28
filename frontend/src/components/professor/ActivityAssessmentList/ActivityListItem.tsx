import { useEffect, useState } from 'react'

import styles from './ActivityListItem.module.scss'
import { useGetFirstTaskToGradeQuery } from '../../../api/apiGrades'
import { ActivityResponseInfo } from '../../../api/types'
import { Activity, getActivityTypeName } from '../../../utils/constants'
import GradeFileTaskModal from '../GradeTask/GradeFileTaskModal/GradeFileTaskModal'

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
  activity: number
  toGrade: number
}

const ActivityListItem = (props: ActivityListItemProps) => {
  const [showModal, setShowModal] = useState(false)

  const [activity, setActivity] = useState<ActivityResponseInfo>(emptyActivity)

  const { data: activityToGrade, isSuccess } = useGetFirstTaskToGradeQuery(props.activity, {
    skip: props.activity === -1
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
            <div>{`${getActivityTypeName(Activity.TASK)} - ${activity.activityName}`}</div>
            <div>{`Do sprawdzenia: ${props.toGrade}`}</div>
          </>
        </div>
      ) : (
        <div>Brak zadań do sprawdzenia</div>
      )}
      <GradeFileTaskModal showDetails={showModal} onCloseDetails={() => setShowModal(false)} activity={activity} />
    </>
  )
}

export default ActivityListItem
