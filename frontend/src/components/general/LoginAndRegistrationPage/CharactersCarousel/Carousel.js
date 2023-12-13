import React from 'react'

import { Col } from 'react-bootstrap'
import { connect } from 'react-redux'

import { CarouselItem, CustomCarousel } from './CarouselStyle'
import wizard from '../../../../utils/resources/newHeroes/mage.png'
import priest from '../../../../utils/resources/newHeroes/priest.png'
import rogue from '../../../../utils/resources/newHeroes/rouge.png'
import warrior from '../../../../utils/resources/newHeroes/warrior.png'
// import gameMapExample from '../../../../storage/resources/game_example.png'

function Carousel(props) {
  const images = [warrior, wizard, priest, rogue] // TODO: use list of images

  const ItemsList = images.map((image, index) => (
    <CarouselItem key={index}>
      <img src={image} alt={`slide ${index}`} height='100%' />
    </CarouselItem>
  ))

  return (
    <Col xs={6} className='p-0 d-none d-md-block'>
      <CustomCarousel $background={props.theme.primary} controls={false} autoPlay interval={2500} pause={false}>
        {ItemsList}
      </CustomCarousel>
    </Col>
  )
}

function mapStateToProps(state) {
  const { theme } = state

  return { theme }
}
export default connect(mapStateToProps)(Carousel)
