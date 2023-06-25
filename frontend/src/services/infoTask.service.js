import { GET_INFO, GET_INFO_CREATE, POST_INFO_CREATE } from './urls'
import { axiosApiGet, axiosApiPost } from '../utils/axios'

class InfoTaskService {
  getInformation(infoId) {
    return axiosApiGet(GET_INFO, { infoId }).catch((error) => {
      throw error
    })
  }

  getInfoTaskJson() {
    return axiosApiGet(GET_INFO_CREATE).catch((error) => {
      throw error
    })
  }

  setInfoTaskJson({ chapterId, form }) {
    return axiosApiPost(POST_INFO_CREATE, {
      chapterId,
      form
    }).catch((error) => {
      throw error
    })
  }
}

export default new InfoTaskService()
