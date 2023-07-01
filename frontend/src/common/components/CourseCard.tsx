import React from 'react'

import { Card } from 'react-bootstrap'

import 'holderjs'
import style from './CourseCard.module.scss'

type CourseCardProps = {
  title: string
  description: string
  onEnterCourse: () => void
}

const CourseCard = (props: CourseCardProps) => (
    <Card style={{ width: '14rem'}} className={style.mainCard}>
      <Card.Img variant="top" src="holder.js/100px150" onClick={props.onEnterCourse} className={style.clickable} />
      <Card.Body className={style.clickable}>
        <Card.Title onClick={props.onEnterCourse} >{props.title}</Card.Title>
        <Card.Text>
          {props.description}
        </Card.Text>
      </Card.Body>
    </Card>
  )

export default CourseCard
