import type {Person} from "@/dto/Person";

export interface Participant extends Person{
    participationDate: string
    drawOrder?: number

    notificationConfirmSentDate?: string

    checked: boolean
}