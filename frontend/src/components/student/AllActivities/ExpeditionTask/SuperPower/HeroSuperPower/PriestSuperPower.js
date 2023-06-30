import React, { useEffect, useState } from 'react'

import { useSuperPowerCheck } from '../../../../../../hooks/useSuperPowerCheck'
import SuperPowerTrigger from '../SuperPowerTrigger'

function PriestSuperPower(props) {
  const [superPowerInfo, setSuperPowerInfo] = useState(undefined)

  const superPowerCanBeUsed = useSuperPowerCheck(props.useCheck, superPowerInfo)

  useEffect(() => {
    if (superPowerInfo?.value) {
      props.setRemainingTime(+superPowerInfo.value / 1000) // ms -> s
    }
    // eslint-disable-next-line
  }, [superPowerInfo])

  const startUsingSuperPower = () => {
    if (!superPowerCanBeUsed?.canBeUsed) {
      return
    }

    props
      .usePower()
      .then((response) => {
        setSuperPowerInfo(response)
      })
      .catch(() => {
        setSuperPowerInfo(null)
      })
  }

  return <SuperPowerTrigger superPowerCanBeUsed={superPowerCanBeUsed} startSuperPower={startUsingSuperPower} />
}

export default PriestSuperPower
