import { Route, Routes } from 'react-router-dom'

import NotFound from '../../../components/general/NotFoundPage/NotFound'
import PageGuard from '../../../components/general/PageGuard/PageGuard'
import Profile from '../../../components/student/Profile/Profile'
import { Role } from '../../../utils/userRole'

export default function ProfileRoutes() {
  return (
    <Routes>
      <Route
        path=""
        element={
          <PageGuard role={Role.LOGGED_IN_AS_STUDENT}>
            <Profile />
          </PageGuard>
        }
      />
      <Route path='*' element={<NotFound />} />
    </Routes>
  )
}
