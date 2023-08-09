import {
  GET_RANKING_SEARCH,
  GET_RANKING,
  GET_RANKING_POSITION,
  GET_RANKING_GROUP_POSITION,
  GET_RANKING_GROUP
} from './urls'
import { axiosApiGet } from '../utils/axios'

class RankingService {
  getGlobalRankingList(courseId: number) {
    return axiosApiGet(`${GET_RANKING}?courseId=${courseId}`).catch((error) => {
      throw error
    })
  }

  getStudentGroupRankingList(courseId: number) {
    return axiosApiGet(`${GET_RANKING_GROUP}?courseId=${courseId}`).catch((error) => {
      throw error
    })
  }

  getFilteredRanking(filterQuery: string) {
    return axiosApiGet(GET_RANKING_SEARCH, { search: filterQuery }).catch((error) => {
      throw error
    })
  }

  getStudentPositionInGlobalRanking(courseId: number) {
    return axiosApiGet(`${GET_RANKING_POSITION}?courseId=${courseId}`).catch((error) => {
      throw error
    })
  }

  getStudentPositionInGroupRanking(courseId: number) {
    return axiosApiGet(`${GET_RANKING_GROUP_POSITION}?courseId=${courseId}`).catch((error) => {
      throw error
    })
  }
}

export default new RankingService()
