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
import { isStudent } from '../../utils/storageManager'
import MobileNavbar from '../general/Sidebar/MobileNavbar'
import Sidebar from '../general/Sidebar/Sidebar'

function App(props) {
  const student = isStudent(props.user)

  return (
    <>
      <Container fluid className='p-0'>
        <div className="d-flex" style={{ minHeight: '100vh', margin: 0 }}>
          <BrowserRouter>
            { window.location.pathname !== '/courses' && (
              <SidebarCol
                style={{ width: props.sidebar.isExpanded ? 400 : 60 }}
                className={sidebarExcludedPaths.includes(window.location.pathname) ? 'd-none' : 'd-md-block d-none'}
              >
                <Sidebar link_titles={student ? UserSidebarTitles : ProfessorSidebarTitles} />
              </SidebarCol>
            )}
            <div className="p-0 w-100">
              <AppRoutes />
            </div>
            { window.location.pathname !== '/courses' && (
              <SidebarCol
                className={sidebarExcludedPaths.includes(window.location.pathname) ? 'd-none' : 'd-md-none d-block'}
              >
                <MobileNavbar link_titles={student ? UserSidebarTitles : ProfessorSidebarTitles} />
              </SidebarCol>
            )}
            <AuthVerify />
          </BrowserRouter>
        </div>
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
        theme="colored"
      />
    </>
  )
}

function mapStateToProps(state) {
  const { user } = state.auth
  const {sidebar} = state
  return {
    user,
    sidebar
  }
}
export default connect(mapStateToProps)(App)
