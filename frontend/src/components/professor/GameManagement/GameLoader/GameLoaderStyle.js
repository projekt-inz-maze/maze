import { Tooltip } from 'react-bootstrap'
import styled from 'styled-components'

export const CustomTooltip = styled(Tooltip)`
  & > .tooltip-inner {
    max-width: 300px;
    margin-left: 15px;
  }
`
