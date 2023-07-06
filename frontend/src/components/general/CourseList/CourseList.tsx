import React from 'react'

import { Container, Stack, Col, Button} from 'react-bootstrap'
import Row from 'react-bootstrap/Row'
import { useNavigate } from 'react-router-dom'

import styles from './CourseList.module.scss'
import CourseCard from '../../../common/components/CourseCard'
import { useAppDispatch } from '../../../hooks/hooks'
import { setCourseId } from '../../../reducers/userSlice'


const CourseList = () => {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()

  const handleClick = (courseId: number) => {
    dispatch(setCourseId(courseId))
    navigate('/game-card')
  }

  return (
    <Container fluid className={styles.mainContainer}>
      <Col>
        <Row className={styles.headerRow}>
          <h1>Cześć, Magda!</h1>
          <h2>Twoje kursy</h2>
        </Row>
        <Row>
          <Col xs={7}>
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
            </Stack>
          </Col>

          <Col className={styles.enrollSection}>
            <p>Aby zapisać się na kurs, podaj kod grupy</p>
            <div className={styles.enterCode}>
              <input type='text' />
              <Button type='submit' className={styles.enrollBtn}>
                Zapisz się
              </Button>
            </div>
          </Col>
        </Row>
      </Col>
    </Container>
  )
}

export default CourseList
