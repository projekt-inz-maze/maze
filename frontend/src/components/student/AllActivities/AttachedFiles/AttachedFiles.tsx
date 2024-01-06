import { faDownload } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import download from 'downloadjs'
import { Button } from 'react-bootstrap'

import styles from './AttachedFiles.module.scss'
import { AttachedFile } from '../../../../api/types'
import CombatTaskService from '../../../../services/combatTask.service'

type AttachedFilesProps = {
  files: AttachedFile[]
}

const AttachedFiles = (props: AttachedFilesProps) => {
  const downloadFile = (fileNumber: number) => {
    const fileId = props.files[fileNumber].id
    CombatTaskService.getCombatFile(fileId).then((file) => {
      download(file, props.files[fileNumber].name)
    })
  }

  return (
    <>
      <span>Załączone pliki: </span>
      <div className={styles.container}>
        {!props.files || props.files?.length === 0 ? (
          <p>Brak dodanych plików</p>
        ) : (
          props.files?.map((file, idx) => (
            <div key={idx} className={styles.container}>
              <p>{file.name}</p>
              <Button
                style={{ backgroundColor: '#FFB21C' }}
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

export default AttachedFiles
