import type {Person} from "@/dto/Person";

export interface Attendee extends Person {
  participantConfirmationDate?: string
  payed: boolean

  checked: boolean
}

export interface AttendeeFull extends Attendee {
  company?: string
  tshirtSize?: string
  tshirtCut?: string
  vegan?: boolean
  meetAndGreet?: boolean
  postalCode?: string
}