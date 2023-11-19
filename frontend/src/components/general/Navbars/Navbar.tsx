import PrimaryNavbar from './PrimaryNavbar/PrimaryNavbar'
import { AddCourseRequest } from '../../../api/types'
import { joinGroupRequest } from '../../../services/types/serviceTypes'

type NavbarProps = {
  navbarTitles: { name: string; navigateTo: string }[]
  userSubtitles: { name: string; navigateTo: string }[]
  // dispatch: any
  // userRole: number
  // onAddCourse: (props: AddCourseRequest) => void
  // onJoinCourse: (props: joinGroupRequest) => void
}

const Navbar = (props: NavbarProps) => {
  const placeholder = true
  return <PrimaryNavbar navbarTitles={props.navbarTitles} userSubtitles={props.userSubtitles} />
}

export default Navbar
