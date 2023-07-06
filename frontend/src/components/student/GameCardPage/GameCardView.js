import React, { useEffect, useState } from 'react'

import { Col, Row } from 'react-bootstrap'
import { connect } from 'react-redux'

import GameCard from './GameCard'
import {
  GradesStatsContent,
  HeroStatsContent,
  LastActivitiesContent,
  PersonalRankingInfoContent
} from './gameCardContents'
import { useAppSelector } from '../../../hooks/hooks'
import StudentService from '../../../services/student.service'
import { ERROR_OCCURRED } from '../../../utils/constants'
import { isMobileView } from '../../../utils/mobileHelper'
import { Content } from '../../App/AppGeneralStyles'
import Loader from '../../general/Loader/Loader'

function GameCardView(props) {
  const isMobile = isMobileView()
  const [dashboardStats, setDashboardStats] = useState(undefined)
  const courseId = useAppSelector((state) => state.user.courseId)

  useEffect(() => {
    StudentService.getDashboardStats(courseId)
      .then((response) => {
        setDashboardStats(response)
        localStorage.setItem('heroType', response.heroTypeStats.heroType)
      })
      .catch(() => setDashboardStats(null))
  }, [])

  return (
    <Content>
      {dashboardStats === undefined ? (
        <Loader />
      ) : dashboardStats == null ? (
        <p className="text-danger">{ERROR_OCCURRED}</p>
      ) : (
        <>
          <Row className='m-0 pt-4 gy-2' style={{ height: isMobile ? 'auto' : '50vh' }}>
            <Col md={5} style={{ height: isMobile ? '45%' : '100%' }}>
              <GameCard
                headerText="Statystyki bohatera"
                content={
                  <HeroStatsContent
                    stats={{ ...dashboardStats.heroStats, heroType: dashboardStats.heroTypeStats.heroType }}
                  />
                }
              />
            </Col>
            <Col md={7} style={{ height: isMobile ? '55%' : '100%' }}>
              <GameCard
                headerText="Statystyki ocen"
                content={<GradesStatsContent stats={dashboardStats.generalStats} />}
              />
            </Col>
          </Row>
          <Row className='m-0 mb-5 m-md-0 pt-3' style={{ height: isMobile ? '140vh' : '50vh' }}>
            <Col md={5} style={{ height: isMobile ? '30%' : '100%' }}>
              <GameCard
                headerText="Miejsce w rankingu"
                content={
                  <PersonalRankingInfoContent
                    stats={{ ...dashboardStats.heroTypeStats, userPoints: dashboardStats.generalStats.allPoints }}
                  />
                }
              />
            </Col>
            <Col md={7} style={{ height: isMobile ? '68%' : '100%', marginBottom: isMobile ? '20px' : 'auto' }}>
              <GameCard
                headerText="Ostatnio dodane aktywności"
                content={<LastActivitiesContent theme={props.theme} stats={dashboardStats.lastAddedActivities} />}
              />
            </Col>
          </Row>
        </>
      )}
    </Content>
  )
}

function mapStateToProps(state) {
  const {theme} = state

  return { theme }
}
export default connect(mapStateToProps)(GameCardView)
