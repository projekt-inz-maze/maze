import React, { useCallback, useEffect, useState } from 'react'

import { Row, Stack } from 'react-bootstrap'

import style from './QuestBoard.module.scss'
import QuestCard from './QuestCard'
import { ActivityMapResponse } from '../../../api/types'
import { useAppSelector } from '../../../hooks/hooks'
import ActivityService from '../../../services/activity.service'

const emptyMapResponse: ActivityMapResponse = {
  id: 0,
  tasks: [],
  mapSizeX: 0,
  mapSizeY: 0,
  image: null
}

const QuestBoard = () => {
  // const [showDetailsModal, setShowDetailsModal] = useState(false)
  const [activities, setActivities] = useState<ActivityMapResponse>(emptyMapResponse)

  const selectedChapterId = useAppSelector((state) => state.user.selectedChapterId)

  // const handleClose = () => setShowDetailsModal(false)

  useEffect(() => {
    getActivities()
  }, [selectedChapterId])

  const getActivities = useCallback(() => {
    ActivityService.getActivityMap(selectedChapterId)
      .then((response) => {
        setActivities(response)
      })
      .catch(() => {
        setActivities(emptyMapResponse)
      })
  }, [selectedChapterId])

  return (
    <div style={{ height: '100%', position: 'relative' }}>
      <Row className='h-100'>
        <Stack direction='horizontal' gap={5} className={style.questStack}>
          {activities.tasks.map((activity) => (
            <QuestCard key={activity.id} activity={activity} description={activity.type} />
          ))}
        </Stack>
      </Row>
    </div>
  )
}

export default QuestBoard
