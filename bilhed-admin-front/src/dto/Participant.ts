export interface Participant {
  id: string
  lastname: string
  firstname: string
  email: string
  telephone: string
  pass: string
  kids?: string

  participationDate: string
  drawOrder?: number

  company?: string
  tShirtSize?: string
  tShirtCut?: string
  vegan?: boolean
  meetAndGreet?: boolean
  postalCode?: string

  confirmationLimitDate?: string

  checked: boolean
}