import {
  ADD_BADGE,
  DELETE_BADGE,
  GET_BADGE_ALL,
  GET_BADGE_UNLOCKED_ALL,
  GET_PASSWORD_RESET_EMAIL, GET_USER_CURRENT,
  PUT_BADGE_UPDATE,
  PUT_PASSWORD_RESET
} from './urls'
import { parseJwt } from '../utils/Api'
import { axiosApiDelete, axiosApiGet, axiosApiMultipartPost, axiosApiMultipartPut, axiosApiPut } from '../utils/axios'

class UserService {
  getUser() {
    const user = localStorage.getItem('user')
    return user ? JSON.parse(user) : null
  }

  getEmail() {
    return parseJwt(this.getUser().access_token).sub
  }

  getUserData() {
    return axiosApiGet(GET_USER_CURRENT).catch((error) => {
      throw error
    })
  }

  getAllBadges(courseId: number) {
    return axiosApiGet(`${GET_BADGE_ALL}?courseId=${courseId}`).catch((error) => {
      throw error
    })
  }

  getUnlockedBadges(courseId: number) {
    return axiosApiGet(`${GET_BADGE_UNLOCKED_ALL}?courseId=${courseId}`).catch((error) => {
      throw error
    })
  }

  deleteBadge(badgeId: number) {
    return axiosApiDelete(DELETE_BADGE, { badgeId }).catch((error) => {
      throw error
    })
  }

  addBadge(title: string, description: string, image: any, value: string, forGroup: boolean, type: any) {
    return axiosApiMultipartPost(ADD_BADGE, {
      title,
      description,
      image,
      value,
      forGroup,
      type
    }).catch((error) => {
      throw error
    })
  }

  editBadge(title: string, description: string, image: any, value: string, forGroup: boolean, id: any) {
    return axiosApiMultipartPut(PUT_BADGE_UPDATE, {
      title,
      description,
      image,
      value,
      forGroup,
      id
    }).catch((error) => {
      throw error
    })
  }

  sendPasswordResetEmail(email: string) {
    return axiosApiGet(GET_PASSWORD_RESET_EMAIL, { email }).catch((error) => {
      throw error
    })
  }

  sendNewPassword(email: string, password: string, token: string) {
    return axiosApiPut(PUT_PASSWORD_RESET, {
      email,
      passwordResetToken: token,
      newPassword: password
    }).catch((error) => {
      throw error
    })
  }
}

export default new UserService()
