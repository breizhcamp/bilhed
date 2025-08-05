import type {ParticipationInfos, Person, RegistrationInfos} from "@/dto/Person";

export interface Group {
    id: string,
    referentId: string,
    pass: PassType
    groupPayment: boolean,
    drawOrder?: number
}

export enum PassType {
    NONE = "NONE",
    TWO_DAYS = "TWO_DAYS",
    THREE_DAYS = "THREE_DAYS",
}

export interface GroupComplete {
    group: Group,
    members: Person[]
}

export interface GroupCompleteParticipant extends GroupComplete {
    registrationInfos: RegistrationInfos,
}

export interface GroupCompleteAttendee extends GroupComplete{
    participationInfos: ParticipationInfos[]
}

export interface GroupCompleteParticipantWithRef extends GroupCompleteParticipant {
    referent: Person | null
}

export interface GroupCompleteAttendeeWithRef extends GroupCompleteAttendee {
    referent: Person | null
}