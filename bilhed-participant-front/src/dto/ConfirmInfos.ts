import type {Person} from "@/dto/Person";

export interface ConfirmInfos {
  members: Person[],
  confirmationLimitDate: string
  refId: string,
  pass: PassType
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

export enum PassType {
  NONE = "NONE",
  TWO_DAYS = "TWO_DAYS",
  THREE_DAYS = "THREE_DAYS",
}