import React, { useEffect, useState } from 'react'

import { Row } from 'react-bootstrap'
import { connect } from 'react-redux'
import { useLocation, useNavigate } from 'react-router-dom'

import ActivityDetails from '../../../../../common/components/ActivityDetails/ActivityDetails'
import { GeneralRoutes, StudentRoutes } from '../../../../../routes/PageRoutes'
import ExpeditionService from '../../../../../services/expedition.service'
import { getTimer } from '../../../../../utils/storageManager'
import { Content } from '../../../../App/AppGeneralStyles'
import Loader from '../../../../general/Loader/Loader'

function ExpeditionSummary(props) {
  const navigate = useNavigate()
  const [maxPointsOpen, setMaxPointsOpen] = useState(0)
  const [maxPointsClosed, setMaxPointsClosed] = useState(0)
  const [scoredPoints, setScoredPoints] = useState(0)
  const [closedQuestionPoints, setClosedQuestionPoints] = useState(0)
  const [remainingTime, setRemainingTime] = useState(0)
  const location = useLocation()
  const { expeditionId, isFinished } = location.state
  const [loaded, setLoaded] = useState(false)
  const [activityScore, setActivityScore] = useState(undefined)
  const [showModal, setShowModal] = useState(true)

  useEffect(() => {
    console.log(expeditionId)
    ExpeditionService.getExpeditionScore(expeditionId)
      .then((response) => {
        setActivityScore(response || -1)
      })
      .catch(() => {
        setActivityScore(null)
      })
  }, [expeditionId])

  useEffect(() => {
    if (expeditionId == null) {
      navigate(GeneralRoutes.HOME)
    } else if (activityScore !== undefined) {
      const promise1 = ExpeditionService.getExpeditionPointsMaxOpen(activityScore)
        .then((response) => setMaxPointsOpen(response ?? 0))
        .catch(() => setMaxPointsOpen(0))

      // TODO: For now we get points from /all, later we will get it from getActivityScore() when it gets fixed
      // StudentService.getActivityScore()...
      const promise2 = ExpeditionService.getExpeditionAllPoints(activityScore)
        .then((response) => {
          setScoredPoints(response ? response?.toFixed(2) : 0)
        })
        .catch(() => setScoredPoints(0))

      const promise3 = ExpeditionService.getExpeditionPointsClosed(activityScore)
        .then((response) => {
          setClosedQuestionPoints(response ? response?.toFixed(2) : 0)
        })
        .catch(() => setClosedQuestionPoints(0))

      const promise4 = ExpeditionService.getExpeditionPointsMaxClosed(activityScore)
        .then((response) => setMaxPointsClosed(response ?? 0))
        .catch(() => setMaxPointsClosed(0))

      const promise5 = ExpeditionService.getRemainingTime(activityScore)
        .then((response) => setRemainingTime(response ?? 0))
        .catch(() => setRemainingTime(0))

      Promise.allSettled([promise1, promise2, promise3, promise4, promise5]).then(() => setLoaded(true))
    }
  }, [expeditionId, navigate, activityScore])

  const finishExpeditionAndGoHome = () => {
    localStorage.removeItem('questionPoints')
    localStorage.removeItem('questionType')
    navigate(StudentRoutes.GAME_MAP.MAIN)
  }

  const showRemainingTime = () => {
    const timer = getTimer(remainingTime / 1000).split(':')
    const seconds = timer[2]
    const minutes = timer[1]
    const hours = timer[0]
    let timeString = `${seconds}s`

    if (minutes !== '00' || (minutes === '00' && hours !== '00')) {
      timeString = `${minutes}min, ${timeString}`
    }

    if (hours !== '00') {
      timeString = `${hours}h, ${timeString}`
    }

    return timeString
  }

  const handleCloseSummary = () => {
    setShowModal(false)
    finishExpeditionAndGoHome()
  }

  return (
    <Content>
      {!loaded ? (
        <Loader />
      ) : (
        <ActivityDetails
          showDetails={showModal}
          onCloseDetails={handleCloseSummary}
          onStartActivity={() => setShowModal(false)}
          name='Nazwa aktywności'
          type='Typ aktywności'
          startDate='20:00, 10.01.2024'
          endDate='22:00, 12.01.2024'
          description='Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et
            dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex
            ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu
            fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt
            mollit anim id est laborum.'
          isHazard={false}
          numberOfAttempts={1}
          maxNumberOfAttempts={1}
          timeLimit={999}
          points={maxPointsClosed + maxPointsOpen}
          result={activityScore}
        />
      )}
    </Content>
  )
}

function mapStateToProps(state) {
  const { theme } = state

  return { theme }
}
export default connect(mapStateToProps)(ExpeditionSummary)
