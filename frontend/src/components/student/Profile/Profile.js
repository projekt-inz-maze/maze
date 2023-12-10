import React, { useEffect, useMemo, useState } from 'react'

import { Col, Container, Row, Spinner, Table } from 'react-bootstrap'
import { connect } from 'react-redux'

import DeleteAccountModal from './DeleteAccountModal'
import EditIndexModal from './EditIndexModal'
import EditPasswordModal from './EditPasswordModal'
import ProfileCard from './ProfileCard'
import { useAppSelector } from '../../../hooks/hooks'
import UserService from '../../../services/user.service'
import { ERROR_OCCURRED } from '../../../utils/constants'
import { isMobileView } from '../../../utils/mobileHelper'

function Profile(props) {
  const isMobileDisplay = isMobileView()

  const courseId = useAppSelector((state) => state.user.courseId)

  const [userData, setUserData] = useState(undefined)
  const [indexNumber, setIndexNumber] = useState(undefined)
  const [user, setUser] = useState(undefined)

  const [isEditIndexModalOpen, setIsEditIndexModalOpen] = useState(false)
  const [isEditPasswordModalOpen, setIsEditPasswordModalOpen] = useState(false)
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false)

  useEffect(() => {
    UserService.getUserData()
      .then((response) => {
        setIndexNumber(response.indexNumber)
        setUser(response)
      })
      .catch(() => {
        setIndexNumber(null)
      })
    UserService.getUserGroup(courseId)
      .then((response) => {
        setUserData(response)
      })
      .catch(() => setUserData(null))
  }, [])

  const userInfoBody = useMemo(() => {
    if (userData === undefined) {
      return <Spinner animation='border' />
    }
    if (userData == null) {
      return <p className='text-center'>{ERROR_OCCURRED}</p>
    }

    const tableHeaders = [
      { text: 'Imię', value: user.firstName },
      { text: 'Nazwisko', value: user.lastName },
      { text: 'Email', value: user.email },
      { text: 'Numer indeksu', value: user.indexNumber },
      { text: 'Grupa zajęciowa', value: userData.name }
    ]
    return (
      <Table>
        <tbody>
          {tableHeaders.map((header, index) => (
            <tr key={index + Date.now()}>
              <td>
                <strong>{header.text}</strong>
              </td>
              <td>{header.value}</td>
            </tr>
          ))}
        </tbody>
      </Table>
    )
  }, [userData, indexNumber])

  return (
    <Container fluid>
      <h3 className='text-center py-3'>Mój profil</h3>
      <Row className='px-0 mx-0'>
        <ProfileCard header='Informacje o profilu' body={userInfoBody} />
      </Row>
      <Row className='px-0 mx-0 py-2' style={{ marginBottom: isMobileDisplay ? '30px' : 'aut   o' }}>
        <Col md={4} className={isMobileDisplay ? 'mb-3' : 'mb-0'}>
          <ProfileCard
            header='Zmień numer indeksu'
            body={
              <p className='text-center h-75'>
                Otwórz okno do edycji numeru indeksu. Pamiętaj, że gdy podasz zły numer, prowadzący nie będzie w stanie
                wystawić oceny końcowej.
              </p>
            }
            showButton
            buttonCallback={() => setIsEditIndexModalOpen(true)}
          />
        </Col>
        <Col md={4} className={isMobileDisplay ? 'mb-3' : 'mb-0'}>
          <ProfileCard
            header='Zmień hasło'
            body={<p className='text-center h-75'>Otwórz formularz do zmiany hasła.</p>}
            showButton
            buttonCallback={() => setIsEditPasswordModalOpen(true)}
          />
        </Col>
        <Col md={4} className={isMobileDisplay ? 'mb-5' : 'mb-0'}>
          <ProfileCard
            header='Usuń konto'
            body={<p className='text-center h-75'>Pamiętaj, że tego procesu nie możesz cofnąć.</p>}
            showButton
            customButton={props.theme.danger}
            buttonText='Usuń'
            buttonCallback={() => setIsDeleteModalOpen(true)}
          />
        </Col>
      </Row>
      <EditIndexModal
        show={isEditIndexModalOpen}
        setModalOpen={setIsEditIndexModalOpen}
        setIndexNumber={setIndexNumber}
      />
      <EditPasswordModal show={isEditPasswordModalOpen} setModalOpen={setIsEditPasswordModalOpen} />
      <DeleteAccountModal show={isDeleteModalOpen} setModalOpen={setIsDeleteModalOpen} />
    </Container>
  )
}

function mapStateToProps(state) {
  const { theme } = state

  return { theme }
}

export default connect(mapStateToProps)(Profile)
