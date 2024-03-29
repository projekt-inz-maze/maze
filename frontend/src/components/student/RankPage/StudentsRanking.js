import React, { useState, useEffect } from 'react'

import {Container, Tab} from 'react-bootstrap'
import { connect } from 'react-redux'

import { useAppSelector } from '../../../hooks/hooks'
import RankingService from '../../../services/ranking.service'
import Ranking from '../../general/Ranking/Ranking'
import { TabsContainer } from '../../professor/ParticipantsPage/ParticipantsStyles'


function StudentsRanking(props) {
  const [ranking, setRanking] = useState(undefined)
  const [studentGroupRanking, setStudentGroupRanking] = useState(undefined)
  const [studentRankingPosition, setStudentRankingPosition] = useState(undefined)
  const [studentRankingGroupPosition, setStudentRankingGroupPosition] = useState(undefined)

  const courseId = useAppSelector((state) => state.user.courseId)

  useEffect(() => {
    RankingService.getGlobalRankingList(courseId)
      .then((response) => {
        setRanking(response)
      })
      .catch(() => {
        setRanking(null)
      })

    RankingService.getStudentGroupRankingList(courseId)
      .then((response) => {
        setStudentGroupRanking(response)
      })
      .catch(() => {
        setStudentGroupRanking(null)
      })

    RankingService.getStudentPositionInGlobalRanking(courseId)
      .then((response) => {
        setStudentRankingPosition(response)
      })
      .catch(() => {
        setStudentGroupRanking(null)
      })

    RankingService.getStudentPositionInGroupRanking(courseId)
      .then((response) => {
        setStudentRankingGroupPosition(response)
      })
      .catch(() => {
        setStudentRankingGroupPosition(null)
      })
  }, [])

  return (
    <Container fluid>
      <TabsContainer
        $background={props.theme.success}
        $fontColor={props.theme.background}
        $linkColor={props.theme.primary}
        defaultActiveKey="global-rank"
      >
        <Tab eventKey="global-rank" title="Ranking ogólny">
          <Ranking rankingList={ranking} studentPosition={studentRankingPosition} />
        </Tab>
        <Tab eventKey="student-group" title="Moja grupa">
          <Ranking rankingList={studentGroupRanking} studentPosition={studentRankingGroupPosition} />
        </Tab>
      </TabsContainer>
    </Container>
  )
}

function mapStateToProps(state) {
  const {theme} = state
  return {
    theme
  }
}
export default connect(mapStateToProps)(StudentsRanking)
