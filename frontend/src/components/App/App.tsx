import { useState } from 'react'

import { connect } from 'react-redux'
import { BrowserRouter } from 'react-router-dom'
import { ToastContainer } from 'react-toastify'

import styles from './App.module.scss'
import AuthVerify from '../../common/auth-verify'
import AppRoutes from '../../routes/AppRoutes'
import { professorSubtitles, sidebarTitles, studentSubtitles } from '../../utils/sidebarTitles'
import { isProfessor, isStudent } from '../../utils/storageManager'
import Navbar from '../general/Navbars/Navbar'

function App(props: any) {
  const [showNavbar, setShowNavbar] = useState(false)
  const student = isStudent(props.user)
  const professor = isProfessor(props.user)

  return (
    <div style={{ height: '100%' }}>
      <BrowserRouter>
        <div className={styles.appContainer}>
          {showNavbar && (
            <Navbar navbarTitles={sidebarTitles} userSubtitles={student ? studentSubtitles : professorSubtitles} />
          )}
          <div className={styles.content}>
            <AppRoutes showNavbar={setShowNavbar} isStudent={student} isProfessor={professor} />
          </div>
          <AuthVerify />
        </div>
      </BrowserRouter>
      <ToastContainer
        position='top-right'
        autoClose={4000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme='colored'
      />
    </div>
  )
}

function mapStateToProps(state: any) {
  const { user } = state.auth
  const { sidebar } = state
  return {
    user,
    sidebar
  }
}

export default connect(mapStateToProps)(App)
