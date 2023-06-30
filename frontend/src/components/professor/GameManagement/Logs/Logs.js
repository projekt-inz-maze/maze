import React from 'react'

import LogViewer from './LogViewer'
import { TeacherRoutes } from '../../../../routes/PageRoutes'
import GoBackButton from '../../../general/GoBackButton/GoBackButton'

function Logs() {
  return (
    <div>
      <h2 className="text-center">Lista logów serwera</h2>
      <LogViewer />
      <GoBackButton goTo={TeacherRoutes.GAME_MANAGEMENT.MAIN} />
    </div>
  )
}

export default Logs
