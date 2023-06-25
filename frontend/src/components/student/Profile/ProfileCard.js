import React from 'react'

import { Button, Card } from 'react-bootstrap'
import CardHeader from 'react-bootstrap/CardHeader'
import { connect } from 'react-redux'

import { CustomCard } from '../GameCardPage/GameCardStyles'

function ProfileCard(props) {
  const buttonColor = props.customButton ?? props.theme.warning
  return (
    <CustomCard $fontColor={props.theme.font} $background={props.theme.primary} $bodyColor={props.theme.secondary}>
      <CardHeader>
        <h5>{props.header}</h5>
      </CardHeader>
      <Card.Body className="d-flex align-items-center justify-content-center flex-column">
        <>{props.body}</>
        {props.showButton && (
          <Button style={{ backgroundColor: buttonColor, borderColor: buttonColor }} onClick={props.buttonCallback}>
            {props.buttonText ?? <span>Przejdź</span>}
          </Button>
        )}
      </Card.Body>
    </CustomCard>
  )
}
function mapStateToProps(state) {
  const {theme} = state

  return { theme }
}
export default connect(mapStateToProps)(ProfileCard)
