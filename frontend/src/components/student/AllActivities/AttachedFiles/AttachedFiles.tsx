import { faDownload } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import download from 'downloadjs'
import { Button } from 'react-bootstrap'

import styles from './AttachedFiles.module.scss'
import { AttachedFile } from '../../../../api/types'
import { axiosApiGet } from '../../../../utils/axios'

type AttachedFilesProps = {
  files: AttachedFile[]
}

const AttachedFiles = (props: AttachedFilesProps) => {
  const downloadFile = (fileNumber: number) => {
    // const fileId = props.files[fileNumber].id
    // axiosApiGet(`http://localhost:8080/api/file?id=${fileId}`)
    axiosApiGet('http://localhost:8080/api/file?id=1').then((file) => {
      // download(file, props.files[fileNumber].name)
      download(file, 'file.png')
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
