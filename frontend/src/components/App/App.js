import { useState } from 'react'

import { Container } from 'react-bootstrap'
import { connect } from 'react-redux'
import { BrowserRouter } from 'react-router-dom'
import './App.css'
import { ToastContainer } from 'react-toastify'

import { SidebarCol } from './AppGeneralStyles'
import AuthVerify from '../../common/auth-verify'
import AppRoutes from '../../routes/AppRoutes'
import { sidebarExcludedPaths } from '../../utils/constants'
import { ProfessorSidebarTitles, UserSidebarTitles } from '../../utils/sidebarTitles'
import { isProfessor, isStudent } from '../../utils/storageManager'
import Sidebar from '../general/Sidebar/Sidebar'

function App(props) {
  const [showNavbar, setShowNavbar] = useState(true)
  const student = isStudent(props.user)
  const professor = isProfessor(props.user)

  return (
    <>
      <Container fluid className='p-0'>
        <BrowserRouter>
          <div className='d-flex' style={{ minHeight: '100vh', margin: 0 }}>
            {showNavbar && (
              <SidebarCol
                style={{ width: props.sidebar.isExpanded ? 400 : 60 }}
                className={sidebarExcludedPaths.includes(window.location.pathname) ? 'd-none' : 'd-md-block d-none'}
              >
                <Sidebar link_titles={student ? UserSidebarTitles : ProfessorSidebarTitles} />
              </SidebarCol>
            )}
            <div className='p-0 w-100'>
              <AppRoutes showNavbar={setShowNavbar} isStudent={student} isProfessor={professor} />
            </div>
            <AuthVerify />
          </div>
        </BrowserRouter>
      </Container>
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
    </>
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
