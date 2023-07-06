import { combineReducers } from 'redux'

import auth from './auth'
import message from './message'
import sidebar from './sidebar'
import theme from './theme'
import user from './userSlice'

export default combineReducers({
  auth,
  message,
  sidebar,
  theme,
  user
})
