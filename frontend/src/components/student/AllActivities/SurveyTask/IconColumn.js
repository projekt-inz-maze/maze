import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { Col } from 'react-bootstrap'

export function IconColumn(props) {
  return (
    <Col>
      {props.icons.map((faIcon, idx) => faIcon && <FontAwesomeIcon key={idx} icon={faIcon} />)}
    </Col>
  )
}
