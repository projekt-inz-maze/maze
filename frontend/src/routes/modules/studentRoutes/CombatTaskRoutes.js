import { Route, Routes } from 'react-router-dom'

import NotFound from '../../../components/general/NotFoundPage/NotFound'
import PageGuard from '../../../components/general/PageGuard/PageGuard'
import CombatTask from '../../../components/student/AllActivities/CombatTask/CombatTask'
import { Role } from '../../../utils/userRole'

export default function CombatTaskRoutes() {
  return (
    <Routes>
      <Route
        path=""
        element={
          <PageGuard role={Role.LOGGED_IN_AS_STUDENT}>
            <CombatTask />
          </PageGuard>
        }
      />

      <Route path='*' element={<NotFound />} />
    </Routes>
  )
}
