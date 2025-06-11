import type {Person, Referent} from "@/dto/Person";

export interface Group {
    id: string,
    referentId: string,
    groupPayment: boolean,
    drawOrder?: number
}

export interface GroupComplete {
    group: Group,
    referent: Referent,
    companions: Person[]
    checked: boolean
}