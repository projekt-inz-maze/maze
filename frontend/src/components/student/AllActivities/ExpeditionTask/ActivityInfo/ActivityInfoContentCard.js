import React from 'react'

import { Card } from 'react-bootstrap'
import CardHeader from 'react-bootstrap/CardHeader'
import { connect } from 'react-redux'

import { CustomCard } from '../../../GameCardPage/GameCardStyles'

function ActivityInfoContentCard(props) {
  return (
    <CustomCard
      className="p-0"
      $fontColor={props.theme.font}
      $background={props.theme.primary}
      $bodyColor={props.theme.secondary}
    >
      <CardHeader>
        <h5>{props.header}</h5>
      </CardHeader>
      <Card.Body>{props.body}</Card.Body>
    </CustomCard>
  )
}

function mapStateToProps(state) {
  const {theme} = state

  return { theme }
}
export default connect(mapStateToProps)(ActivityInfoContentCard)
