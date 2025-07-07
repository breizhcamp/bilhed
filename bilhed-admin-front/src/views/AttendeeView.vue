<template>
  <h1>
    Conférenciers

    <span class="d-inline-block float-end">
      <button type="button" class="btn btn-primary" v-on:click="exportAll()">Export</button>
    </span>
  </h1>

  <div class="mb-3">
    <AttendeesFilter :filter="filter" @filter="(f) => load(f)"/>

    <div class="d-flex align-items-center p-2 fw-bold mb-2">
      <div class="form-check me-3">
        <input type="checkbox" class="form-check-input" v-model="allChecked" />
      </div>
      <div class="flex-grow-1">
        <div class="row">
          <div class="col-md-auto"><i class="bi col-md-auto bi-dash"></i></div>
          <div class="col-md-1">Nom</div>
          <div class="col-md-1">Prénom</div>
          <div class="col-md-2">Email</div>
          <div style="width: 15%">Téléphone</div>
          <div class="col-md-auto">Pass</div>
          <div class="col-md-2">Date particip.</div>
          <div style="width: 15%">Date limite</div>
          <div class="col-md-auto">Payé</div>
        </div>
      </div>
      <div class="me-1 ms-2">
        Actions
      </div>
    </div>

    <div class="accordion mb-3" id="groupAccordion">
      <div v-for="(g, i) in groups" :key="g.group.id" :class="['accordion-item', { 'bg-body-secondary': i % 2 === 0 }]">

        <template v-if="g.members.length > 1 && g.group.groupPayment">
          <div class="d-flex align-items-center p-2">
            <div class="form-check">
              <input class="form-check-input" type="checkbox" :id="`checkbox${g.group.id}`" v-model="g.members[0].checked" @change="checkGroup(g.group.id, g.members[0].checked)" />
            </div>

            <button
                type="button"
                class="btn flex-grow-1 text-start me-2"
                :class="{ 'no-pointer-events': g.members.length === 1 && !g.group.groupPayment }"
                :data-bs-toggle="g.members.length > 1 && g.group.groupPayment ? 'collapse' : null"
                :data-bs-target="`#collapseG${g.group.id}`"
                aria-expanded="false"
                :aria-controls="`collapseG${g.group.id}`"
                @click="g.members.length > 1 && g.group.groupPayment ? animateChevron(`chevron-${g.group.id}`) : null"
            >
              <span class="row">
                <i :class="g.members.length > 1 && g.group.groupPayment ? 'bi-chevron-down' : 'bi-dash'" class="bi transition-icon col-md-auto" :id="`chevron-${g.group.id}`"></i>
                <span class="col-md-1">{{ g.members[0].lastname }}</span>
                <span class="col-md-1">{{ g.members[0].firstname }}</span>
                <span class="col-md-2 break-email">{{ g.members[0].email }}</span>
                <span class="col-md-2">{{ g.members[0].telephone }}</span>
                <span class="col-md-auto"><Pass :pass="g.members[0].pass"/> &emsp;</span>
                <span class="col-md-2"><DateView format="DD/MM HH:mm" sup="" :date="g.participationInfos.find(p => p.personId === g.members[0].id)?.confirmationDate"/></span>
                <span class="col-md-2"><DateView format="DD/MM HH:mm" sup="" :date="getLimitDate(g.participationInfos.find(p => p.personId === g.members[0].id)?.confirmationDate)"/></span>
                <span class="col-md-auto">{{ getBoolStr(g.members[0].payed) }}</span>
              </span>
            </button>
            <div class="d-flex">
              <button type="button" class="btn btn-link btn-sm ms-1 icon-small" title="Send email reminder" @click="notifyOne(g.members[0].id, 'payed/reminder/mail')" :disabled="loading"><BiEnvelope/></button>
              <button type="button" class="btn btn-link btn-sm ms-1 icon-small" title="Send sms reminder" @click="notifyOne(g.members[0].id, 'payed/reminder/sms')" :disabled="loading"><BiChatText/></button>
              <router-link :to="`/person/${g.members[0].id}`" class="nav-link ms-1 d-flex align-items-center icon-small"><BiPencil/></router-link>
              <router-link :to="`/group/${g.group.id}`" class="nav-link ms-1 d-flex align-items-center icon-small"><BiPeople/></router-link>
            </div>
          </div>

          <div
              :id="`collapseG${g.group.id}`"
              class="accordion-collapse collapse p-1"
              :aria-labelledby="`headingG${g.group.id}`"
          >
            <div class="accordion-body pe-1">

              <table class="table table-sm table-transparent">
                <thead>
                <tr>
                  <th scope="col">Nom</th>
                  <th scope="col">Prénom</th>
                  <th scope="col">Email</th>
                  <th scope="col"></th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="comp in g.members.slice(1)" :key="comp.id">
                  <td class="w-25">{{ comp.lastname }}</td>
                  <td class="w-25">{{ comp.firstname }}</td>
                  <td class="w-25 break-email">{{ comp.email }}</td>
                  <td class="w-25 p-0 align-middle text-end">
                    <router-link :to="`/person/${comp.id}`" class="nav-link ms-1  icon-small"><BiPencil/></router-link>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </template>
        <template v-else v-for="m in g.members" :key="m.id">
          <div class="d-flex align-items-center p-2">
            <div class="form-check">
              <input class="form-check-input" type="checkbox" :id="`checkbox${m.id}`" v-model="m.checked" />
            </div>

            <button
                type="button"
                class="btn flex-grow-1 text-start me-2 no-pointer-events"
            >
               <span class="row">
                <i class="bi transition-icon col-md-auto bi-dash"></i>
                <span class="col-md-1">{{ m.lastname }}</span>
                <span class="col-md-1">{{ m.firstname }}</span>
                <span class="col-md-2 break-email">{{ m.email }}</span>
                <span class="col-md-2">{{ m.telephone }}</span>
                <span class="col-md-auto"><Pass :pass="m.pass"/> &emsp;</span>
                <span class="col-md-2"><DateView format="DD/MM HH:mm" sup="" :date="g.participationInfos.find(p => p.personId === m.id)?.confirmationDate"/></span>
                <span class="col-md-2"><DateView format="DD/MM HH:mm" sup="" :date="getLimitDate(g.participationInfos.find(p => p.personId === m.id)?.confirmationDate)"/></span>
                <span class="col-md-auto">{{ getBoolStr(m.payed) }}</span>
              </span>
            </button>
            <div class="d-flex">
              <button type="button" class="btn btn-link btn-sm ms-1 icon-small" title="Send email reminder" @click="notifyOne(m.id, 'payed/reminder/mail')" :disabled="loading"><BiEnvelope/></button>
              <button type="button" class="btn btn-link btn-sm ms-1 icon-small" title="Send sms reminder" @click="notifyOne(m.id, 'payed/reminder/sms')" :disabled="loading"><BiChatText/></button>
              <router-link :to="`/person/${m.id}`" class="nav-link ms-1 d-flex align-items-center icon-small"><BiPencil/></router-link>
              <router-link :to="`/group/${g.group.id}`" class="nav-link ms-1 d-flex align-items-center icon-small"><BiPeople/></router-link>
            </div>
          </div>
        </template>
      </div>
    </div>

    <nav class="navbar sticky-bottom bg-light">
      <div class="container-fluid">
        <div>
          <button type="button" class="btn btn-primary me-1" v-on:click="notifySel('payed/reminder/mail')" :disabled="loading"><BiSendCheck/> Remind payed mail</button>
          <button type="button" class="btn btn-outline-warning me-1" v-on:click="notifySel('payed/reminder/sms')" :disabled="loading"><BiSendCheck/> Remind payed SMS</button>
          <button type="button" class="btn btn-outline-danger me-1" v-on:click="levelUp('release')" :disabled="loading"><BiArrowUp/> Level Up to release</button>

          <div class="d-inline-block ms-3" v-if="checked.length > 0">{{ checked.length }}/{{ groups.reduce((acc, g) => acc + g.members.length, 0) }}</div>
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
import type {Attendee} from '@/dto/Attendee';
import axios from 'axios'
import BiArrowUp from 'bootstrap-icons/icons/arrow-bar-up.svg?component'
import BiSendCheck from 'bootstrap-icons/icons/send-check.svg?component'
import BiEnvelope from "bootstrap-icons/icons/envelope.svg?component";
import BiPencil from "bootstrap-icons/icons/pencil.svg?component";
import BiChatText from "bootstrap-icons/icons/chat-text.svg?component";
import BiPeople from "bootstrap-icons/icons/people.svg?component";
import {defineComponent} from 'vue'
import FileSaver from 'file-saver'
import {toastError, toastSuccess, toastWarning, toInt} from "@/utils/ReminderUtils";
import type {Config} from "@/dto/Config";
import dayjs from "dayjs";
import type {PersonFilter} from "@/dto/PersonFilter";
import {animateChevron, getBoolStr, getSortedGroups} from "@/utils/Global";
import type {GroupCompleteAttendee} from "@/dto/Group";
import {type Person, PersonStatus} from "@/dto/Person";

