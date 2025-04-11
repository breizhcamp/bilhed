export interface ReminderConfig {
    type: string
    hours: number
    templateMail: string
    templateSms: string
}

export interface ReminderConfigRes extends ReminderConfig {
    id: string
}

export interface RemindersLists {
    reg: ReminderConfig[]
    par: ReminderConfig[]
    att: ReminderConfig[]
}

export interface ReminderUpdate {
    reminders: ReminderConfig[]
    reminderTime: number
    oldReminderTime: number
}

export interface ReminderBloc {
    type: "REGISTERED" | "PARTICIPANT" | "ATTENDEE",
    title: string,
    text: string,
    bgColor: string,
    reminderTimeKey: string,
    reminderBgColor: string
}