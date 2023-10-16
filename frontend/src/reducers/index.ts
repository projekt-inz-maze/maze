import { combineReducers } from 'redux'

import authReducer from './auth'
import message from './message'
import sidebar from './sidebar'
import theme from './theme'
import user from './userSlice'
import { api } from '../api/api'

export default combineReducers({
  auth: authReducer,
  message,
  sidebar,
  theme,
  user,
  [api.reducerPath]: api.reducer
})
