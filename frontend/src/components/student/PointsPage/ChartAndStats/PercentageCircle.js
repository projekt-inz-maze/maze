import React from 'react'

import { connect } from 'react-redux'

import { Chart, Circle, Percentage } from './PercentageCircleStyle'

function PercentageCircle(props) {
  const { percentageValue, points, maxPoints } = props
  return (
    <Chart>
      <svg viewBox='0 0 36 36'>
        <Circle
          $strokeColor={props.theme.warning}
          $fillColor={props.theme.primary}
          strokeDasharray={`${percentageValue}, 100`}
          d='M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831'
        />
        <Percentage x='18' y='20.35'>
          {points}
        </Percentage>
        <Percentage x='24' y='25'>
          /{maxPoints}
        </Percentage>
      </svg>
    </Chart>
  )
}

function mapStateToProps(state) {
  const {theme} = state

  return { theme }
}
export default connect(mapStateToProps)(PercentageCircle)
