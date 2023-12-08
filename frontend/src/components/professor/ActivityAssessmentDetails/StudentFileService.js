import { faDownload } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import download from 'downloadjs'
import { Button } from 'react-bootstrap'
import { connect } from 'react-redux'

import styles from './StudentFileService.module.scss'
import CombatTaskService from '../../../services/combatTask.service'

function StudentFileService(props) {
  const downloadFile = (fileNumber) => {
    const fileId = props.activity.file[fileNumber].id
    CombatTaskService.getCombatFile(fileId).then((file) => {
      download(file, props.activity.file[fileNumber].name)
    })
  }

  return (
    <>
      <span>Załączone pliki: </span>
      <div className={styles.container}>
        {!props.activity || props.activity.file?.length === 0 ? (
          <p>Brak dodanych plików</p>
        ) : (
          props.activity.file?.map((file, idx) => (
            <div key={idx} className={styles.container}>
              <p>{file.name}</p>
              <Button
                style={{ backgroundColor: props.theme.warning, borderColor: props.theme.warning }}
                className={styles.downloadButton}
                onClick={() => downloadFile(idx)}
              >
                <FontAwesomeIcon icon={faDownload} />
              </Button>
            </div>
          ))
        )}
      </div>
    </>
  )
}

function mapStateToProps(state) {
  const { theme } = state

  return { theme }
}

export default connect(mapStateToProps)(StudentFileService)
