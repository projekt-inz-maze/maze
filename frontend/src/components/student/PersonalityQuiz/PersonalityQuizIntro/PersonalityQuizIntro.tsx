import React from 'react'

import styles from './PersonalityQuizIntro.module.scss'
import { personalityQuizIntro } from '../../../../utils/constants'

const PersonalityQuizIntro = () => (
  <div className={styles.modalTaskDescription}>
    <p>
      Aby umilić Ci rozgrywkę, przygotowaliśmy dla Ciebie test osobowości gracza. Po uzupełnieniu testu, przydzielimy
      Cię do jednego z czterech typów graczy wg. taksonomii Bartle&apos;a:
    </p>
    <ol>
      {personalityQuizIntro.map((listItem) => (
        <li key={listItem.nameEng}>
          <p key={listItem.name}>
            <span>{listItem.name}</span> {listItem.nameEng}
          </p>
          <p>{listItem.description}</p>
        </li>
      ))}
    </ol>
  </div>
)

export default PersonalityQuizIntro
