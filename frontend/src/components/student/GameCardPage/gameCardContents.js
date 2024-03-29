import React from 'react'

import { ArcElement, BarElement, CategoryScale, Chart as ChartJS, Legend, LinearScale, Title, Tooltip } from 'chart.js'
import moment from 'moment'
import { Col, Row } from 'react-bootstrap'
import { Bar, Pie } from 'react-chartjs-2'

import { ChartCol, CustomTable } from './gameCardContentsStyle'
import { barConfig, pieConfig } from '../../../utils/chartConfig'
import { convertHeroTypeToPlayerType, getActivityTypeName, getGameCardInfo, HeroImg } from '../../../utils/constants'
import { isMobileView } from '../../../utils/mobileHelper'
import { PlayerType } from '../../../utils/userRole'
import { colorPalette } from '../../general/chartHelper'
import PercentageCircle from '../PointsPage/ChartAndStats/PercentageCircle'

export function GradesStatsContent(props) {
  const {
    allPoints = 0,
    maxPoints = 0,
    avgGraphTask = 0,
    avgFileTask = 0,
    surveysNumber = 0,
    graphTaskPoints = 0,
    fileTaskPoints = 0
  } = props.stats
  const percentageValue = allPoints && maxPoints ? Math.round(100 * (allPoints / maxPoints)) : 0

  return (
    <Row className='h-100 d-flex justify-content-center align-items-center'>
      <Col md={7}>
        <p className='pb-1'>Średni wynik z ekspedycji: {avgGraphTask ?? 0}%</p>
        <p className='pb-1'>Średni wynik z zadań bojowych: {avgFileTask ?? 0}%</p>
        <p className='pb-1'>Ilość wykonanych sondaży: {surveysNumber}</p>
        <p className='pb-1'>Punkty zdobyte w ekspedycjach: {graphTaskPoints}</p>
        <p>Punkty zdobyte w zadaniach bojowych: {fileTaskPoints}</p>
      </Col>
      <Col md={5}>
        <PercentageCircle percentageValue={percentageValue} points={allPoints} maxPoints={maxPoints} />
      </Col>
    </Row>
  )
}

export function LastActivitiesContent(props) {
  return (
    <CustomTable $fontColor={props.theme.font} $borderColor={props.theme.primary} $background={props.theme.secondary}>
      <thead>
        <tr>
          <th>Rozdział</th>
          <th>Aktywność</th>
          <th>Punkty</th>
          <th>Dostępne do</th>
        </tr>
      </thead>
      <tbody>
        {props.stats.map((activity, index) => (
          <tr key={index + Date.now()}>
            <td>{activity.chapterName}</td>
            <td>{getActivityTypeName(activity.activityType)}</td>
            <td>{activity.points}</td>
            <td>
              {activity.availableUntil ? moment(activity.availableUntil).format('DD.MM.YYYY') : 'Bez limitu czasowego'}
            </td>
          </tr>
        ))}
      </tbody>
    </CustomTable>
  )
}

export function HeroStatsContent(props) {
  return (
    <Row
      className={`h-100 d-flex justify-content-center align-items-center ${
        isMobileView() ? 'flex-column' : 'flex-row'
      }`}
    >
      <Col md={4} className='h-100'>
        <img style={{ maxWidth: '100%' }} height='90%' src={HeroImg[props.heroType]} alt='Your hero' />
      </Col>
      <Col md={7}>
        <p className='pb-1'>Punkty doświadczenia: {props.stats.experiencePoints}</p>
        <p className='pb-1'>Punkty do kolejnej rangi: {props.stats.nextLvlPoints}</p>
        <p className='pb-1'>Ranga: {props.stats.rankName}</p>
        <p className='pb-1'>Zdobytych medali: {props.stats.badgesNumber}</p>
        <p>Wykonanych aktywności: {props.stats.completedActivities}</p>
      </Col>
    </Row>
  )
}

export function PersonalRankingInfoContent(props) {
  const userPointsGroup = Math.ceil((props.stats.rankPosition / props.stats.rankLength) * 100)
  const playerType = convertHeroTypeToPlayerType(props.stats.heroType)
  const chartType = playerType === PlayerType.CHALLENGING ? 'BAR' : 'PIE'

  const rankComment = getGameCardInfo(playerType, {
    rankPosition: props.stats.rankPosition,
    rankLength: props.stats.rankLength,
    userPoints: userPointsGroup
  })

  ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement)

  const getChartInfo = () => {
    if (chartType === 'BAR') {
      const barLabels = [
        props.stats.betterPlayerPoints != null ? 'Punkty gracza przed Tobą' : '',
        'Twój wynik',
        props.stats.worsePlayerPoints != null ? 'Punkty gracza za Tobą' : ''
      ].filter((label) => !!label)

      const barPoints = [props.stats.betterPlayerPoints, props.stats.userPoints, props.stats.worsePlayerPoints].filter(
        (points) => points != null
      )
      return barConfig(barLabels, barPoints, colorPalette(barLabels.length))
    }

    return pieConfig(
      ['Grupa graczy, w której jesteś', 'Pozostali gracze'],
      [props.stats.rankPosition, props.stats.rankLength],
      colorPalette(2)
    )
  }

  const { data, options } = getChartInfo()

  return (
    <Row className='h-100 d-flex justify-content-center align-items-center'>
      <ChartCol md={12}>
        {chartType === 'BAR' ? <Bar data={data} options={options} /> : <Pie data={data} options={options} />}
      </ChartCol>
      <Col md={12}>
        <p className='text-center w-100'>{rankComment}</p>
      </Col>
    </Row>
  )
}
