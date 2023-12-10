import React, { useEffect, useState } from 'react'

import { Button, Modal } from 'react-bootstrap'
import { useLocation, useNavigate } from 'react-router-dom'

import styles from './InfoTask.module.scss'
import { useGetPersonalityQuery } from '../../../../api/apiPersonality'
import InfoTaskService from '../../../../services/infoTask.service'
import { personalityQuizIntro } from '../../../../utils/constants'
import { getMaxValuePersonalityType } from '../../../../utils/formatters'
import AttachedFiles from '../AttachedFiles/AttachedFiles'

const emptyPersonality = {
  id: 'KILLER',
  name: 'Zabójca',
  type: 20
}

const emptyInfo = {
  name: 'Wytyczne',
  description: 'Brak informacji o zadaniu',
  imageUrls: [],
  content: '',
  files: []
}

const InfoTask = () => {
  const navigate = useNavigate()

  const location = useLocation()
  const { activityId: informationId } = location.state
  const [information, setInformation] = useState(emptyInfo)

  useEffect(() => {
    InfoTaskService.getInformation(informationId).then((response) => {
      setInformation(response)
    })
  }, [informationId])

  return (
    <Modal show size='xl' centered fullscreen className={styles.modalContainer}>
      <Modal.Header className={styles.modalHeader}>
        <Modal.Title className={styles.modalTitle}>Wytyczne: {information.name ?? ''}</Modal.Title>
        <button type='button' className={styles.customButtonClose} onClick={() => navigate('/courses')}>
          {/* Close button content */}
          <span>&times;</span>
        </button>
      </Modal.Header>
      <Modal.Body>
        <div className={styles.modalTaskDescription}>
          <p>
            <span>Temat:</span>
          </p>
          <p>{information.description}</p>
          <p className={styles.sectionP}>
            <span>Treść:</span>
          </p>
          <p className={`text-justify ${styles.lastP}`}>{information.content}</p>
          <AttachedFiles files={information.files} />
          <p className={styles.sectionP}>
            <span>Linki:</span>
          </p>
          {information.imageUrls &&
            information.imageUrls.map((url: string, index: number) => (
              <p key={url}>
                <a href={url} target='_blank' rel='noreferrer'>{`Link ${index + 1}`}</a>
              </p>
            ))}
        </div>
      </Modal.Body>
      <Modal.Footer className={styles.modalFooter}>
        <Button type='button' className={styles.acceptButton} onClick={() => navigate('/courses')}>
          Powrót
        </Button>
      </Modal.Footer>
    </Modal>
  )
}

export default InfoTask
