import React, { useState } from 'react'

import { Row, Stack } from 'react-bootstrap'

import style from './QuestBoard.module.scss'
import QuestCard from './QuestCard'
import ActivityDetails from '../../../common/components/ActivityDetails/ActivityDetails'

const QuestBoard = () => {
  const [showDetailsModal, setShowDetailsModal] = useState(false)

  const handleClose = () => setShowDetailsModal(false)

  return (
    <div style={{ height: '100%', position: 'relative' }}>
      <ActivityDetails
        showDetails={showDetailsModal}
        onCloseDetails={handleClose}
        name='Jungla kabli'
        type='Ekspedycja'
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
        points={100}
        result={0}
      />
      <Row className='h-100'>
        <Stack direction='horizontal' gap={5} className={style.questStack}>
          <QuestCard description='Zadanie 1' onShowQuestDetails={() => setShowDetailsModal(true)} />
          <QuestCard description='Zadanie 2' onShowQuestDetails={() => setShowDetailsModal(true)} />
          <QuestCard description='Zadanie 3' onShowQuestDetails={() => setShowDetailsModal(true)} />
          <QuestCard description='Zadanie 4' onShowQuestDetails={() => setShowDetailsModal(true)} />
          <QuestCard description='Zadanie 4' onShowQuestDetails={() => setShowDetailsModal(true)} />
          <QuestCard description='Zadanie 4' onShowQuestDetails={() => setShowDetailsModal(true)} />
          <QuestCard description='Zadanie 4' onShowQuestDetails={() => setShowDetailsModal(true)} />
          <QuestCard description='Zadanie 4' onShowQuestDetails={() => setShowDetailsModal(true)} />
          <QuestCard description='Zadanie 4' onShowQuestDetails={() => setShowDetailsModal(true)} />
        </Stack>
      </Row>
    </div>
  )
}

export default QuestBoard
