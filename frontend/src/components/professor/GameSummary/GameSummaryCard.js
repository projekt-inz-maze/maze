import React from 'react'

import { Card } from 'react-bootstrap'
import CardHeader from 'react-bootstrap/CardHeader'
import { connect } from 'react-redux'

import { CustomCard } from '../../student/GameCardPage/GameCardStyles'

function GameSummaryCard(props) {
  return (
    <CustomCard $fontColor={props.theme.font} $background={props.theme.primary} $bodyColor={props.theme.secondary}>
      <CardHeader>{props.header}</CardHeader>
      <Card.Body>{props.body}</Card.Body>
    </CustomCard>
  )
}

function mapStateToProps(state) {
  const {theme} = state

  return { theme }
}
export default connect(mapStateToProps)(GameSummaryCard)
