import { addGroupRequest } from './types/serviceTypes'
import {
  GET_GROUP_INVITATION_CODE_LIST,
  GET_GROUP_STUDENTS,
  GET_STUDENTS_WITH_GROUP_ALL,
  POST_GROUP,
  POST_USER_GROUP
} from './urls'
import { axiosApiGet, axiosApiPost } from '../utils/axios'

class GroupService {
  addGroup({ groupName, groupKey, courseId }: addGroupRequest) {
    return axiosApiPost(POST_GROUP, {
      name: groupName,
      invitationCode: groupKey,
      courseId
    }).catch((error) => {
      throw error
    })
  }

  getGroups(courseId: number) {
    return axiosApiGet(`${GET_GROUP_INVITATION_CODE_LIST}?courseId=${courseId}`).catch((error) => {
      throw error
    })
  }

  getAllStudents(courseId: number) {
    return axiosApiGet(`${GET_STUDENTS_WITH_GROUP_ALL}?courseId=${courseId}`).catch((error) => {
      throw error
    })
  }

  getGroupStudents(groupId: number) {
    return axiosApiGet(GET_GROUP_STUDENTS, { groupId }).catch((error) => {
      throw error
    })
  }

  changeStudentGroup(studentId: number, newGroupId: number) {
    return axiosApiPost(POST_USER_GROUP, {
      studentId,
      newGroupId
    }).catch((error) => {
      throw error
    })
  }
}

export default new GroupService()
