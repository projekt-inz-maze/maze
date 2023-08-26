import { useState } from 'react'

import { Container } from 'react-bootstrap'
import { connect } from 'react-redux'
import { BrowserRouter } from 'react-router-dom'
import './App.css'
import { ToastContainer } from 'react-toastify'

import AuthVerify from '../../common/auth-verify'
import AppRoutes from '../../routes/AppRoutes'
import { professorSubtitles, sidebarTitles, studentSubtitles } from '../../utils/sidebarTitles'
import { isProfessor, isStudent } from '../../utils/storageManager'
import Sidebar from '../general/Sidebar/Sidebar'

function App(props) {
    const [showNavbar, setShowNavbar] = useState(false)
    const student = isStudent(props.user)
    const professor = isProfessor(props.user)

    return (
        <>
            <Container fluid className='p-0'>
                <BrowserRouter>
                    <div className='d-flex flex-column' style={{ minHeight: '100vh', margin: 0 }}>
                        {showNavbar && (
                            <Sidebar sidebarTitles={sidebarTitles}
                                     userSubtitles={student ? studentSubtitles : professorSubtitles} />
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
