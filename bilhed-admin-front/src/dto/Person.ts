export interface Person {
    id: string
    lastname: string
    firstname: string
    email: string
    telephone: string
    pass: string
    _status: PersonStatus
}

export enum PersonStatus {
    REGISTERED = "REGISTERED",
    PARTICIPANT = "PARTICIPANT",
    ATTENDEE = "ATTENDEE",
    ATTENDEE_FULL = "ATTENDEE_FULL"
}

