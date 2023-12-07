import React, { useEffect, useState } from 'react'

import { Button, Col, Container, Modal, Row } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'

import styles from './PersonalityQuizResults.module.scss'
import { useGetPersonalityQuery } from '../../../../api/apiPersonality'
import { getPersonalityName, personalityQuizIntro } from '../../../../utils/constants'
import { getMaxValuePersonalityType } from '../../../../utils/formatters'
import AchieverImg from '../../../../utils/resources/achiever_type.png'

const emptyPersonality = {
  id: 'KILLER',
  name: 'Zabójca',
  type: 20
}

const PersonalityQuizResults = () => {
  const navigate = useNavigate()

  const [userPersonality, setUserPersonality] = useState(emptyPersonality)
  const [dominantPersonality, setDominantPersonality] = useState<string>('')
  const [personalityDescription, setPersonalityDescription] = useState<string>('')

  const { data: personality, isSuccess: personalitySuccess } = useGetPersonalityQuery()

  useEffect(() => {
    if (!personalitySuccess) {
      return
    }

    const maxValuePersonality = getMaxValuePersonalityType(personality)
    const description = personalityQuizIntro.find((p) => p.type === maxValuePersonality.id)?.description ?? ''

    setUserPersonality(maxValuePersonality)
    setDominantPersonality(maxValuePersonality.id)
    setPersonalityDescription(description)
  }, [personality, personalitySuccess])

  return (
    <Modal show size='xl' centered fullscreen className={styles.modalContainer}>
      <Modal.Header className={styles.modalHeader}>
        <Modal.Title className={styles.modalTitle}>Test osobowości</Modal.Title>
        <button type='button' className={styles.customButtonClose} onClick={() => navigate('/courses')}>
          {/* Close button content */}
          <span>&times;</span>
        </button>
      </Modal.Header>
      <Modal.Body className={styles.modalBody}>
        <Container>
          <Row>
            <Col>
              <img src={AchieverImg} alt='Hero img' className='img-fluid' />
            </Col>
            <Col className={styles.resultsContainer}>
              <Row>
                <p className={styles.typeInfo}>Twój typ gracza to:</p>
                <p className={styles.typeResult}>
                  <span>{getPersonalityName(dominantPersonality)}</span>{' '}
                  {`(${userPersonality ? (userPersonality.type || 0).toFixed(2) : ''}%)`}
                </p>
              </Row>
              <Row>
                <p className={styles.typeDescription}>{personalityDescription}</p>
              </Row>
              <Row>
                <p className={styles.typeOther}>
                  <span>Pozostałe wyniki:</span>
                </p>
                <ul>
                  {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
                  {/* @ts-ignore */}
                  {personality &&
                    Object.entries(personality)
                      .filter(([pe]) => pe !== dominantPersonality)
                      .map(([p, percentage]) => (
                        <li key={p}>
                          {getPersonalityName(p)} - {(percentage || 0).toFixed(2)}%
                        </li>
                      ))}
                </ul>
              </Row>
            </Col>
          </Row>
        </Container>
      </Modal.Body>
      <Modal.Footer className={styles.modalFooter}>
        <Button type='button' className={styles.acceptButton} onClick={() => navigate('/courses')}>
          Powrót
        </Button>
      </Modal.Footer>
    </Modal>
  )
}

export default PersonalityQuizResults
