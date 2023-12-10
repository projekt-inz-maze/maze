import React, { useEffect, useRef, useTransition } from 'react'

import { faDownload, faTrash } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import download from 'downloadjs'
import { Button, Col, Form, Row, Spinner } from 'react-bootstrap'
import { connect } from 'react-redux'

import CombatTaskService from '../../../../services/combatTask.service'

function FileService(props) {
  const { task, setFile, setFileName, setIsFetching, isFetching, isReviewed } = props
  const fileInput = useRef(null)
  const [isRemoving, startRemoving] = useTransition()

  useEffect(() => {
    if (!isFetching && fileInput.current) {
      fileInput.current.value = ''
    }
  }, [isFetching])

  const saveFile = (event) => {
    const filename = event.target.value.split(/(\\|\/)/g).pop()
    setFileName(filename)
    setFile(event.target.files[0])
  }

  const remove = (fileNumber) => {
    startRemoving(() => {
      setIsFetching(true)
      // TODO: this endpoint needs refactoring - it should require only fileId
      CombatTaskService.removeCombatTaskFile(task.fileTaskId, fileNumber)
        .then(() => setIsFetching(false))
        .catch(() => setIsFetching(false))
    })
  }

  const downloadFile = (fileNumber) => {
    const fileId = task.taskFiles[fileNumber].id
    CombatTaskService.getCombatFile(fileId).then((file) => {
      download(file, task.taskFiles[fileNumber].name)
    })
  }

  return (
    <>
      {!isReviewed && (
        <Form.Group>
          <Form.Label>
            <span>Załącz plik:</span>
          </Form.Label>
          <Form.Control
            ref={fileInput}
            type='file'
            className='mb-2 mt-1'
            onChange={saveFile}
            style={{ width: '250px' }}
          />
        </Form.Group>
      )}
      {!task || task.taskFiles?.length === 0 ? (
        <p>Brak dodanych plików</p>
      ) : (
        task.taskFiles?.map((file, idx) => (
          <Row key={idx} className='mt-4'>
            <Col>
              <span style={{ fontWeight: 'normal', marginRight: '2em' }}>{file.name}</span>
              <Button
                style={{ backgroundColor: props.theme.danger, borderColor: props.theme.danger }}
                disabled={isReviewed}
                onClick={() => remove(idx)}
              >
                {isRemoving ? <Spinner animation='border' size='sm' /> : <FontAwesomeIcon icon={faTrash} />}
              </Button>
              <Button
                style={{ backgroundColor: props.theme.warning, borderColor: props.theme.warning }}
                className='ms-2'
                onClick={() => downloadFile(idx)}
              >
                <FontAwesomeIcon icon={faDownload} />
              </Button>
            </Col>
          </Row>
        ))
      )}
    </>
  )
}

function mapStateToProps(state) {
  const { theme } = state

  return { theme }
}

export default connect(mapStateToProps)(FileService)
