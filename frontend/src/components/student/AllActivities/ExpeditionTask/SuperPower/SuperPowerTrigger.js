import React from 'react'

import { faBolt } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { connect } from 'react-redux'

import { SuperPowerButton } from './SuperPowerStyle'
import Tooltip from '../../../../general/Tooltip/Tooltip'

function SuperPowerTrigger(props) {
  const isSidebarExpanded = props.sidebar.isExpanded
  const {superPowerCanBeUsed} = props

  return (
    <>
      <SuperPowerButton
        data-for="super-power-trigger"
        data-tip={superPowerCanBeUsed?.message ?? ''}
        $isBlocked={!superPowerCanBeUsed?.canBeUsed}
        $isExpanded={isSidebarExpanded}
        $color={props.theme.success}
        onClick={props.startSuperPower}
      >
        <FontAwesomeIcon icon={faBolt} />
      </SuperPowerButton>

      <Tooltip id="super-power-trigger" />
    </>
  )
}

function mapStateToProps(state) {
  const {sidebar} = state
  const {theme} = state

  return {
    sidebar,
    theme
  }
}

export default connect(mapStateToProps)(SuperPowerTrigger)
