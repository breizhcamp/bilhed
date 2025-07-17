import type {Person} from "@/dto/Person";

export interface ConfirmInfos {
  members: Person[],
  confirmationLimitDate: string
}

export interface AttendeeData {
  id: string
  company?: string
  tShirtSize?: string
  tShirtCut?: string
  vegan?: boolean
  meetAndGreet?: boolean
  postalCode?: string
}