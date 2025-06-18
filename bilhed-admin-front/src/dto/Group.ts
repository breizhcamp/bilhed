import type {Person, ReferentInfos} from "@/dto/Person";

export interface Group {
    id: string,
    referentId: string,
    groupPayment: boolean,
    drawOrder?: number
}

export interface GroupComplete {
    group: Group,
    referentInfos: ReferentInfos,
    members: Person[]
    // checked: boolean
}