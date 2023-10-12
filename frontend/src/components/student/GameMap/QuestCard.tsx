import React, { useState } from 'react'

import { Button } from 'react-bootstrap'

import styles from './QuestCard.module.scss'
import { ActivityResponse } from '../../../api/types'
import ActivityDetails from '../../../common/components/ActivityDetails/ActivityDetails'

type QuestCardProps = {
  activity: ActivityResponse
  description: string
}

const QuestCard = (props: QuestCardProps) => {
  const [showModal, setShowModal] = useState(false)

  return (
    <>
      <ActivityDetails
        showDetails={showModal}
        onCloseDetails={() => setShowModal(false)}
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
        numberOfAttempts={0}
        maxNumberOfAttempts={1}
        timeLimit={60}
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
