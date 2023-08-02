import React, { useEffect, useState } from 'react'

import { Button, Col, Container, Stack } from 'react-bootstrap'
import Row from 'react-bootstrap/Row'
import { useNavigate } from 'react-router-dom'

import styles from './CourseList.module.scss'
import { useAddNewCourseMutation, useDeleteCourseMutation, useGetAllCoursesQuery } from '../../../api/apiCourses'
import { Course } from '../../../api/types'
import CourseCard from '../../../common/components/CourseCard/CourseCard'
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
  const [addNewCourse] = useAddNewCourseMutation()
  const [deleteCourse] = useDeleteCourseMutation()

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
  }, [courses, coursesSuccess])

  const handleClick = (courseId: number) => {
    dispatch(setCourseId(courseId))
    if (isStudent) {
      navigate('/game-card')
    } else if (isProfessor) {
      navigate('/game-summary')
    }
  }

  const handleAddNewCourse = async (name: string, description: string) => {
    await addNewCourse({ name, description }).unwrap()
  }

  const handleDeleteCourse = async (courseId: number) => {
    await deleteCourse(courseId).unwrap()
  }

  return (
    <div className={styles.color}>
      <CourseNav userRole={role} onAddCourse={handleAddNewCourse} />
      <Container className={styles.mainContainer}>
        <Col>
          <Row className={styles.headerRow}>
            <h1>Cześć!</h1>
            <h2>Twoje kursy</h2>
          </Row>

          <Row>
            <Stack className={styles.stackContainer} direction='horizontal' gap={3}>
              {coursesList.length > 0 ? (
                coursesList.map((course) => (
                  <CourseCard
                    key={course.id}
                    course={course}
                    role={role}
                    onEnterCourse={() => handleClick(course.id)}
                    onDeleteCourse={() => handleDeleteCourse(course.id)}
                  />
                ))
              ) : (
                <>
                  <p className='text-white'>No courses found. Displaying mock card</p>
                  <CourseCard
                    key={1}
                    course={{ id: 1, name: 'Mock course', description: 'Mock description' }}
                    role={role}
                    onEnterCourse={() => handleClick(1)}
                    onDeleteCourse={() => handleDeleteCourse(1)}
                  />
                </>
              )}
            </Stack>
          </Row>
        </Col>
      </Container>
    </div>
  )
}

export default CourseList
