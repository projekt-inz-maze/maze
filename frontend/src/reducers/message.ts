import { CLEAR_MESSAGE, SET_MESSAGE } from '../actions/types'

const initialState = {}

export default function getMessage(state = initialState, action: any) {
  const { type, payload } = action
  switch (type) {
    case SET_MESSAGE:
      return { message: payload }
    case CLEAR_MESSAGE:
      return { message: '' }
    default:
      return state
  }
}
