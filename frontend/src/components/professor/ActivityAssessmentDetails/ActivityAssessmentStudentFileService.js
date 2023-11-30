import { faDownload } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import download from 'downloadjs'
import { Button, Col } from 'react-bootstrap'
import { connect } from 'react-redux'

import { ActivityAssessmentStudentFileRow } from './ActivityAssesmentDetailsStyles'
import CombatTaskService from '../../../services/combatTask.service'

function ActivityAssessmentStudentFileService(props) {
  const { activityResponseInfo } = props
  const downloadFile = (fileNumber) => {
    const fileId = activityResponseInfo.file[fileNumber].id
    CombatTaskService.getCombatFile(fileId).then((file) => {
      download(file, activityResponseInfo.file[fileNumber].name)
    })
  }
  return (
    <Col>
      <strong>Załączone pliki: </strong>
      {!activityResponseInfo || activityResponseInfo.file?.length === 0 ? (
        <p>Brak dodanych plików</p>
      ) : (
        activityResponseInfo.file?.map((file, idx) => (
          <ActivityAssessmentStudentFileRow key={idx} className='mt-4'>
            <Col>{file.name}</Col>
            <Col>
              <Button
                style={{ backgroundColor: props.theme.warning, borderColor: props.theme.warning }}
                className='ms-2'
                onClick={() => downloadFile(idx)}
              >
                <FontAwesomeIcon icon={faDownload} />
              </Button>
            </Col>
          </ActivityAssessmentStudentFileRow>
        ))
      )}
    </Col>
  )
}

function mapStateToProps(state) {
  const { theme } = state

  return { theme }
}

export default connect(mapStateToProps)(ActivityAssessmentStudentFileService)
