import React, { useState } from 'react'

import { Button } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'

import styles from './QuestCard.module.scss'
import { ActivityResponse } from '../../../../api/types'
import ActivityDetails from '../../../../common/components/ActivityDetails/ActivityDetails'
import Auction from '../../../../common/components/Auction/Auction'
import { StudentRoutes } from '../../../../routes/PageRoutes'
import { getActivityPath } from '../../../../utils/constants'
import { convertMilisecondsToMinutes } from '../../../../utils/formatters'
import QuestSubmit from '../QuestSubmit/QuestSubmit'

type QuestCardProps = {
  activity: ActivityResponse
  description: string
  isActivityCompleted: boolean
}

const QuestCard = (props: QuestCardProps) => {
  const navigate = useNavigate()
  const [showModal, setShowModal] = useState(false)

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
      {props.activity.type !== 'AUCTION' && props.activity.type !== 'SUBMIT' && props.activity.type !== 'INFO' && (
        <ActivityDetails
          activityId={props.activity.id}
          showDetails={showModal}
          onCloseDetails={() => setShowModal(false)}
          onStartActivity={handleStartActivity}
          isActivityCompleted={props.isActivityCompleted}
          name={props.activity.title}
          type={props.activity.type}
          startDate='20:00, 10.01.2024'
          endDate='22:00, 12.01.2024'
          description={props.activity.description}
          numberOfAttempts={props.isActivityCompleted ? 1 : 0}
          maxNumberOfAttempts={1}
          timeLimit={convertMilisecondsToMinutes(props.activity.timeLimit)}
          points={props.activity.points}
        />
      )}
      {props.activity.type === 'INFO' && (
        <ActivityDetails
          activityId={props.activity.id}
          showDetails={showModal}
          onCloseDetails={() => setShowModal(false)}
          onStartActivity={handleStartActivity}
          isActivityCompleted={false}
          name={props.activity.title}
          type={props.activity.type}
          startDate='20:00, 10.01.2024'
          endDate='22:00, 12.01.2024'
          description={props.activity.description}
          numberOfAttempts={0}
          maxNumberOfAttempts={1}
          timeLimit={convertMilisecondsToMinutes(props.activity.timeLimit)}
          points={props.activity.points}
        />
      )}
      {props.activity.type === 'AUCTION' && (
        <Auction
          activityId={props.activity.id}
          showDetails={showModal}
          onCloseDetails={() => setShowModal(false)}
          points={props.activity.points}
        />
      )}
      {props.activity.type === 'SUBMIT' && (
        <QuestSubmit
          activityId={props.activity.id}
          showDetails={showModal}
          onCloseDetails={() => setShowModal(false)}
        />
      )}
      <div className={styles.questCard}>
        <Button className={styles.questItem} onClick={() => setShowModal(!showModal)}>
          <p>{props.description}</p>
        </Button>
      </div>
    </>
  )
}

export default QuestCard
