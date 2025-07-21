import type {ParticipationInfos, Person, ReferentInfos} from "@/dto/Person";

export interface Group {
    id: string,
    referentId: string,
    groupPayment: boolean,
    drawOrder?: number
}

export interface GroupComplete {
    group: Group,
    members: Person[]
}

export interface GroupCompleteParticipant extends GroupComplete {
    referentInfos: ReferentInfos,
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