import { useState } from 'react'

import { connect } from 'react-redux'
import { BrowserRouter } from 'react-router-dom'
import './App.css'
import { ToastContainer } from 'react-toastify'

import AuthVerify from '../../common/auth-verify'
import AppRoutes from '../../routes/AppRoutes'
import { professorSubtitles, sidebarTitles, studentSubtitles } from '../../utils/sidebarTitles'
import { isProfessor, isStudent } from '../../utils/storageManager'
import TopNavbar from '../general/Navbar/TopNavbar'

function App(props) {
  const [showNavbar, setShowNavbar] = useState(false)
  const student = isStudent(props.user)
  const professor = isProfessor(props.user)

  return (
    <div style={{ height: '100%' }}>
      <div className='p-0 w-100' style={{ minHeight: '100%' }}>
        <BrowserRouter>
          {showNavbar && (
            <div style={{ height: '11vh' }}>
              <TopNavbar
                sidebarTitles={sidebarTitles}
                userSubtitles={student ? studentSubtitles : professorSubtitles}
              />
            </div>
          )}
          <div className='m-0 p-0' style={{ height: '89vh' }}>
            <AppRoutes showNavbar={setShowNavbar} isStudent={student} isProfessor={professor} />
          </div>
          <AuthVerify />
          {/* </div> */}
        </BrowserRouter>
      </div>
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

function mapStateToProps(state) {
  const { user } = state.auth
  const { sidebar } = state
  return {
    user,
    sidebar
  }
}

export default connect(mapStateToProps)(App)
