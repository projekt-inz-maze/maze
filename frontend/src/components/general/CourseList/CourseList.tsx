import React, { useEffect, useState } from 'react'

import { Col, Container, Stack } from 'react-bootstrap'
import Row from 'react-bootstrap/Row'
import { useNavigate } from 'react-router-dom'

import styles from './CourseList.module.scss'
import {
  useAddNewCourseMutation,
  useDeleteCourseMutation,
  useGetAllCoursesQuery,
  useJoinCourseGroupMutation
} from '../../../api/apiCourses'
import { AddCourseRequest, Course } from '../../../api/types'
import CourseCard from '../../../common/components/CourseCard/CourseCard'
import { useAppDispatch } from '../../../hooks/hooks'
import { setCourseId } from '../../../reducers/userSlice'
import { joinGroupRequest } from '../../../services/types/serviceTypes'
import { Role } from '../../../utils/userRole'
import CourseNav from '../CourseNav/CourseNav'

type CourseListProps = {
  showNavbar: (show: boolean) => void
  isStudent: boolean
  isProfessor: boolean
}

const CourseList = (props: CourseListProps) => {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()
  const role = props.isStudent ? Role.LOGGED_IN_AS_STUDENT : Role.LOGGED_IN_AS_TEACHER

  const [coursesList, setCoursesList] = useState<Course[]>([])

  const { data: courses, isSuccess: coursesSuccess } = useGetAllCoursesQuery()
  const [addNewCourse] = useAddNewCourseMutation()
  const [deleteCourse] = useDeleteCourseMutation()
  const [joinCourseGroup] = useJoinCourseGroupMutation()

  useEffect(() => {
    props.showNavbar(false)
    return () => {
      props.showNavbar(true)
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
    if (props.isStudent) {
      navigate('/game-card')
    } else if (props.isProfessor) {
      navigate('/game-summary')
    }
  }

  const handleAddNewCourse = async (requestBody: AddCourseRequest) => {
    await addNewCourse(requestBody).unwrap()
  }

  const handleDeleteCourse = async (courseId: number) => {
    await deleteCourse(courseId).unwrap()
  }

  const handleJoinCourseGroup = async (requestBody: joinGroupRequest) => {
    await joinCourseGroup(requestBody).unwrap()
  }

  return (
    <div className={styles.color}>
      <CourseNav userRole={role} onAddCourse={handleAddNewCourse} onJoinCourse={handleJoinCourseGroup} />
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
