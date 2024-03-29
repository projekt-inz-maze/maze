import React from 'react'

import { Route, Routes } from 'react-router-dom'

import CombatTaskRoutes from './CombatTaskRoutes'
import ExpeditionRoutes from './ExpeditionRoutes'
import InformationTaskRoutes from './InformationTaskRoutes'
import SurveyTaskRoutes from './SurveyTaskRoutes'
import NotFound from '../../../components/general/NotFoundPage/NotFound'
import PageGuard from '../../../components/general/PageGuard/PageGuard'
import ChapterMap from '../../../components/student/ChapterMap/ChapterMap'
import QuestBoard from '../../../components/student/Quests/QuestBoard/QuestBoard'
import { Role } from '../../../utils/userRole'

export default function MapRoutes() {
  return (
    <Routes>
      <Route
        path=''
        element={
          <PageGuard role={Role.LOGGED_IN_AS_STUDENT}>
            <ChapterMap />
          </PageGuard>
        }
      />

      <Route path={'quests/*'} element={<QuestBoard />} />

      <Route path={'combat-task/*'} element={<CombatTaskRoutes />} />

      <Route path={'survey-task/*'} element={<SurveyTaskRoutes />} />

      <Route path={'information/*'} element={<InformationTaskRoutes />} />

      <Route path={'expedition/*'} element={<ExpeditionRoutes />} />

      <Route path='*' element={<NotFound />} />
    </Routes>
  )
}
