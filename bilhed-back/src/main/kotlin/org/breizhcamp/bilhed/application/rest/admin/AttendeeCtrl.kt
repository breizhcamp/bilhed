package org.breizhcamp.bilhed.application.rest.admin

import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.breizhcamp.bilhed.domain.entities.ReminderOrigin
import org.breizhcamp.bilhed.domain.entities.TicketExportData
import org.breizhcamp.bilhed.domain.use_cases.AttendeeNotify
import org.breizhcamp.bilhed.domain.use_cases.PersonRelease
import org.breizhcamp.bilhed.domain.use_cases.TicketExport
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.nio.charset.StandardCharsets
import java.util.*

@RestController("adminAttendeeCtrl")
@RequestMapping("/admin/attendees")
class AttendeeCtrl(
    private val attendeeNotify: AttendeeNotify,
    private val personRelease: PersonRelease,
    private val ticketExport: TicketExport,
) {
    @PostMapping("/notif/payed/reminder/mail") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun payedReminderMail(@RequestBody ids: List<UUID>) {
        attendeeNotify.remindPayedMail(ids, ReminderOrigin.MANUAL)
    }

    @PostMapping("/notif/payed/reminder/sms") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun payedReminderSms(@RequestBody ids: List<UUID>) {
        attendeeNotify.remindPayedSms(ids, ReminderOrigin.MANUAL)
    }

    @PostMapping("/levelUp/release") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun release(@RequestBody ids: List<UUID>) {
        personRelease.attendeeRelease(ids)
    }

    @GetMapping("/export")
    fun exportTickets(output: HttpServletResponse) {
        output.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"inscrits.csv\"")
        output.setHeader(HttpHeaders.CONTENT_TYPE, "text/csv")
        output.characterEncoding = StandardCharsets.UTF_8.name()

        val out = CSVPrinter(output.writer,
            CSVFormat.EXCEL.builder()
                .setHeader("id", "ticketType", "Codes-barres", "lastname", "firstname", "email", "company", "noGoodies", "tShirtSize", "tShirtFitting")
                .build()
        )

        ticketExport.export().sortedWith(compareBy(TicketExportData::lastname, TicketExportData::firstname)).forEachIndexed { index, it ->
            out.printRecord(index, it.ticketType, it.barcode, it.lastname, it.firstname, it.email, it.company, it.noGoodies, it.tShirtSize, it.tShirtFitting)
        }

        out.close()
    }
}

