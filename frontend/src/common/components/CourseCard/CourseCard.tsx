import React from 'react'

import { Card, Dropdown } from 'react-bootstrap'

import 'holderjs'
import style from './CourseCard.module.scss'
import { Course } from '../../../api/types'
import { Role } from '../../../utils/userRole'
import CustomDropdownToggle from '../CustomDropdownToggle/CustomDropdownToggle'

const randomImageURLs = [
  'https://cdn.pixabay.com/photo/2023/07/17/13/50/baby-snow-leopard-8132690_960_720.jpg',
  'https://cdn.pixabay.com/photo/2017/08/30/01/05/milky-way-2695569_960_720.jpg',
  'https://cdn.pixabay.com/photo/2018/08/21/23/29/forest-3622519_640.jpg',
  'https://cdn.pixabay.com/photo/2018/09/03/23/56/sea-3652697_640.jpg'
]

const getRandomImageURL = () => {
  const randomIndex = Math.floor(Math.random() * randomImageURLs.length)
  return randomImageURLs[randomIndex]
}

type CourseCardProps = {
  course: Course
  role: number
  onEnterCourse: () => void
  onDeleteCourse: (id: number) => void
}

const CourseCard = (props: CourseCardProps) => {
  const cardImg = getRandomImageURL()
  return (
    <>
      <Card style={{ width: '14rem' }} className={style.mainCard}>
        <Card.Img variant='top' src={cardImg} onClick={props.onEnterCourse} className={style.clickableImg} />
        {props.role === Role.LOGGED_IN_AS_TEACHER ? (
          <Dropdown className={style.menu}>
            <Dropdown.Toggle as={CustomDropdownToggle} id='course-menu' />
            <Dropdown.Menu className={style.dropdownMenu}>
              <Dropdown.Item onClick={() => props.onDeleteCourse(props.course.id)}>Usuń kurs</Dropdown.Item>
            </Dropdown.Menu>
          </Dropdown>
        ) : (
          <></>
        )}
        <Card.Body className={style.clickable}>
          <Card.Title onClick={props.onEnterCourse}>{props.course.name}</Card.Title>
          <Card.Text>{props.course.description}</Card.Text>
        </Card.Body>
      </Card>
    </>
  )
}

export default CourseCard
