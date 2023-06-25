import {
  POST_GROUP,
  GET_STUDENTS_WITH_GROUP_ALL,
  GET_GROUP_INVITATION_CODE_LIST,
  POST_USER_GROUP,
  GET_GROUP_STUDENTS
} from './urls'
import { axiosApiGet, axiosApiPost } from '../utils/axios'

class GroupService {
  addGroup({ groupName, groupKey }) {
    return axiosApiPost(POST_GROUP, {
      name: groupName,
      invitationCode: groupKey
    }).catch((error) => {
      throw error
    })
  }

  getGroups() {
    return axiosApiGet(GET_GROUP_INVITATION_CODE_LIST).catch((error) => {
      throw error
    })
  }

  getAllStudents() {
    return axiosApiGet(GET_STUDENTS_WITH_GROUP_ALL).catch((error) => {
      throw error
    })
  }

  getGroupStudents(groupId) {
    return axiosApiGet(GET_GROUP_STUDENTS, { groupId }).catch((error) => {
      throw error
    })
  }

  changeStudentGroup(studentId, newGroupId) {
    return axiosApiPost(POST_USER_GROUP, {
      studentId,
      newGroupId
    }).catch((error) => {
      throw error
    })
  }
}

export default new GroupService()
