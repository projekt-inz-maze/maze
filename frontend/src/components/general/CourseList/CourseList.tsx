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
import { useGetPersonalityQuery } from '../../../api/apiPersonality'
import { AddCourseRequest, Course, QuizResults } from '../../../api/types'
import CourseCard from '../../../common/components/CourseCard/CourseCard'
import { useAppDispatch } from '../../../hooks/hooks'
import { setCourseId } from '../../../reducers/userSlice'
import { joinGroupRequest } from '../../../services/types/serviceTypes'
import { getGreetingForPersonality } from '../../../utils/formatters'
import { Role } from '../../../utils/userRole'
import PersonalityQuiz from '../../student/PersonalityQuiz/PersonalityQuiz'
import CourseNav from '../Navbars/CourseNavbar/CourseNav'

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
  const [userPersonality, setUserPersonality] = useState<QuizResults>()
  const [showQuiz, setShowQuiz] = useState<boolean>(false)

  const { data: courses, isSuccess: coursesSuccess } = useGetAllCoursesQuery()
  const { data: personality, isSuccess: personalitySuccess } = useGetPersonalityQuery(undefined, {
    skip: role === Role.LOGGED_IN_AS_TEACHER
  })
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

  useEffect(() => {
    if (!personalitySuccess) {
      return
    }
    setUserPersonality(personality)
  }, [personality, personalitySuccess])

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
            {personality?.ACHIEVER || personality?.EXPLORER || personality?.KILLER || personality?.SOCIALIZER ? (
              <p className={styles.greeting}>{getGreetingForPersonality(personality)}</p>
            ) : (
              <p className={styles.greeting}>
                <span>Cześć!</span>
                {role !== Role.LOGGED_IN_AS_TEACHER && (
                  <button type='button' className={styles.actionButton} onClick={() => setShowQuiz(true)}>
                    Poznaj swój typ osobowości!
                  </button>
                )}
              </p>
            )}
            <p className={styles.courseInfo}>Twoje kursy</p>
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
                  <p className='text-white'>
                    <span>No courses found.</span>
                  </p>
                </>
              )}
            </Stack>
          </Row>
        </Col>
      </Container>
      <PersonalityQuiz showModal={showQuiz} setShowModal={setShowQuiz} />
    </div>
  )
}

export default CourseList
