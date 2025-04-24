import {type ReminderConfig, type ReminderConfigRes, ReminderType} from "@/dto/ReminderConfig";
import {toast} from "vue3-toastify";
import "vue3-toastify/dist/index.css";

export function isReminderConfigRes(obj: ReminderConfig): obj is ReminderConfigRes {
    // Check if reminderConfig is a ReminderConfigRes
    return (obj as ReminderConfigRes).id !== undefined
}

export function getShorterType(reminderConfigType: ReminderType): string {
    // Get a shorter string of type : PARTICIPANT -> Par
    return reminderConfigType.charAt(0) + reminderConfigType.slice(1, 3).toLowerCase()
}

export function toastInfo(message: string) {
    toast(message, {
        type: "info",
    })
}

export function toastWarning(message: string) {
    toast(message, {
        type: "warning",
    })
}

export function toastSuccess(message: string) {
    toast(message, {
        type: "success",
    })
}

export function toastError(message: string) {
    toast(message, {
        type: "error",
    })
}

export function toInt(value: string): number {
    return parseInt(value, 10)
}