export default defineComponent({
  name: "ParticipantView",
  components: {AttendeesFilter, Pass, DateView, BiSendCheck, BiArrowUp, BiEnvelope, BiPencil, BiChatText, BiPeople},

  data() {
    return {
      participants: [] as Attendee[],
      allChecked: false,
      loading: false,
      filter: { status: PersonStatus.ATTENDEE } as PersonFilter,
      nbHoursBeforeRelease: 0,
      groups: [] as GroupCompleteAttendee[],
    }
  },

  computed: {
    checked(): Person[] {
      return this.groups.flatMap(g => g.members.filter(m => m.checked))
    }
  },

  created() {
    this.$watch(() => this.$route.params, () => this.load(), { immediate: true })
  },

  watch: {
    allChecked() {
      this.groups.forEach((g) => g.members.forEach(m => m.checked = this.allChecked))
    }
  },

  methods: {
    getBoolStr,
    animateChevron,
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

    load(filter?: PersonFilter) {
      axios.post("/groups/attendee/complete", filter ?? this.filter)
          .then(response => {
            const sortedGroups = getSortedGroups(response.data)
            this.groups = sortedGroups as GroupCompleteAttendee[]
          })
          .catch(err => console.error(err))

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
        toastSuccess(`La notification a bien été envoyée (${ids.length} personnes).`)
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
    },
    checkGroup(groupId: string, checked: boolean) {
      const group = this.groups.find(g => g.group.id === groupId)
      if (group) group.members.forEach(m => m.checked = checked)
    },
  }
})
</script>
