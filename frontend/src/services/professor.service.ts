import {
  POST_TASK_RESULT_CSV,
  GET_TASK_EVALUATE_ALL,
  GET_TASK_EVALUATE_FIRST,
  POST_FEEDBACK_PROFESSOR,
  POST_ADDITIONAL_POINTS,
  GET_POINTS_ALL_LIST_PROFESSOR,
  GET_SUMMARY,
  GET_PROFESSOR_REGISTER_TOKEN,
  GET_GRADES,
  DELETE_USER_PROFESSOR,
  GET_PROFESSOR_EMAILS,
  PUT_HERO,
  GET_FILE_LOG,
  POST_TASK_FILE_RESULT_FILE
} from './urls'
import { parseJwt } from '../utils/Api'
import {
  axiosApiGet,
  axiosApiGetFile,
  axiosApiPost,
  axiosApiMultipartPost,
  axiosApiDelete,
  axiosApiPut
} from '../utils/axios'

class ProfessorService {
  getUser() {
    const user = localStorage.getItem('user')
    return user ? JSON.parse(user) : null
  }

  getEmail() {
    return parseJwt(this.getUser().access_token).sub
  }

  getCSVGradesFile(studentsId: number[], activitiesId: number[]) {
    return axiosApiGetFile(POST_TASK_RESULT_CSV, { studentIds: studentsId, activityIds: activitiesId }).catch(
      (error) => {
        throw error
      }
    )
  }

  getTasksToEvaluateList(courseId: number) {
    return axiosApiGet(`${GET_TASK_EVALUATE_ALL}?courseId=${courseId}`).catch((error) => {
      throw error
    })
  }

  getFirstTaskToEvaluate(taskId: number) {
    return axiosApiGet(GET_TASK_EVALUATE_FIRST, { fileTaskId: taskId }).catch((error) => {
      throw error
    })
  }

  sendTaskEvaluation(taskId: number, remarks: string, points: number, file: any, fileName: string) {
    return axiosApiMultipartPost(POST_FEEDBACK_PROFESSOR, {
      fileTaskResultId: taskId,
      content: remarks,
      points,
      file,
      fileName
    }).catch((error) => {
      throw error
    })
  }

  sendBonusPoints(studentId: number, points: number, description: string, dateInMillis: number) {
    return axiosApiPost(POST_ADDITIONAL_POINTS, {
      studentId,
      points,
      description,
      dateInMillis
    }).catch((error) => {
      throw error
    })
  }

  getStudentPointsList(studentEmail: string) {
    return axiosApiGet(GET_POINTS_ALL_LIST_PROFESSOR, { studentEmail }).catch((error) => {
      throw error
    })
  }

  getGameSummaryStats(courseId: number) {
    return axiosApiGet(`${GET_SUMMARY}?courseId=${courseId}`).catch((error) => {
      throw error
    })
  }

  getRegistrationToken() {
    return axiosApiGet(GET_PROFESSOR_REGISTER_TOKEN).catch((error) => {
      throw error
    })
  }

  getStudentGrades(courseId: number) {
    return axiosApiGet(`${GET_GRADES}?courseId=${courseId}`).catch((error) => {
      throw error
    })
  }

  deleteAccount(newProfessorEmail: string) {
    return axiosApiDelete(DELETE_USER_PROFESSOR, { professorEmail: newProfessorEmail }).catch((error) => {
      throw error
    })
  }

  getProfessorsEmails() {
    return axiosApiGet(GET_PROFESSOR_EMAILS).catch((error) => {
      throw error
    })
  }

  editHeroSuperPower(heroType: any, powerBaseValue: number, coolDownMs: number) {
    return axiosApiPut(PUT_HERO, {
      type: heroType,
      value: powerBaseValue,
      coolDownMillis: coolDownMs
    }).catch((error) => {
      throw error
    })
  }

  getLogsFile() {
    return axiosApiGet(GET_FILE_LOG).catch((error) => {
      throw error
    })
  }

  addAttachmentFileTask(body: any) {
    return axiosApiMultipartPost(POST_TASK_FILE_RESULT_FILE, body)
  }
}

export default new ProfessorService()
