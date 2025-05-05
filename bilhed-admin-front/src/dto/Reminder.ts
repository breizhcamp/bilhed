export interface Reminder {
    id: string
    method: string
    origin: string
    personId: string
    reminderDate: string
    template: string
    model: Map<String, String>
}