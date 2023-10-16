import { Route, Routes } from 'react-router-dom'

import NotFound from '../../../components/general/NotFoundPage/NotFound'
import PageGuard from '../../../components/general/PageGuard/PageGuard'
import ActivityInfo from '../../../components/student/AllActivities/ExpeditionTask/ActivityInfo/ActivityInfo'
import ExpeditionSummary from '../../../components/student/AllActivities/ExpeditionTask/ExpeditionSummary/ExpeditionSummary'
import { ExpeditionWrapper } from '../../../components/student/AllActivities/ExpeditionTask/ExpeditionWrapper/ExpeditionWrapper'
import { Role } from '../../../utils/userRole'

export default function ExpeditionRoutes() {
  return (
    <Routes>
      <Route
        path="summary"
        element={
          <PageGuard role={Role.LOGGED_IN_AS_STUDENT}>
            <ExpeditionSummary />
          </PageGuard>
        }
      />

      <Route
        path="info"
        element={
          <PageGuard role={Role.LOGGED_IN_AS_STUDENT}>
            <ActivityInfo />
          </PageGuard>
        }
      />

      <Route
        path="test"
        element={
          <PageGuard role={Role.LOGGED_IN_AS_STUDENT}>
            <ExpeditionWrapper />
          </PageGuard>
        }
      />

      <Route path='*' element={<NotFound />} />
    </Routes>
  )
}
