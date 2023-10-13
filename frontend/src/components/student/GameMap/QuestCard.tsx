import React, { useState } from 'react'

import { Button } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'

import styles from './QuestCard.module.scss'
import { ActivityResponse } from '../../../api/types'
import ActivityDetails from '../../../common/components/ActivityDetails/ActivityDetails'
import { StudentRoutes } from '../../../routes/PageRoutes'
import { getActivityPath } from '../../../utils/constants'

type QuestCardProps = {
  activity: ActivityResponse
  description: string
}

const QuestCard = (props: QuestCardProps) => {
  const [showModal, setShowModal] = useState(false)

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

  // const handleStartActivity = () =>
  //   navigate(StudentRoutes.GAME_MAP.GRAPH_TASK.EXPEDITION_WRAPPER, {
  //     state: {
  //       activityId: props.activity.id,
  //       maxPoints: props.activity.points
  //     }
  //   })

  return (
    <>
      <ActivityDetails
        showDetails={showModal}
        onCloseDetails={() => setShowModal(false)}
        onStartActivity={handleStartActivity}
        name={props.activity.title}
        type={props.activity.type}
        startDate='20:00, 10.01.2024'
        endDate='22:00, 12.01.2024'
        description='Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et
            dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex
            ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu
            fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt
            mollit anim id est laborum.'
        isHazard={false}
        numberOfAttempts={999}
        maxNumberOfAttempts={1000}
        timeLimit={999}
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
