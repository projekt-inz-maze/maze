import { Route, Routes } from 'react-router-dom'

import NotFound from '../../../components/general/NotFoundPage/NotFound'
import PageGuard from '../../../components/general/PageGuard/PageGuard'
import ProfessorSettings from '../../../components/professor/ProfessorSettings/ProfessorSettings'
import StudentSettings from '../../../components/student/StudentSettings/StudentSettings'
import { Role } from '../../../utils/userRole'

export default function SettingsRoutes() {
  return (
    <Routes>
      <Route
        path='/professor'
        element={
          <PageGuard role={Role.LOGGED_IN_AS_TEACHER}>
            <ProfessorSettings />
          </PageGuard>
        }
      />

      <Route
        path='/student'
        element={
          <PageGuard role={Role.LOGGED_IN_AS_STUDENT}>
            <StudentSettings />
          </PageGuard>
        }
      />
      <Route path='*' element={<NotFound />} />
    </Routes>
  )
}
