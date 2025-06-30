<template>
  <h1>
    Participants

    <span class="d-inline-block float-end">
      <button type="button" class="btn btn-primary" v-on:click="draw()" :disabled="loading">Draw</button>
    </span>
  </h1>

  <div class="mb-3">
    <PersonsFilter :filter="filter" @filter="(f) => load(f)"/>

    <table class="table table-hover table-borderless">
      <thead>
      <tr>
        <th scope="col"><input type="checkbox" v-model="allChecked"></th>
        <th scope="col">Lastname</th>
        <th scope="col">Firstname</th>
        <th scope="col">Email</th>
        <th scope="col">Telephone</th>
        <th scope="col">Pass</th>
        <th scope="col">Or.</th>
        <th scope="col">Limit date</th>
        <th scope="col"></th>
      </tr>
      </thead>
      <tbody>
      <template v-for="(g, i) in groups" :key="g.group.id">
        <tr v-for="(member, iMemb) in g.members" :key="member.id" :class="{ 'table-secondary': i % 2 === 0 }"
            @click.exact="g.group.groupPayment && iMemb === 0 ? checkGroup(g.group.id, !member.checked) : checkPerson(g.group.groupPayment, member)"
            >

          <td><input type="checkbox" v-model="member.checked" :disabled="g.group.groupPayment && iMemb !== 0"
                     @change="g.group.groupPayment && iMemb === 0 ? checkGroup(g.group.id, member.checked) : null"></td>

          <td>{{ member.lastname }}</td>
          <td>{{ member.firstname }}</td>
          <td>{{ member.email }}</td>
          <td>{{ member.telephone }}</td>
          <td><Pass :pass="member.pass"/></td>
          <td>{{ g.group.drawOrder }}</td>
          <td><DateView :date="getLimitDate(g.group, member.id)" format="DD/MM HH:mm" sup=""/></td>
          <td class="d-flex align-items-center justify-content-end">
            <template v-if="!g.group.groupPayment || iMemb === 0">
              <button type="button" class="btn btn-link btn-sm text-dark" title="Notify success" @click="notifyOne(member.id, 'success')" :disabled="loading"><BiSendCheck/></button>
              <button type="button" class="btn btn-link btn-sm text-dark" title="Notify waiting" @click="notifyOne(member.id, 'waiting')" :disabled="loading"><BiSendExclamation/></button>
              <button type="button" class="btn btn-link btn-sm text-dark" title="Notify failed" @click="notifyOne(member.id, 'failed')" :disabled="loading"><BiSendX/></button>
            </template>
            <router-link :to="`/person/${member.id}`" class="nav-link ms-2"><BiPencil/></router-link>
            <router-link :to="`/group/${g.group.id}`" class="nav-link ms-2"><BiPeople/></router-link>
          </td>
        </tr>
      </template>
      </tbody>
    </table>

    <nav class="navbar sticky-bottom bg-light">
      <div class="container-fluid">
        <div>
          <button type="button" class="btn btn-primary me-1" @click="notifySel('success')" :disabled="loading"><BiSendCheck/> Notify success</button>
          <button type="button" class="btn btn-warning me-1" @click="notifySel('waiting')" :disabled="loading"><BiSendExclamation/> Notify waiting</button>
          <button type="button" class="btn btn-outline-danger me-4" @click="notifySel('failed')" :disabled="loading"><BiSendX/> Notify failed</button>

          <button type="button" class="btn btn-outline-primary me-4" @click="notifySel('success/reminder')" :disabled="loading"><BiSendCheck/> Remind success</button>

          <button type="button" class="btn btn-outline-primary me-1" @click="levelUp('attendee')" :disabled="loading"><BiArrowUp/> Level Up to attendee</button>
          <button type="button" class="btn btn-outline-warning me-1" @click="levelUp('release')" :disabled="loading"><BiArrowUp/> Level Up to release</button>

          <div class="d-inline-block ms-3" v-if="checked.length > 0">{{ checked.length }}/{{ groups.reduce((acc, g) => acc + g.members.length, 0) }}</div>
        </div>
      </div>
    </nav>
  </div>
</template>

<script lang="ts">
/// <reference types="vite-svg-loader" />

