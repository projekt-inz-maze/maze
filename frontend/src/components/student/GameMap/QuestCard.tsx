import React from 'react'

import { Button } from 'react-bootstrap'

import styles from './QuestCard.module.scss'

type QuestCardProps = {
  description: string
  onShowQuestDetails: () => void
}

const QuestCard = (props: QuestCardProps) => (
  <div className={styles.questCard}>
    <Button className={styles.questItem} onClick={props.onShowQuestDetails}>
      <p>{props.description}</p>
    </Button>
  </div>
)

export default QuestCard
