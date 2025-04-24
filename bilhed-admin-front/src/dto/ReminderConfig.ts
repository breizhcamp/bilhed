export interface ReminderConfig {
    type: ReminderType
    hours: number
    templateMail: string
    templateSms: string
}

export interface ReminderConfigRes extends ReminderConfig {
    id: string
}

export interface ReminderUpdate {
    reminders: ReminderConfig[]
    reminderTime: number
    oldReminderTime: number
}

export interface ReminderBloc {
    type: ReminderType,
    title: string,
    text: string,
    bgColor: string,
    reminderTimeKey: string,
    reminderBgColor: string
}

export enum ReminderType {
    REGISTERED = "REGISTERED",
    PARTICIPANT = "PARTICIPANT",
    ATTENDEE = "ATTENDEE"
}