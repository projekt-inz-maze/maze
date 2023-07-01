import React from 'react'

import { Card, Container, Stack } from 'react-bootstrap'
import { useNavigate } from 'react-router-dom'


const CourseList = () => {
  const navigate = useNavigate()

  const handleClick = () => {
    navigate('/game-card')
  }

  return (
    <Container>
      <h1>Cześć, Magda!</h1>
      <h2>Twoje kursy</h2>
      <Stack gap={3}>
        <Card onClick={() => handleClick()}>Sieci komputerowe</Card>
        <Card>Bezpieczeństwo sieci komputerowych</Card>
        <Card>Wirtualne sieci prywatne</Card>
      </Stack>
    </Container>
  )
}

export default CourseList
