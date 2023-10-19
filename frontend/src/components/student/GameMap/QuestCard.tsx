import React, { useState } from 'react'

import { Button } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'

import styles from './QuestCard.module.scss'
import { ActivityRequirements, ActivityResponse } from '../../../api/types'
import ActivityDetails from '../../../common/components/ActivityDetails/ActivityDetails'
import { StudentRoutes } from '../../../routes/PageRoutes'
import { getActivityPath } from '../../../utils/constants'
import { convertMilisecondsToMinutes } from '../../../utils/formatters'

const emptyRequirements: ActivityRequirements = {
  isBlocked: false,
  requirements: []
}

type QuestCardProps = {
  activity: ActivityResponse
  description: string
  isActivityCompleted: boolean
}

const QuestCard = (props: QuestCardProps) => {
  const [showModal, setShowModal] = useState(false)
  const [requirements, setRequirements] = useState<ActivityRequirements>(emptyRequirements)

  const navigate = useNavigate()

  const handleStartActivity = () => {
    setShowModal(false)
    if (props.activity.type === 'EXPEDITION') {
      navigate(StudentRoutes.GAME_MAP.GRAPH_TASK.EXPEDITION_WRAPPER, {
        state: {
          activityId: props.activity.id,
          maxPoints: props.activity.points
        }
      })
    } else {
      navigate(`${getActivityPath(props.activity.type)}`, {
        state: { activityId: props.activity.id }
      })
    }
  }

  return (
    <>
      <ActivityDetails
        showDetails={showModal}
        onCloseDetails={() => setShowModal(false)}
        onStartActivity={handleStartActivity}
        isActivityCompleted={props.isActivityCompleted}
        name={props.activity.title}
        type={props.activity.type}
        startDate='20:00, 10.01.2024'
        endDate='22:00, 12.01.2024'
        description={props.activity.description}
        isWager={props.activity.wager}
        numberOfAttempts={0}
        maxNumberOfAttempts={1}
        timeLimit={convertMilisecondsToMinutes(props.activity.timeLimit)}
        points={props.activity.points}
        result={0}
      />

      <div className={styles.questCard}>
        <Button className={styles.questItem} onClick={() => setShowModal(!showModal)}>
          <p>{props.description}</p>
        </Button>
      </div>
    </>
  )
}

export default QuestCard
