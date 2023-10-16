import { DELETE_RANK, GET_RANK_ALL, GET_RANK_CURRENT, POST_RANK, PUT_RANK_UPDATE } from './urls'
import { axiosApiDelete, axiosApiGet, axiosApiMultipartPut, axiosApiMultipartPost } from '../utils/axios'

class RankService {
  editRank(rankId: number, rankName: string, minPoints: number, image: any, rankType: string) {
    return axiosApiMultipartPut(PUT_RANK_UPDATE, {
      name: rankName,
      minPoints,
      image,
      type: rankType,
      rankId
    }).catch((error) => {
      throw error
    })
  }

  addNewRank(rankName: string, minPoints: number, image: any, rankType: string) {
    return axiosApiMultipartPost(POST_RANK, {
      name: rankName,
      minPoints,
      image,
      type: rankType
    }).catch((error) => {
      throw error
    })
  }

  getCurrentStudentRank(courseId: number) {
    return axiosApiGet(`${GET_RANK_CURRENT}?courseId=${courseId}`).catch((error) => {
      throw error
    })
  }

  getAllRanks(courseId: number) {
    return axiosApiGet(`${GET_RANK_ALL}?courseId=${courseId}`).catch((error) => {
      throw error
    })
  }

  deleteRank(rankId: number) {
    return axiosApiDelete(DELETE_RANK, { rankId }).catch((error) => {
      throw error
    })
  }
}

export default new RankService()
