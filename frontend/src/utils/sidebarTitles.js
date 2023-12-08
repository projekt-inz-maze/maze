import {
  fa5,
  faArrowsToEye,
  faBullseye,
  faCertificate,
  faChessBoard,
  faHouse,
  faListCheck,
  faRankingStar,
  faStar,
  faTerminal,
  faUser,
  faUserGroup,
  faUsers
} from '@fortawesome/free-solid-svg-icons'

import { GeneralRoutes, StudentRoutes, TeacherRoutes } from '../routes/PageRoutes'

export const getUserSidebarTitles = (isStudent) => {
  if (isStudent) {
    return studentSidebarTitles
  }
  return professorSidebarTitles
}

export const studentSidebarTitles = [
  {
    name: 'Kursy',
    navigateTo: GeneralRoutes.COURSE_LIST
  },
  {
    name: 'Ustawienia',
    navigateTo: GeneralRoutes.STUDENT_SETTINGS
  },
  {
    name: 'Wyloguj',
    navigateTo: GeneralRoutes.HOME
  }
]

export const professorSidebarTitles = [
  {
    name: 'Kursy',
    navigateTo: GeneralRoutes.COURSE_LIST
  },
  {
    name: 'Ustawienia',
    navigateTo: GeneralRoutes.PROFESSOR_SETTINGS
  },
  {
    name: 'Wyloguj',
    navigateTo: GeneralRoutes.HOME
  }
]

export const studentSubtitles = [
  {
    name: 'Karta gry',
    icon: faHouse,
    navigateTo: StudentRoutes.GAME_CARD
  },
  {
    name: 'Mapa gry',
    icon: faChessBoard,
    navigateTo: StudentRoutes.GAME_MAP.MAIN
  },
  {
    name: 'Mapa gry (nowa)',
    icon: faChessBoard,
    navigateTo: StudentRoutes.NEW_GAME_MAP.MAIN
  },
  {
    name: 'Punkty',
    icon: faStar,
    navigateTo: StudentRoutes.POINTS
  },
  {
    name: 'Ranking',
    icon: faRankingStar,
    navigateTo: StudentRoutes.RANKING
  },
  {
    name: 'Rangi i odznaki',
    icon: faCertificate,
    navigateTo: StudentRoutes.BADGES
  },
  {
    name: 'Profil',
    icon: faUser,
    navigateTo: StudentRoutes.PROFILE
  }
]

export const professorSubtitles = [
  {
    name: 'Podsumowanie gry',
    icon: faBullseye,
    navigateTo: TeacherRoutes.GAME_SUMMARY
  },
  {
    name: 'Ranking',
    icon: faRankingStar,
    navigateTo: TeacherRoutes.RANKING
  },
  {
    name: 'Zarządzanie grą',
    icon: faListCheck,
    navigateTo: TeacherRoutes.GAME_MANAGEMENT.MAIN,
    subpages: [
      {
        name: 'Grupy',
        icon: faUserGroup,
        navigateTo: TeacherRoutes.GAME_MANAGEMENT.GROUPS
      },
      {
        name: 'Rangi i odznaki',
        icon: faCertificate,
        navigateTo: TeacherRoutes.GAME_MANAGEMENT.RANKS_AND_BADGES
      },
      {
        name: 'Logi',
        icon: faTerminal,
        navigateTo: TeacherRoutes.GAME_MANAGEMENT.LOGS
      }
    ]
  },
  {
    name: 'Uczestnicy',
    icon: faUsers,
    navigateTo: TeacherRoutes.PARTICIPANTS
  },
  {
    name: 'Sprawdzanie aktywności',
    icon: faArrowsToEye,
    navigateTo: TeacherRoutes.ACTIVITY_ASSESSMENT.LIST,
    action: 'BADGE'
  },
  {
    name: 'Oceny',
    icon: fa5,
    navigateTo: TeacherRoutes.GRADES
  }
]
