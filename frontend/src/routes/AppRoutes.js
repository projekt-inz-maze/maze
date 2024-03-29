import { Route, Routes } from 'react-router-dom'

import BadgesAndAchievementsRoutes from './modules/studentRoutes/BadgesAndAchievementsRoutes'
import GameCardRoutes from './modules/studentRoutes/GameCardRoutes'
import GameMapRoutes from './modules/studentRoutes/GameMapRoutes'
import MapRoutes from './modules/studentRoutes/MapRoutes'
import PointsRoutes from './modules/studentRoutes/PointsRoutes'
import ProfileRoutes from './modules/studentRoutes/ProfileRoutes'
import StudentRankingRoutes from './modules/studentRoutes/RankingRoutes'
import ActivityAssessmentRoutes from './modules/teacherRoutes/ActivityAssessmentRoutes'
import GameManagementRoutes from './modules/teacherRoutes/GameManagementRoutes'
import GameSummaryRoutes from './modules/teacherRoutes/GameSummaryRoutes'
import GradesRoutes from './modules/teacherRoutes/GradesRoutes'
import ParticipantsRoutes from './modules/teacherRoutes/ParticipantsRoutes'
import TeacherRankingRoutes from './modules/teacherRoutes/RankingRoutes'
import SettingsRoutes from './modules/teacherRoutes/SettingsRoutes'
import CourseList from '../components/general/CourseList/CourseList.tsx'
import LoginAndRegistration from '../components/general/LoginAndRegistrationPage/LoginAndRegistration'
import ResetPassword from '../components/general/LoginAndRegistrationPage/ResetPassword/ResetPassword'
import NotFound from '../components/general/NotFoundPage/NotFound'
import PageGuard from '../components/general/PageGuard/PageGuard'
import CanvasMap from '../components/student/CanvasMapPage/CanvasMap'
import PersonalityQuizResults from '../components/student/PersonalityQuiz/PersonalityQuizResults/PersonalityQuizResults'
import { Role } from '../utils/userRole'

export default function AppRoutes({ showNavbar, isStudent, isProfessor }) {
  return (
    <Routes>
      <Route
        path='/'
        exact
        element={
          <PageGuard role={Role.NOT_LOGGED_IN}>
            <LoginAndRegistration />
          </PageGuard>
        }
      />

      <Route
        path='/password-reset'
        exact
        element={
          <PageGuard role={Role.NOT_LOGGED_IN}>
            <ResetPassword />
          </PageGuard>
        }
      />

      <Route
        path='/canvas'
        exact
        element={
          <PageGuard role={Role.LOGGED_IN_AS_STUDENT}>
            <CanvasMap />
          </PageGuard>
        }
      />

      <Route
        path='/quiz-result'
        exact
        element={
          <PageGuard role={Role.LOGGED_IN_AS_STUDENT}>
            <PersonalityQuizResults />
          </PageGuard>
        }
      />

      <Route
        path={'/courses/*'}
        element={<CourseList showNavbar={showNavbar} isStudent={isStudent} isProfessor={isProfessor} />}
      />

      <Route path={'/game-card/*'} element={<GameCardRoutes />} />

      <Route path={'/game-map/*'} element={<GameMapRoutes />} />

      <Route path={'/map/*'} element={<MapRoutes />} />

      <Route path={'/points/*'} element={<PointsRoutes />} />

      <Route path={'/ranking/*'} element={<StudentRankingRoutes />} />

      <Route path={'/badges-achievements/*'} element={<BadgesAndAchievementsRoutes />} />

      <Route path={'/profile/*'} element={<ProfileRoutes />} />

      <Route path={'/game-summary/*'} element={<GameSummaryRoutes />} />

      <Route path={'/students-ranking/*'} element={<TeacherRankingRoutes />} />

      <Route path={'/game-management/*'} element={<GameManagementRoutes />} />

      <Route path={'/participants/*'} element={<ParticipantsRoutes />} />

      <Route path={'/assessment/*'} element={<ActivityAssessmentRoutes />} />

      <Route path={'/grades/*'} element={<GradesRoutes />} />

      <Route path={'/settings/*'} element={<SettingsRoutes />} />

      <Route path='*' element={<NotFound />} />
    </Routes>
  )
}
