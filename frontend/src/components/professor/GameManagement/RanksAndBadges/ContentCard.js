import React from 'react'

import { Card } from 'react-bootstrap'
import CardHeader from 'react-bootstrap/CardHeader'
import { connect } from 'react-redux'

import { isMobileView } from '../../../../utils/mobileHelper'
import { CustomCard } from '../../../student/GameCardPage/GameCardStyles'

function ContentCard(props) {
  const isMobileDisplay = isMobileView()

  return (
    <CustomCard
      $customHeight={isMobileDisplay ? 'auto' : '87vh'}
      $fontColor={props.theme.font}
      $background={props.theme.primary}
      $bodyColor={props.theme.secondary}
    >
      <CardHeader>
        <h5>{props.header}</h5>
      </CardHeader>
      <Card.Body className={`${isMobileDisplay ? 'auto' : 'h-75'}`} style={{ padding: isMobileDisplay ? 0 : 'auto' }}>
        {props.body}
      </Card.Body>
    </CustomCard>
  )
}

function mapStateToProps(state) {
  const {theme} = state

  return { theme }
}
export default connect(mapStateToProps)(ContentCard)
