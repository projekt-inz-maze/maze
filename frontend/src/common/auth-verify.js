import React, { useEffect } from 'react'

import { connect } from 'react-redux'
import { useNavigate } from 'react-router-dom'

import { logout } from '../actions/auth'
import { parseJwt } from '../utils/Api'
import { refreshSessionToast } from '../utils/toasts'

function AuthVerify(props) {
  const navigate = useNavigate()
  const {pathname} = window.location

  useEffect(() => {
    if (props.user && !JSON.parse(localStorage.getItem('user'))) {
      props.dispatch(logout(navigate))
    } else if (props.user) {
      const decodedJwt = parseJwt(props.user.access_token)

      if (decodedJwt.exp * 1000 < Date.now()) {
        props.dispatch(logout(navigate))
      } else if (decodedJwt.exp * 1000 < Date.now() + 15 * 60 * 1000) {
        refreshSessionToast(props.user, props.dispatch, navigate)
      }
    }
  }, [navigate, props, pathname])

  // when one tab is logged out, log out all tabs with application
  useEffect(() => {
    const handleInvalidUser = (event) => {
      if (event.key === 'user' && event.oldValue && !event.newValue) {
        props.dispatch(logout(navigate))
      }
    }

    window.addEventListener('storage', (event) => handleInvalidUser(event))
    return () => window.removeEventListener('storage', (event) => handleInvalidUser(event))
  })

  return <div />
}

function mapStateToProps(state) {
  const { isLoggedIn, user } = state.auth
  return {
    isLoggedIn,
    user
  }
}
export default connect(mapStateToProps)(AuthVerify)
