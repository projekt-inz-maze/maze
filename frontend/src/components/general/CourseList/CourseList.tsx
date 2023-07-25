import React, { useEffect, useState } from 'react'

import { Col, Container, Stack } from 'react-bootstrap'
import Row from 'react-bootstrap/Row'
import { useNavigate } from 'react-router-dom'

import styles from './CourseList.module.scss'
import { useGetAllCoursesQuery } from '../../../api/apiCourses'
import { Course } from '../../../api/types'
import CourseCard from '../../../common/components/CourseCard'
import { useAppDispatch } from '../../../hooks/hooks'
import { setCourseId } from '../../../reducers/userSlice'
import { Role } from '../../../utils/userRole'
import CourseNav from '../CourseNav/CourseNav'

const CourseList = ({ showNavbar, isStudent, isProfessor }: any) => {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()
  const role = isStudent ? Role.LOGGED_IN_AS_STUDENT : Role.LOGGED_IN_AS_TEACHER

  const [coursesList, setCoursesList] = useState<Course[]>([])

  const { data: courses, isSuccess: coursesSuccess } = useGetAllCoursesQuery()

  useEffect(() => {
    showNavbar(false)
    return () => {
      showNavbar(true)
    }
  }, [])

  useEffect(() => {
    if (!coursesSuccess) {
      return
    }
    setCoursesList(courses)
    console.log(courses, coursesList)
  }, [courses, coursesSuccess])

  const handleClick = (courseId: number) => {
    dispatch(setCourseId(courseId))
    if (isStudent) {
      navigate('/game-card')
    } else if (isProfessor) {
      navigate('/game-summary')
    }
  }

  const stackConfig = [
    { title: 'Sieci komputerowe', description: 'Przedmiot na 5 semestrze studiów informatycznych.', id: 1 },
    {
      title: 'Bezpieczeństwo sieci komputerowych',
      description: 'Przedmiot na 6 semestrze studiów informatycznych.',
      id: 2
    },
    { title: 'Wirtualne sieci prywatne', description: 'Przedmiot na 7 semestrze studiów informatycznych.', id: 3 },
    { title: 'Projektowanie obiektowe', description: 'Przedmiot na 4 semestrze studiów informatycznych.', id: 4 },
    { title: 'Podstawy baz danych', description: 'Przedmiot na 3 semestrze studiów informatycznych.', id: 5 },
    {
      title: 'Wprowadzenie do aplikacji internetowych',
      description: 'Przedmiot na 3 semestrze studiów informatycznych.',
      id: 6
    },
    { title: 'Inżynieria oprogramowania', description: 'Przedmiot na 6 semestrze studiów informatycznych.', id: 7 }
    // Add more course configurations as needed
  ]

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
              {stackConfig.map((course) => (
                <CourseCard
                  key={course.id}
                  title={course.title}
                  description={course.description}
                  onEnterCourse={() => handleClick(course.id)}
                />
              ))}
            </Stack>
          </Row>
        </Col>
      </Container>
    </div>
  )
}

export default CourseList
