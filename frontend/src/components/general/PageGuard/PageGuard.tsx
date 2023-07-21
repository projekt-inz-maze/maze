import React, { useEffect } from 'react'

import { connect } from 'react-redux'
import { useNavigate } from 'react-router-dom'

import { GeneralRoutes } from '../../../routes/PageRoutes'
import { isStudent } from '../../../utils/storageManager'
import { Role } from '../../../utils/userRole'

function PageGuard(props: any) {
  const navigate = useNavigate()
  const { isLoggedIn } = props
  const student = isStudent(props.user)
  const { role } = props
  const child = props.children

  useEffect(() => {
    if (!isLoggedIn && role !== Role.NOT_LOGGED_IN) {
      navigate(GeneralRoutes.HOME)
      window.location.reload() // without it, sidebar not reload after redirect
    } else if ((role === Role.LOGGED_IN_AS_STUDENT || (role === Role.NOT_LOGGED_IN && isLoggedIn)) && !student) {
      // navigate(TeacherRoutes.GAME_SUMMARY)
      navigate(GeneralRoutes.COURSE_LIST)
      window.location.reload() // without it, sidebar not reload after redirect
    } else if ((role === Role.LOGGED_IN_AS_TEACHER || (role === Role.NOT_LOGGED_IN && isLoggedIn)) && student) {
      navigate(GeneralRoutes.COURSE_LIST)
      window.location.reload() // without it, sidebar not reload after redirect
    }
  }, [navigate, role, isLoggedIn, student])

  return <>{child}</>
}

function mapStateToProps(state: any) {
  const { isLoggedIn, user } = state.auth
  return {
    isLoggedIn,
    user
  }
}

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
export default connect(mapStateToProps)(PageGuard)
