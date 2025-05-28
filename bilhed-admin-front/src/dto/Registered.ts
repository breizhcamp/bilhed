import type {Person} from "@/dto/Person";

export interface Registered extends Person {
  registrationDate: string

  checked: boolean

  smsStatus: string, // status spécial
  nbSmsSent: number,

  lastSmsSentDate?: string,
  smsError?: string,
  token: string,
  nbTokenTries: number,
}