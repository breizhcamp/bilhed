export interface Person {
    id: string
    lastname: string
    firstname: string
    email: string
    telephone: string
    status: PersonStatus
    groupId: string
    checked: boolean
}

export interface RegistrationInfos {
    personId: string,
    registrationDate: string,
    smsStatus: string,
    nbSmsSent: number,
    lastSmsSentDate: string,
    smsError: string,
    token: string,
    nbTokenTries: number
}

export interface ParticipationInfos {
    personId: string
    smsStatus?: string
    nbSmsSent: number
    smsError?: string
    notificationConfirmSentDate?: string
    confirmationDate?: string
    payed: boolean
}

export enum PersonStatus {
    REGISTERED = "REGISTERED",
    PARTICIPANT = "PARTICIPANT",
    ATTENDEE = "ATTENDEE",
}

