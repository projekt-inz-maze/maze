import React, { useCallback, useEffect, useState } from 'react'

import { Col, Row, Tab } from 'react-bootstrap'
import { connect } from 'react-redux'

import PercentageCircle from './ChartAndStats/PercentageCircle'
import { TabsContainer } from './PointsPageStyle'
import BonusPointsTable from './Tables/BonusPointsTable'
import LastPointsTable from './Tables/LastPointsTable'
import { useAppSelector } from '../../../hooks/hooks'
import StudentService from '../../../services/student.service'
import { ERROR_OCCURRED } from '../../../utils/constants'
import { Content } from '../../App/AppGeneralStyles'
import Loader from '../../general/Loader/Loader'

function Points(props) {
  const [pointsData, setPointsData] = useState(undefined)
  const [totalPointsData, setTotalPointsData] = useState(undefined)
  const courseId = useAppSelector((state) => state.user.courseId)

  const calculatedPercentageValue = useCallback(() => {
    if (totalPointsData.totalPointsPossibleToReceive === 0) {
      return 0
    }
    return Math.round(100 * (totalPointsData.totalPointsReceived / totalPointsData.totalPointsPossibleToReceive))
  }, [totalPointsData])

  const pointsToNextRank = 210

  useEffect(() => {
    StudentService.getPointsStats(courseId)
      .then((response) => {
        setPointsData(response)
      })
      .catch(() => {
        setPointsData(null)
      })
    StudentService.getTotalReceivedPoints(courseId)
      .then((response) => {
        setTotalPointsData(response)
      })
      .catch(() => {
        setTotalPointsData(null)
      })
  }, [])

  return (
    <Content>
      {totalPointsData === undefined ? (
        <Loader />
      ) : totalPointsData == null ? (
        ERROR_OCCURRED
      ) : (
        <Row className='m-0'>
          <Col className='p-0'>
            <PercentageCircle
              percentageValue={calculatedPercentageValue()}
              points={totalPointsData.totalPointsReceived}
              maxPoints={totalPointsData.totalPointsPossibleToReceive}
            />
          </Col>
          <Col className='p-0 justify-content-center d-flex flex-column'>
            <h5>
              <strong>Twój wynik to: {totalPointsData.totalPointsReceived}pkt</strong>
            </h5>
            <h5>
              <strong>{`Co stanowi: ${  calculatedPercentageValue()}`}%</strong>
            </h5>
            <h5>
              {/* not yet here */}
              <strong>Do kolejnego poziomu wymagane jest: {pointsToNextRank}pkt</strong>
            </h5>
          </Col>
        </Row>
      )}
      <Row className='m-0 m-md-3'>
        <TabsContainer
          $linkColor={props.theme.primary}
          className="w-100"
          defaultActiveKey='last-points'
          id='points-tabs'
          style={{ padding: '50px 0 20px 0' }}
        >
          <Tab className="w-100" eventKey='last-points' title='Ostatnio zdobyte punkty'>
            <LastPointsTable pointsList={pointsData} />
          </Tab>
          <Tab eventKey="bonus-points" title="Punkty dodatkowe">
            <BonusPointsTable />
          </Tab>
        </TabsContainer>
      </Row>
    </Content>
  )
}

function mapStateToProps(state) {
  const {theme} = state

  return { theme }
}
export default connect(mapStateToProps)(Points)
