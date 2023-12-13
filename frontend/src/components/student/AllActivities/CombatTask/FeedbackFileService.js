import { faDownload } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import download from 'downloadjs'
import { Row, Col, Button } from 'react-bootstrap'
import { connect } from 'react-redux'

import CombatTaskService from '../../../../services/combatTask.service'

function FeedbackFileService(props) {
  const { feedbackFile } = props
  const downloadFile = () => {
    const fileId = feedbackFile.id
    CombatTaskService.getCombatFile(fileId).then((file) => {
      download(file, feedbackFile.name)
    })
  }

  return (
    <Col>
      <strong>Załączony plik prowadzącego:</strong>
      {!feedbackFile ? (
        <p>Brak pliku</p>
      ) : (
        <Row className='mt-1'>
          <Col>
            {feedbackFile.name}
            <Button
              style={{ backgroundColor: props.theme.warning, borderColor: props.theme.warning }}
              className='ms-2'
              onClick={downloadFile}
            >
              <FontAwesomeIcon icon={faDownload} />
            </Button>
          </Col>
        </Row>
      )}
    </Col>
  )
}

function mapStateToProps(state) {
  const { theme } = state

  return { theme }
}
export default connect(mapStateToProps)(FeedbackFileService)
