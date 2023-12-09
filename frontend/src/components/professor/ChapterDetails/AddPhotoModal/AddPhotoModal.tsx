import React, { ChangeEvent, useState } from 'react'

import { Button, Form, Modal, ModalBody, ModalFooter, ModalHeader } from 'react-bootstrap'

import styles from './AddPhotoModal.module.scss'
import { useSendFileMutation } from '../../../../api/apiFiles'
import { SendFileRequest } from '../../../../api/types'
import ProfessorService from '../../../../services/professor.service'

type AddPhotoModalProps = {
  showModal: boolean
  setShowModal: (arg: boolean) => void
  activityId: number
  activityName: string
}

const AddPhotoModal = (props: AddPhotoModalProps) => {
  const [fileBlob, setFileBlob] = useState<Blob | null>(null)
  const [fileName, setFileName] = useState<string>('')

  const fileRef = React.useRef<HTMLInputElement>(null)

  const [sendFile] = useSendFileMutation()

  const saveFile = (event: ChangeEvent<HTMLInputElement>) => {
    const filename = event.target.value.split(/(\\|\/)/g).pop()
    if (filename != null) {
      setFileName(filename)
    }
    if (event.target.files !== null) {
      setFileBlob(event.target.files[0])
    }
  }

  const prepareRequest = (body: SendFileRequest): FormData => {
    const formData = new FormData()
    // Object.keys(body).forEach((key) => {
    //   // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //   // @ts-ignore
    //   if (body[key]) {
    //     // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //     // @ts-ignore
    //     formData.append(key, body[key])
    //   }
    // })
    formData.append('fileTaskId', body.fileTaskId)
    formData.append('openAnswer', body.openAnswer)
    formData.append('file', body.file)
    formData.append('fileName', body.fileName)

    return formData
  }

  const handleAddPhoto = async () => {
    // TODO: finish this
    // const request: SendFileRequest = {
    //   fileTaskId: props.activityId.toString() || '0',
    //   openAnswer: 'openAnswer',
    //   file: fileBlob,
    //   fileName
    // }
    // const preparedRequest = prepareRequest(request)
    // // console.log('request', request)
    // // console.log('prepared', preparedRequest)
    // await sendFile(preparedRequest)
    const request = {
      activityId: props.activityId,
      file: fileBlob,
      fileName
    }
    await ProfessorService.addAttachmentFileTask(request)
    props.setShowModal(false)
  }

  return (
    <Modal show={props.showModal} onHide={() => props.setShowModal(false)} size='lg'>
      <ModalHeader>
        <h4 className='text-center w-100'>{`Dodawanie pliku do${
          props.activityName ? `: ${props.activityName}` : ''
        }`}</h4>
      </ModalHeader>
      <ModalBody>
        <Form.Group className={styles.addFileForm}>
          <Form.Label>
            <span>Załącz plik:</span>
          </Form.Label>
          <Form.Control ref={fileRef} type='file' onChange={saveFile} />
        </Form.Group>
      </ModalBody>
      <ModalFooter className='d-flex justify-content-center'>
        <Button
          variant='secondary'
          onClick={() => {
            props.setShowModal(false)
          }}
          className={styles.rejectButton}
        >
          Anuluj
        </Button>

        <Button type='button' variant='primary' className={styles.acceptButton} onClick={() => handleAddPhoto()}>
          Zapisz
        </Button>
      </ModalFooter>
    </Modal>
  )
}

export default AddPhotoModal
