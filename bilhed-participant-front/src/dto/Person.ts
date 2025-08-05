export interface PersonReq {
    email: string,
    firstname: string,
    lastname: string
    telephone?: string
}

export interface Person extends PersonReq {
    id: string,
}