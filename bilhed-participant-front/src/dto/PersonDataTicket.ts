export interface PersonDataTicket {
  hasAttendeeData: Boolean,
  hasTicket: Boolean,
  hasPayed: Boolean,
  payUrl?: string,
}

export interface EndInfo {
  groupPayment: Boolean,
  nbMembers: number,
}