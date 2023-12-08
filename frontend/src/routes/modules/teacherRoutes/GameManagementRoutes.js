import { Route, Routes } from 'react-router-dom'

import NotFound from '../../../components/general/NotFoundPage/NotFound'
import PageGuard from '../../../components/general/PageGuard/PageGuard'
import ChapterDetails from '../../../components/professor/ChapterDetails/ChapterDetails'
import ChapterRequirements from '../../../components/professor/ChapterDetails/ChapterRequirements/ChapterRequirements'
import ActivityDetails from '../../../components/professor/GameManagement/ActivityDetails/ActivityDetails'
import GameManagement from '../../../components/professor/GameManagement/GameManagement'
import Logs from '../../../components/professor/GameManagement/Logs/Logs'
import RankAndBadgesManagement from '../../../components/professor/GameManagement/RanksAndBadges/RankAndBadgesManagement'
import Groups from '../../../components/professor/GroupsPage/Groups'
import { Role } from '../../../utils/userRole'

export default function GameManagementRoutes() {
  return (
    <Routes>
      <Route
        path=''
        element={
          <PageGuard role={Role.LOGGED_IN_AS_TEACHER}>
            <GameManagement />
          </PageGuard>
        }
      />

      <Route
        path='groups'
        element={
          <PageGuard role={Role.LOGGED_IN_AS_TEACHER}>
            <Groups />
          </PageGuard>
        }
      />

      <Route
        path='logs'
        element={
          <PageGuard role={Role.LOGGED_IN_AS_TEACHER}>
            <Logs />
          </PageGuard>
        }
      />

      <Route
        path='chapter/:name/:id'
        element={
          <PageGuard role={Role.LOGGED_IN_AS_TEACHER}>
            <ChapterDetails />
          </PageGuard>
        }
      />

      <Route
        path='chapter/:name/:id/requirements'
        element={
          <PageGuard role={Role.LOGGED_IN_AS_TEACHER}>
            <ChapterRequirements />
          </PageGuard>
        }
      />

      <Route
        path='chapter/:name/:id/activity/:activityName'
        element={
          <PageGuard role={Role.LOGGED_IN_AS_TEACHER}>
            <ActivityDetails />
          </PageGuard>
        }
      />

      <Route
        path='ranks-and-badges'
        element={
          <PageGuard role={Role.LOGGED_IN_AS_TEACHER}>
            <RankAndBadgesManagement />
          </PageGuard>
        }
      />

      <Route path='*' element={<NotFound />} />
    </Routes>
  )
}
