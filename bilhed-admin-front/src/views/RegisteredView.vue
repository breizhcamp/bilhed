<template>
  <h1>
    Inscrits
  </h1>

  <div class="d-flex align-items-center p-2 fw-bold mb-2">
    <div class="form-check me-3">
      <input type="checkbox" class="form-check-input" v-model="allChecked" />
    </div>
    <div class="flex-grow-1">
      <div class="row">
        <div class="col-md-auto"><i class="bi col-md-auto bi-dash"></i></div>
        <div class="col-md-1">Nom</div>
        <div class="col-md-1">Prénom</div>
        <div class="col-md-3">Email</div>
        <div style="width: 15%">Téléphone</div>
        <div class="col-md-1">Pass</div>
        <div class="col-md-3">Date inscription</div>
      </div>
    </div>
    <div class="form-check me-1">
      Actions
    </div>
  </div>

  <div class="accordion mb-3" id="groupAccordion">
    <div v-for="(g, i) in groups" :key="g.group.id" :class="['accordion-item', { 'bg-body-secondary': i % 2 === 0 }]">
      <div class="d-flex align-items-center p-2">
        <div class="form-check">
          <input class="form-check-input" type="checkbox" :id="`checkbox${g.group.id}`" v-model="g.members[0].checked" @change="checkGroup(g.group.id, g.members[0].checked)" />
        </div>

        <button
            type="button"
            class="btn flex-grow-1 text-start me-2"
            :class="{ 'no-pointer-events': g.members.length === 1 }"
            :data-bs-toggle="g.members.length > 1 ? 'collapse' : null"
            :data-bs-target="`#collapseG${g.group.id}`"
            aria-expanded="false"
            :aria-controls="`collapseG${g.group.id}`"
            @click="g.members.length > 1 ? animateChevron(`chevron-${g.group.id}`) : null"
        >
          <span class="row">
            <i :class="g.members.length > 1 ? 'bi-chevron-down' : 'bi-dash'" class="bi transition-icon col-md-auto" :id="`chevron-${g.group.id}`"></i>
            <span class="col-md-1">{{ g.members[0].lastname }}</span>
            <span class="col-md-1">{{ g.members[0].firstname }}</span>
            <span class="col-md-3 break-email">{{ g.members[0].email }}</span>
            <span class="col-md-2">{{ g.members[0].telephone }}</span>
            <span class="col-md-1"><Pass :pass="g.members[0].pass"/></span>
            <span class="col-md-3"><DateView :date="g.referentInfos.registrationDate" format="DD/MM HH:mm"/></span>
          </span>
        </button>
        <div class="d-flex">
          <button type="button" class="btn btn-link btn-sm icon-small" title="Send SMS reminder" @click="sendReminder(g.members[0].id, 'sms')" :disabled="loading"><BiChatText/></button>
          <button type="button" class="btn btn-link btn-sm ms-2 icon-small" title="Send email reminder" @click="sendReminder(g.members[0].id, 'email')" :disabled="loading"><BiEnvelope/></button>
          <router-link :to="`/person/${g.members[0].id}`" class="nav-link ms-2 d-flex align-items-center icon-small"><BiPencil/></router-link>
          <router-link :to="`/group/${g.group.id}`" class="nav-link ms-2 d-flex align-items-center icon-small"><BiPeople/></router-link>
        </div>
      </div>

      <div
          :id="`collapseG${g.group.id}`"
          class="accordion-collapse collapse"
          :aria-labelledby="`headingG${g.group.id}`"
      >
        <div class="accordion-body py-1">

          <table class="table table-sm table-transparent">
            <thead>
            <tr>
              <th scope="col">Nom</th>
              <th scope="col">Prénom</th>
              <th scope="col">Email</th>
              <th scope="col" v-if="!g.group.groupPayment">Téléphone</th>
              <th scope="col"></th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="comp in g.members.slice(1)" :key="comp.id">
              <td style="width: 20%">{{ comp.lastname }}</td>
              <td style="width: 20%">{{ comp.firstname }}</td>
              <td style="width: 20%" class="break-email">{{ comp.email }}</td>
              <td style="width: 20%" v-if="!g.group.groupPayment">{{ comp.telephone }}</td>
              <td style="width: 20%" class="p-0 align-middle text-end">
                <router-link :to="`/person/${comp.id}`" class="nav-link ms-1 icon-small"><BiPencil/></router-link>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

  <div class="mb-3">
    <button type="button" class="btn btn-primary" v-on:click="levelUp()" :disabled="loading">Level up to participant</button>
  </div>


</template>

<script lang="ts">
/// <reference types="vite-svg-loader" />

import {defineComponent} from 'vue'
import axios from 'axios'
import DateView from '@/components/DateView.vue'
import BiChatText from 'bootstrap-icons/icons/chat-text.svg?component'
import BiEnvelope from 'bootstrap-icons/icons/envelope.svg?component'
import BiPencil from 'bootstrap-icons/icons/pencil.svg?component'
import BiPeople from "bootstrap-icons/icons/people.svg?component";
import Pass from "@/components/Pass.vue";
import {toastError, toastSuccess, toastWarning} from "@/utils/ReminderUtils";
import type {GroupCompleteParticipant} from "@/dto/Group";
import {PersonStatus} from "@/dto/Person";
import {animateChevron} from "@/utils/Global";

export default defineComponent({
  name: "RegisteredView",
  components: {Pass, DateView, BiChatText, BiEnvelope, BiPencil, BiPeople},

  data() {
    return {
      loading: false,
      allChecked: false,
      groups: [] as GroupCompleteParticipant[]
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
    animateChevron,
    load() {
      axios.post('/groups/participant/complete', {status: PersonStatus.REGISTERED}).then( response => {
        this.groups = response.data
      })
    },

    sendReminder(id: string, type: string) {
      this.loading = true
      let data = {}
      // @ts-ignore
      data[type] = true

      axios.post(`/notifs/registered/${id}/reminder`, data).then(() => {
        this.load()
      }).finally(() => {
        this.loading = false
      })
    },

    levelUp() {
      const ids = this.groups.flatMap(g => g.members.filter(m => m.checked)).map(m => m.id);

      if (ids.length === 0) {
        toastWarning("Aucun groupe sélectionné")
        return
      }

      if (!confirm(`Voulez allez changer le statut de ${ids.length} groupes en Participant, voulez vous continuer ?`))
        return

      this.loading = true
      axios.post('/groups/levelUp', ids).then(() => {
        this.load()
        toastSuccess(`Le statut a bien été modifié (${ids.length} groupes).`)
      }).catch(() => {
        toastError("Une erreur s'est produite lors du changement de statut.")
      }).finally(() => {
        this.loading = false
      })
    },

    checkGroup(groupId: string, checked: boolean) {
      const group = this.groups.find(g => g.group.id === groupId)
      if (group) group.members.forEach(m => m.checked = checked)
    }
  }
})
</script>

<style scoped>

</style>