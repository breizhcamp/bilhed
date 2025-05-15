<template>
  <h1>
    Conférenciers

    <span class="d-inline-block float-end">
      <button type="button" class="btn btn-primary" v-on:click="exportAll()">Export</button>
    </span>
  </h1>

  <div class="mb-3">
    <AttendeesFilter :filter="filter" @filter="(f) => load(f)"/>

    <table class="table table-striped table-hover">
      <thead>
      <tr>
        <th scope="col"><input type="checkbox" v-model="allChecked"></th>
        <th scope="col">Lastname</th>
        <th scope="col">Firstname</th>
        <th scope="col">Email</th>
        <th scope="col">Telephone</th>
        <th scope="col">Pass</th>
        <th scope="col">Particip. date</th>
        <th scope="col">Limit date</th>
        <th scope="col">Paid</th>
        <th scope="col"></th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="p in participants" :key="p.id" @click.exact="p.checked = !p.checked" @click.shift="checkBetween(p)">
        <td><input type="checkbox" v-model="p.checked"></td>
        <td><router-link :to="`/person/${p.id}`" class="nav-link text-decoration-underline">{{ p.lastname }}</router-link></td>
        <td>{{ p.firstname }}</td>
        <td>{{ p.email }}</td>
        <td>{{ p.telephone }}</td>
        <td><Pass :pass="p.pass"/></td>
        <td><DateView :date="p.participantConfirmationDate" format="DD/MM HH:mm" sup=""/></td>
        <td><DateView :date="getLimitDate(p.participantConfirmationDate)" format="DD/MM HH:mm" sup=""/></td>
        <td>{{ p.payed }}</td>
        <td>
          <button type="button" class="btn btn-link btn-sm" title="Remind payed mail" @click="notifyOne(p.id, 'payed/reminder/mail')" :disabled="loading"><BiSendCheck/></button>
        </td>
      </tr>
      </tbody>
    </table>

    <nav class="navbar sticky-bottom bg-light">
      <div class="container-fluid">
        <div>
          <button type="button" class="btn btn-primary me-1" v-on:click="notifySel('payed/reminder/mail')" :disabled="loading"><BiSendCheck/> Remind payed mail</button>
          <button type="button" class="btn btn-outline-warning me-1" v-on:click="notifySel('payed/reminder/sms')" :disabled="loading"><BiSendCheck/> Remind payed SMS</button>
          <button type="button" class="btn btn-outline-danger me-1" v-on:click="levelUp('release')" :disabled="loading"><BiArrowUp/> Level Up to release</button>

          <div class="d-inline-block ms-3" v-if="checked.length > 0">{{ checked.length }}/{{ participants.length }}</div>
        </div>
      </div>
    </nav>
  </div>
</template>

<script lang="ts">
/// <reference types="vite-svg-loader" />

import AttendeesFilter from '@/components/AttendeesFilter.vue';
import DateView from '@/components/DateView.vue'
import Pass from '@/components/Pass.vue'
import type { Attendee } from '@/dto/Attendee';
import type { AttendeeFilter } from '@/dto/AttendeeFilter';
import axios from 'axios'
import BiArrowUp from 'bootstrap-icons/icons/arrow-bar-up.svg?component'
import BiSendCheck from 'bootstrap-icons/icons/send-check.svg?component'
import { defineComponent } from 'vue'
import FileSaver from 'file-saver'
import {toastError, toastSuccess, toastWarning, toInt} from "@/utils/ReminderUtils";
import type {Config} from "@/dto/Config";
import dayjs from "dayjs";

export default defineComponent({
  name: "ParticipantView",
  components: {AttendeesFilter, Pass, DateView, BiSendCheck, BiArrowUp },

  data() {
    return {
      participants: [] as Attendee[],
      allChecked: false,
      loading: false,
      filter: {} as AttendeeFilter,
      nbHoursBeforeRelease: 0
    }
  },

  computed: {
    checked(): Attendee[] {
      return this.participants.filter((r) => r.checked)
    }
  },

  created() {
    this.$watch(() => this.$route.params, () => this.load(), { immediate: true })
  },

  watch: {
    allChecked() {
      this.participants.forEach((r) => r.checked = this.allChecked)
    }
  },

  methods: {
    checkBetween(p: Attendee) {
      const first = this.participants.findIndex((r) => r.checked)
      const clicked = this.participants.findIndex((r) => r.id === p.id)
      if (first === -1) {
        p.checked = !p.checked
      } else {
        const min = Math.min(first, clicked)
        const max = Math.max(first, clicked)
        for (let i = min; i <= max; i++) {
          this.participants[i].checked = !p.checked
        }
      }
    },

    load(filter?: AttendeeFilter) {
      const method = filter ? 'post' : 'get'
      const url = filter ? '/attendees/filter' : '/attendees'

      axios.request({ method, url, data: filter }).then((response) => {
        this.participants = response.data
      })

      axios.get('/config/reminderTimeAtt').then((response) => {
        this.nbHoursBeforeRelease = toInt((response.data as Config).value)
      })
    },

    notifyOne(id: string, type: string) {
      this.sendNotify([id], type);
    },

    notifySel(type: string) {
      this.sendNotify(this.getSelected(), type);
    },

    sendNotify(ids: string[], type: string) {
      if (ids.length === 0) {
        toastWarning("Aucun attendee sélectionné")
        return
      }

      if (!confirm(`Voulez allez envoyer une notification à ${ids.length} personnes, voulez vous continuer ?`))
        return

      this.loading = true
      axios.post('/attendees/notif/' + type, ids).then(() => {
        this.load()
        toastSuccess(`La notification a bien été envoyé (${ids.length}} personnes).`)
      }).catch(() => {
        toastError("Une erreur s'est produite lors de l'envoi des notifications.")
      }).finally(() => {
        this.loading = false
      })
    },

    levelUp(level: string) {
      const ids = this.getSelected()
      if (ids.length === 0) {
        toastWarning("Aucun attendee sélectionné")
        return
      }

      if (!confirm(`Voulez allez changer le statut de ${ids.length} personnes en ${level}, voulez vous continuer ?`))
        return

      this.loading = true
      axios.post('/attendees/levelUp/' + level, ids).then(() => {
        this.load()
        toastSuccess(`Le statut a bien été modifié (${ids.length} personnes).`)
      }).catch(() => {
        toastError("Une erreur s'est produite lors du changement de statut.")
      }).finally(() => {
        this.loading = false
      })
    },

    exportAll() {
      axios.get('/attendees/export', { responseType: 'blob' }).then(res => {
        FileSaver.saveAs(res.data, 'inscrits.csv')
      })
    },

    getSelected: function () {
      return this.participants.filter((p) => p.checked).map((p) => p.id)
    },

    getLimitDate(date ?: string) {
      if (!date) {
        return ''
      }
      const dateJs = dayjs(date)
      return dateJs.add(this.nbHoursBeforeRelease, 'hour').toString()
    }
  }
})
</script>
