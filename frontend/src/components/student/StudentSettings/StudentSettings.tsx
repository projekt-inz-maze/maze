import { Button, Container, Row } from 'react-bootstrap'

import styles from './StudentSettings.module.scss'

const StudentSettings = () => {
  const placeholder = 'brak'

  return (
    <Container fluid className={styles.container}>
      <Row>
        <p className={styles.title}>Ustawienia</p>
      </Row>
      <div className={styles.items}>
        <p>{`Typ osobowości: ${placeholder}`}</p>
        <Button className={styles.testButton}>Wypełnij test!</Button>
      </div>
    </Container>
  )
}

export default StudentSettings
