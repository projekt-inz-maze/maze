import { Button } from 'react-bootstrap'
import styled from 'styled-components'

export const SendTaskButton = styled(Button)`
  bottom: 0;
  position: relative;
  margin-left: auto;
  margin-right: auto;
  margin-bottom: 10px;
  background-color: ${(props) => props.$background} !important;
`
