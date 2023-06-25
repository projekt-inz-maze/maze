import React, { useState, useEffect } from 'react'

import { Tab } from 'react-bootstrap'
import { connect } from 'react-redux'

import RankingService from '../../../services/ranking.service'
import { Content } from '../../App/AppGeneralStyles'
import Ranking from '../../general/Ranking/Ranking'
import { TabsContainer } from '../../professor/ParticipantsPage/ParticipantsStyles'


function StudentsRanking(props) {
  const [ranking, setRanking] = useState(undefined)
  const [studentGroupRanking, setStudentGroupRanking] = useState(undefined)
  const [studentRankingPosition, setStudentRankingPosition] = useState(undefined)
  const [studentRankingGroupPosition, setStudentRankingGroupPosition] = useState(undefined)

  useEffect(() => {
    RankingService.getGlobalRankingList()
      .then((response) => {
        setRanking(response)
      })
      .catch(() => {
        setRanking(null)
      })

    RankingService.getStudentGroupRankingList()
      .then((response) => {
        setStudentGroupRanking(response)
      })
      .catch(() => {
        setStudentGroupRanking(null)
      })

    RankingService.getStudentPositionInGlobalRanking()
      .then((response) => {
        setStudentRankingPosition(response)
      })
      .catch(() => {
        setStudentGroupRanking(null)
      })

    RankingService.getStudentPositionInGroupRanking()
      .then((response) => {
        setStudentRankingGroupPosition(response)
      })
      .catch(() => {
        setStudentRankingGroupPosition(null)
      })
  }, [])

  return (
    <Content>
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
    </Content>
  )
}

function mapStateToProps(state) {
  const {theme} = state
  return {
    theme
  }
}
export default connect(mapStateToProps)(StudentsRanking)
