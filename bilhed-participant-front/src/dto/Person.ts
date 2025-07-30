export interface PersonReq {
    email: string,
    firstname: string,
    lastname: string
    telephone?: string
}

export interface ReferentReq extends PersonReq {
    pass: string,
}

export interface Person extends ReferentReq {
    id: string,
}