import { useState } from 'react'

import styles from './ActivityListItem.module.scss'
import { ActivityResponseInfo } from '../../../api/types'
import { Activity, getActivityTypeName } from '../../../utils/constants'
import GradeFileTaskModal from '../GradeTask/GradeFileTaskModal/GradeFileTaskModal'

type ActivityListItemProps = {
  activity: ActivityResponseInfo
  toGrade: number
}

const ActivityListItem = (props: ActivityListItemProps) => {
  const [showModal, setShowModal] = useState(false)

  return (
    <>
      <div className={styles.activityItem} onClick={() => setShowModal(true)}>
        <div>{`${getActivityTypeName(Activity.TASK)} - ${props.activity.activityName}`}</div>
        <div>{`Do sprawdzenia: ${props.toGrade}`}</div>
      </div>
      <GradeFileTaskModal
        showDetails={showModal}
        onCloseDetails={() => setShowModal(false)}
        activity={props.activity}
      />
    </>
  )
}

export default ActivityListItem
