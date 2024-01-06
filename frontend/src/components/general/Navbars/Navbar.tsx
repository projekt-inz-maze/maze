import PrimaryNavbar from './PrimaryNavbar/PrimaryNavbar'

type NavbarProps = {
  isStudent: boolean
  isProfessor: boolean
  navbarTitles: { name: string; navigateTo: string }[]
  userSubtitles: { name: string; navigateTo: string }[]
}

const Navbar = (props: NavbarProps) => (
  <PrimaryNavbar
    isStudent={props.isStudent}
    isProfessor={props.isProfessor}
    navbarTitles={props.navbarTitles}
    userSubtitles={props.userSubtitles}
  />
)

export default Navbar
