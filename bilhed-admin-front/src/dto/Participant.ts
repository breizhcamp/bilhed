import type {Person} from "@/dto/Person";

export interface Participant extends Person{
    participationDate: string
    drawOrder?: number

    notificationConfirmSentDate?: string

    checked: boolean
}

export interface ParticipationInfos {
    personId: string
    smsStatus?: string
    nbSmsSent: number
    smsError?: string
    notificationConfirmSentDate?: string
    confirmationDate?: string

}