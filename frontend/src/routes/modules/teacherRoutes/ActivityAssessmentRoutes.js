import { Route, Routes } from 'react-router-dom'

import NotFound from '../../../components/general/NotFoundPage/NotFound'
import PageGuard from '../../../components/general/PageGuard/PageGuard'
import ActivityAssessmentDetails from '../../../components/professor/ActivityAssessmentDetails/ActivityAssessmentDetails'
import ActivityAssessmentList from '../../../components/professor/ActivityAssessmentList/ActivityAssessmentList'
import { Role } from '../../../utils/userRole'

export default function ActivityAssessmentRoutes() {
  return (
    <Routes>
      <Route
        path="list"
        element={
          <PageGuard role={Role.LOGGED_IN_AS_TEACHER}>
            <ActivityAssessmentList />
          </PageGuard>
        }
      />

      <Route
        path="activity-assessment"
        element={
          <PageGuard role={Role.LOGGED_IN_AS_TEACHER}>
            <ActivityAssessmentDetails />
          </PageGuard>
        }
      />

      <Route path='*' element={<NotFound />} />
    </Routes>
  )
}
