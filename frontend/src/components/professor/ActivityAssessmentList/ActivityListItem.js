import { Row } from 'react-bootstrap'
import { connect } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import styled from 'styled-components'

import { TeacherRoutes } from '../../../routes/PageRoutes'
import { getActivityImg, getActivityTypeName , Activity } from '../../../utils/constants'
import { ActivityImg } from '../../student/AllActivities/ExpeditionTask/ActivityInfo/ActivityInfoStyles'


// todo: move it to styles file
const ActivityListItemRow = styled(Row)`
  margin: 20px auto;
  width: 75%;
  background-color: ${(props) => props.$background};
  color: ${(props) => props.$fontColor};
  padding: 8px;
  padding-right: 10px;
  text-align: center;

  &:hover {
    cursor: pointer;
  }

  @media (max-width: 575px) {
    max-width: 95%;
  }
`

function ActivityListItem(props) {
  const navigate = useNavigate()
  return (
    <ActivityListItemRow
      $background={props.theme.primary}
      $fontColor={props.theme.font}
      onClick={() => {
        navigate(TeacherRoutes.ACTIVITY_ASSESSMENT.ACTIVITY, {
          state: { activityId: props.activity.fileTaskId }
        })
      }}
    >
      <ActivityImg src={getActivityImg(Activity.TASK)} />
      <div>{`${getActivityTypeName(Activity.TASK)} - ${props.activity.activityName}`}</div>
      <div style={{ marginLeft: 'auto' }}>{`${props.toGrade  } do sprawdzenia`}</div>
    </ActivityListItemRow>
  )
}

function mapStateToProps(state) {
  const {theme} = state

  return { theme }
}
export default connect(mapStateToProps)(ActivityListItem)
