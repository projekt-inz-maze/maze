import moment from 'moment'

import { parseJwt } from './Api'
import { AccountType } from './userRole'

export const isStudent = (user) => (user ? parseJwt(user.access_token).roles.includes(AccountType.STUDENT) : false)

export const getTimer = (remainingTime) => moment.utc(remainingTime * 1000).format('HH:mm:ss')
