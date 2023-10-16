import React from 'react'

import style from './CustomDropdownToggle.module.scss'

type CustomDropdownToggleProps = {
  // eslint-disable-next-line no-unused-vars
  onClick: (e: React.MouseEvent<HTMLDivElement>) => void
}

const CustomDropdownToggle : React.ForwardRefRenderFunction<HTMLDivElement, CustomDropdownToggleProps> = ({ onClick }, ref) => (
  <div
    ref={ref}
    onClick={(e) => {
      e.preventDefault()
      onClick(e)
    }}
    className={style.dropdownToggle}
  >
    <svg
      xmlns='http://www.w3.org/2000/svg'
      fill='none'
      viewBox='0 0 24 24'
      strokeWidth={1.5}
      stroke='currentColor'
      className={style.menuIcon}
    >
      <path
        strokeLinecap='round'
        strokeLinejoin='round'
        d='M6.75 12a.75.75 0 11-1.5 0 .75.75 0 011.5 0zM12.75 12a.75.75 0 11-1.5 0 .75.75 0 011.5 0zM18.75 12a.75.75 0 11-1.5 0 .75.75 0 011.5 0z'
      />
    </svg>
  </div>
)

export default React.forwardRef<HTMLDivElement, CustomDropdownToggleProps>(CustomDropdownToggle)