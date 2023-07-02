import { Route, Routes } from 'react-router-dom'

import NotFound from '../../../components/general/NotFoundPage/NotFound'
import PageGuard from '../../../components/general/PageGuard/PageGuard'
import GameCardView from '../../../components/student/GameCardPage/GameCardView'
import { Role } from '../../../utils/userRole'

export default function GameCardRoutes() {
  return (
    <Routes>
      <Route
        path=""
        element={
          <PageGuard role={Role.LOGGED_IN_AS_STUDENT}>
            <GameCardView />
          </PageGuard>
        }
      />

      <Route path='*' element={<NotFound />} />
    </Routes>
  )
}
