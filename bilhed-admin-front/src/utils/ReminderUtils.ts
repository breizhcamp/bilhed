import type {ReminderConfig, ReminderConfigRes} from "@/dto/ReminderConfig";

export function isReminderConfigRes(obj: ReminderConfig): obj is ReminderConfigRes {
    // Check if reminderConfig is a ReminderConfigRes
    return (obj as ReminderConfigRes).id !== undefined
}

export function getShorterType(reminderConfigType: string): string {
    // Get a shorter string of type : PARTICIPANT -> Par
    return reminderConfigType.charAt(0) + reminderConfigType.slice(1, 3).toLowerCase()
}

export function toastInfo(toast: any, message: string) {
    toast.info(message)
}

export function toastWarning(toast: any, message: string) {
    toast.warning(message)
}

export function toastSuccess(toast: any, message: string) {
    toast.success(message)
}

export function toastError(toast: any, message: string) {
    toast.error(message)
}