import React from 'react'

import styles from './PersonalityQuizIntro.module.scss'

const PersonalityQuizIntro = () => (
  <div className={styles.modalTaskDescription}>
    <p>
      Aby umilić Ci rozgrywkę, przygotowaliśmy dla Ciebie test osobowości gracza. Po uzupełnieniu testu, przydzielimy
      Cię do jednego z czterech typów graczy wg. taksonomii Bartle&apos;a:
    </p>
    <ol>
      <li>
        <p>
          <span>Zabójców</span> (Killers)
        </p>
        <p>Gracze, którzy największą przyjemność czerpią z pokonywania innych graczy.</p>
      </li>
      <li>
        <p>
          <span>Zdobywców</span> (Achievers)
        </p>
        <p>
          Gracze, którzy największą przyjemność czerpią z osiągania różnych sukcesów (przejście do kolejnego poziomu,
          zdobycie wyższej rangi, znalezienie wszystkich &quot;znajdziek&quot;).
        </p>
      </li>
      <li>
        <p>
          <span>Odkrywców</span> (Explorers)
        </p>
        <p>
          Gracze, którzy lubią odkrywać świat i mechaniki gry - lubią szukać ukrytych przejść, easter eggów oraz
          ograniczeń.
        </p>
      </li>
      <li>
        <p>
          <span>Towarzyskich / Społecznościowców</span> (Socializers)
        </p>
        <p>Gracze, którzy najwięcej przyjemności czerpią z interakcji z innymi graczami.</p>
      </li>
    </ol>
  </div>
)

export default PersonalityQuizIntro
