export interface Attendee {
  id: string
  lastname: string
  firstname: string
  email: string
  telephone: string
  pass: string
  kids?: string

  participantConfirmationDate?: string
  payed: boolean

  checked: boolean
}