import axios from 'axios'
import BiSendCheck from 'bootstrap-icons/icons/send-check.svg?component'
import BiSendExclamation from 'bootstrap-icons/icons/send-exclamation.svg?component'
import BiSendX from 'bootstrap-icons/icons/send-x.svg?component'
import BiArrowUp from 'bootstrap-icons/icons/arrow-bar-up.svg?component'
import BiPencil from "bootstrap-icons/icons/pencil.svg?component";
import BiPeople from "bootstrap-icons/icons/people.svg?component";
import {defineComponent} from 'vue'
import Pass from '@/components/Pass.vue'
import PersonsFilter from '@/components/PersonsFilter.vue'
import type {PersonFilter} from '@/dto/PersonFilter'
import dayjs from "dayjs";
import type {Config} from "@/dto/Config";
import {toastError, toastSuccess, toastWarning, toInt} from "@/utils/ReminderUtils";
import type {Group, GroupCompleteParticipant} from "@/dto/Group";
import {type ParticipationInfos, type Person, PersonStatus} from "@/dto/Person";
import DateView from "@/components/DateView.vue";
import {getSortedGroups} from "@/utils/Global";

export default defineComponent({
  name: "ParticipantView",
  components: {DateView, PersonsFilter, Pass, BiSendCheck, BiSendExclamation, BiSendX, BiArrowUp, BiPencil, BiPeople},

  data() {
    return {
      allChecked: false,
      loading: false,
      filter: { status: PersonStatus.PARTICIPANT} as PersonFilter,
      reminderTimePar: {} as Config,
      groups: [] as GroupCompleteParticipant[],
      partInfos: [] as ParticipationInfos[]
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
    checkBetween(p: Person) {
      const first = this.groups.findIndex(g => g.members.some(m => m.checked))
      const clicked = this.groups.findIndex(g => g.members.some(m => m.id === p.id))
      if (first === -1) {
        p.checked = !p.checked
      } else {
        const min = Math.min(first, clicked)
        const max = Math.max(first, clicked)
        for (let i = min; i <= max; i++) {
          this.groups[i].members.forEach(m => m.checked = !m.checked)
        }
      }
    },

    load(formFilter?: PersonFilter) {
      axios.post("/groups/participant/complete", formFilter ?? this.filter)
          .then(response => {
            const sortedGroups = getSortedGroups(response.data)
            this.groups = sortedGroups as GroupCompleteParticipant[]

            if (sortedGroups.length > 0 && sortedGroups[0].group.drawOrder != null) {
              const ids = sortedGroups.map(e => e.group.id)
              return axios.post("/participations/groups", ids).then(res => {
                this.partInfos = res.data
              })
            }
          })
          .catch(err => console.error(err))

      axios.get('/config/reminderTimePar').then(res => {
        this.reminderTimePar = res.data
      })
    },

    draw() {
      if (!confirm("Vous allez d'effectuer le tirage au sort, voulez vous continuer ?"))
        return

      this.loading = true
      axios.post('/groups/draw').then(() => {
        this.load()
        toastSuccess("Le tirage au sort a bien été effectué.")
      }).catch(() => {
        toastError("Une erreur s'est produite lors du tirage au sort.")
      }).finally(() => {
        this.loading = false
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
        toastWarning("Aucun participant sélectionné")
        return
      }

      if (!confirm(`Voulez allez envoyer une notification à ${ids.length} personnes, voulez vous continuer ?`))
        return

      this.loading = true
      axios.post('/notifs/' + type, ids).then(() => {
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
        toastWarning("Aucun participant sélectionné")
        return
      }

      if (!confirm(`Voulez allez changer le statut de ${ids.length} personnes en ${level}, voulez vous continuer ?`))
        return

      this.loading = true
      axios.post('/notifs/levelUp/' + level, ids).then(() => {
        this.load()
        toastSuccess(`Le statut a bien été modifié (${ids.length} personnes).`)
      }).catch(() => {
        toastError("Une erreur s'est produite lors du changement de statut.")
      }).finally(() => {
        this.loading = false
      })
    },

    getSelected: function () {
      return this.groups.flatMap(g => {
        if (!g.group.groupPayment) {
          return g.members.filter(m => m.checked).map(m => m.id)
        } else {
          return g.members
              .filter(m => m.checked && m.id === g.group.referentId)
              .map(m => m.id)
        }
      })
    },

    checkGroup(groupId: string, checked: boolean) {
      const group = this.groups.find(g => g.group.id === groupId)
      if (group) group.members.forEach(m => m.checked = checked)
    },

    checkPerson(groupPayment: boolean, member: Person) {
      if (!groupPayment) {
        member.checked = !member.checked
      }
    },

    getLimitDate(group: Group, memberId: string): string {
      const isReferent = group.referentId === memberId

      let infos: ParticipationInfos | undefined = undefined;
      if (!group.groupPayment && !isReferent) {
        infos = this.partInfos.find(p => p.personId === memberId)
      } else {
        infos = this.partInfos.find(p => p.personId === group.referentId)
      }

      if (!infos || !infos.notificationConfirmSentDate) return ''

      let date = dayjs(infos.notificationConfirmSentDate)
      date = date.add(toInt(this.reminderTimePar.value), 'hour')
      return date.toString()
    }
  }
})
</script>
