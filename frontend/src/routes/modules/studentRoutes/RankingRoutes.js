import { Route, Routes } from 'react-router-dom'

import NotFound from '../../../components/general/NotFoundPage/NotFound'
import PageGuard from '../../../components/general/PageGuard/PageGuard'
import StudentsRanking from '../../../components/student/RankPage/StudentsRanking'
import { Role } from '../../../utils/userRole'

export default function RankingRoutes() {
  return (
    <Routes>
      <Route
        path=""
        element={
          <PageGuard role={Role.LOGGED_IN_AS_STUDENT}>
            <StudentsRanking />
          </PageGuard>
        }
      />
      <Route path='*' element={<NotFound />} />
    </Routes>
  )
}
