export interface Person {
    id: string
    lastname: string
    firstname: string
    email: string
    telephone: string
    pass: string
    status: PersonStatus
    groupId: string
    payed: boolean
}

export interface ReferentInfos {
    personId: string,
    registrationDate: string,
    smsStatus: string,
    nbSmsSent: number,
    lastSmsSentDate: string,
    smsError: string,
    token: string,
    nbTokenTries: number
}

export interface Referent {
    person: Person,
    referentInfos: ReferentInfos
}

export enum PersonStatus {
    REGISTERED = "REGISTERED",
    PARTICIPANT = "PARTICIPANT",
    ATTENDEE = "ATTENDEE",
    ATTENDEE_FULL = "ATTENDEE_FULL"
}

