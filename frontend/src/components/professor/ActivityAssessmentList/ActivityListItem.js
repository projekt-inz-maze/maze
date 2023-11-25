import { connect } from 'react-redux'
import { useNavigate } from 'react-router-dom'

import styles from './ActivityListItem.module.scss'
import { TeacherRoutes } from '../../../routes/PageRoutes'
import { Activity, getActivityTypeName } from '../../../utils/constants'

function ActivityListItem(props) {
  const navigate = useNavigate()
  return (
    <div
      className={styles.activityItem}
      onClick={() => {
        navigate(TeacherRoutes.ACTIVITY_ASSESSMENT.ACTIVITY, {
          state: { activityId: props.activity.fileTaskId }
        })
      }}
    >
      <div>{`${getActivityTypeName(Activity.TASK)} - ${props.activity.activityName}`}</div>
      <div>{`Do sprawdzenia: ${props.toGrade}`}</div>
    </div>
  )
}

function mapStateToProps(state) {
  const { theme } = state

  return { theme }
}

export default connect(mapStateToProps)(ActivityListItem)
