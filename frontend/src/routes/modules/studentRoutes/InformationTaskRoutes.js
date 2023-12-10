import { Route, Routes } from 'react-router-dom'

import NotFound from '../../../components/general/NotFoundPage/NotFound'
import PageGuard from '../../../components/general/PageGuard/PageGuard'
import InfoTask from '../../../components/student/AllActivities/InfoTask/InfoTask'
import { Role } from '../../../utils/userRole'

export default function InformationTaskRoutes() {
  return (
    <Routes>
      <Route
        path=''
        element={
          <PageGuard role={Role.LOGGED_IN_AS_STUDENT}>
            {/* <Information /> */}
            <InfoTask />
          </PageGuard>
        }
      />

      <Route path='*' element={<NotFound />} />
    </Routes>
  )
}
