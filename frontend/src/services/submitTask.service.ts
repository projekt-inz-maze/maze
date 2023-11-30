import {GET_SUBMIT_TASK_FILE, POST_SUBMIT_TASK_CREATE} from './urls'
import {TaskRequest} from '../api/types'
import {axiosApiGet, axiosApiPost} from '../utils/axios'

class SubmitTaskService {
    getFileTaskJson() {
        return axiosApiGet(GET_SUBMIT_TASK_FILE).catch((error) => {
            throw error
        })
    }

    setFileTaskJson(request: TaskRequest) {
        return axiosApiPost(POST_SUBMIT_TASK_CREATE, {
            chapterId: request.chapterId,
            form: request.form
        }).catch((error) => {
            throw error
        })
    }
}

export default new SubmitTaskService()
