import React from 'react'

import { useNavigate } from 'react-router-dom'

import styles from './CourseList.module.css'

const CourseList = () => {
  const navigate = useNavigate()

  const handleClick = () => {
    navigate('/game-card')
  }

  return (
    <div className={styles.mainContainer}>
      <h1>Cześć, Magda!</h1>
      <h2>Twoje kursy</h2>
      <button type="button" onClick={() => handleClick()}>Kurs 1</button>
    </div>
  )
}

export default CourseList
