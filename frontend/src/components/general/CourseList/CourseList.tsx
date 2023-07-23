import React, { useEffect } from 'react'

import { Col, Container, Stack } from 'react-bootstrap'
import Row from 'react-bootstrap/Row'
import { useNavigate } from 'react-router-dom'

import styles from './CourseList.module.scss'
import CourseCard from '../../../common/components/CourseCard'
import { useAppDispatch } from '../../../hooks/hooks'
import { setCourseId } from '../../../reducers/userSlice'
import { Role } from '../../../utils/userRole'
import CourseNav from '../CourseNav/CourseNav'

const CourseList = ({ showNavbar, isStudent, isProfessor }: any) => {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()
  const role = isStudent ? Role.LOGGED_IN_AS_STUDENT : Role.LOGGED_IN_AS_TEACHER

  useEffect(() => {
    showNavbar(false)
    return () => {
      showNavbar(true)
    }
  }, [])

  const handleClick = (courseId: number) => {
    dispatch(setCourseId(courseId))
    if (isStudent) {
      navigate('/game-card')
    } else if (isProfessor) {
      navigate('/game-summary')
    }
  }

  return (
    <div className={styles.color}>
      <CourseNav userRole={role} />
      <Container className={styles.mainContainer}>
        <Col>
          <Row className={styles.headerRow}>
            <h1>Cześć!</h1>
            <h2>Twoje kursy</h2>
          </Row>

          <Row>
            <Stack className={styles.stackContainer} direction='horizontal' gap={3}>
              <CourseCard
                title='Sieci komputerowe'
                description='Przedmiot na 5 semestrze studiów informatycznych.'
                onEnterCourse={() => handleClick(1)}
              />
              <CourseCard
                title='Bezpieczeństwo sieci komputerowych'
                description='Przedmiot na 6 semestrze studiów informatycznych.'
                onEnterCourse={() => handleClick(2)}
              />
              <CourseCard
                title='Wirtualne sieci prywatne'
                description='Przedmiot na 7 semestrze studiów informatycznych.'
                onEnterCourse={() => handleClick(3)}
              />
              <CourseCard
                title='Wirtualne sieci prywatne'
                description='Przedmiot na 7 semestrze studiów informatycznych.'
                onEnterCourse={() => handleClick(4)}
              />
              <CourseCard
                title='Wirtualne sieci prywatne'
                description='Przedmiot na 7 semestrze studiów informatycznych.'
                onEnterCourse={() => handleClick(5)}
              />
              <CourseCard
                title='Wirtualne sieci prywatne'
                description='Przedmiot na 7 semestrze studiów informatycznych.'
                onEnterCourse={() => handleClick(6)}
              />
              <CourseCard
                title='Wirtualne sieci prywatne'
                description='Przedmiot na 7 semestrze studiów informatycznych.'
                onEnterCourse={() => handleClick(7)}
              />
              <CourseCard
                title='Wirtualne sieci prywatne'
                description='Przedmiot na 7 semestrze studiów informatycznych.'
                onEnterCourse={() => handleClick(8)}
              />
              <CourseCard
                title='Wirtualne sieci prywatne'
                description='Przedmiot na 7 semestrze studiów informatycznych.'
                onEnterCourse={() => handleClick(9)}
              />
              <CourseCard
                title='Wirtualne sieci prywatne'
                description='Przedmiot na 7 semestrze studiów informatycznych.'
                onEnterCourse={() => handleClick(10)}
              />
            </Stack>
          </Row>
        </Col>
      </Container>
    </div>
  )
}

export default CourseList
