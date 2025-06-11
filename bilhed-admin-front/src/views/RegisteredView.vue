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
        <div class="col-md-2">Téléphone</div>
        <div class="col-md-auto">Pass</div>
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
        <!-- Checkbox -->
        <div class="form-check">
          <input class="form-check-input" type="checkbox" :id="`checkbox${g.group.id}`" v-model="g.checked"/>
        </div>

        <!-- Bouton accordéon custom -->
        <button
            type="button"
            class="btn flex-grow-1 text-start"
            :class="{ 'no-pointer-events': g.companions.length === 0 }"
            :data-bs-toggle="g.companions.length > 0 ? 'collapse' : null"
            :data-bs-target="`#collapseG${g.group.id}`"
            aria-expanded="false"
            :aria-controls="`collapseG${g.group.id}`"
            @click="g.companions.length > 0 ? animateChevron(`chevron-${g.group.id}`) : null"
        >
<!--          d-flex justify-content-between-->
          <span class=" row">
            <i :class="['bi', 'transition-icon', 'col-md-auto', g.companions.length > 0 ? 'bi-chevron-down' : 'bi-dash']" :id="`chevron-${g.group.id}`"></i>
            <span class="col-md-1">{{ g.referent.person.lastname }}</span>
            <span class="col-md-1">{{ g.referent.person.firstname }}</span>
            <span class="col-md-3">{{ g.referent.person.email }}</span>
            <span class="col-md-2">{{ g.referent.person.telephone }}</span>
            <span class="col-md-auto"><Pass :pass="g.referent.person.pass"/></span>
            <span class="col-md-3"><DateView :date="g.referent.referentInfos.registrationDate"/></span>
          </span>
        </button>
        <div>
          <button type="button" class="btn btn-link btn-sm" title="Send SMS reminder" @click="sendReminder(g.referent.person.id, 'sms')" :disabled="loading"><BiChatText/></button>
          <button type="button" class="btn btn-link btn-sm ms-1" title="Send email reminder" @click="sendReminder(g.referent.person.id, 'email')" :disabled="loading"><BiEnvelope/></button>
        </div>
      </div>

      <div
          :id="`collapseG${g.group.id}`"
          class="accordion-collapse collapse"
          :aria-labelledby="`headingG${g.group.id}`"
          data-bs-parent="#groupAccordion"
      >
        <div class="accordion-body py-1">

          <table class="table table-sm bg-transparent">
            <thead>
            <tr>
              <th>Nom</th>
              <th>Prénom</th>
              <th>Email</th>
              <th v-if="!g.group.groupPayment">Téléphone</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="comp in g.companions" :key="comp.id">
              <td>{{ comp.lastname }}</td>
              <td>{{ comp.firstname }}</td>
              <td>{{ comp.email }}</td>
              <td v-if="!g.group.groupPayment">{{ comp.telephone }}</td>
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
import Pass from "@/components/Pass.vue";
import {toastError, toastSuccess, toastWarning} from "@/utils/ReminderUtils";
import type {GroupComplete} from "@/dto/Group";

export default defineComponent({
  name: "RegisteredView",
  components: {Pass, DateView, BiChatText, BiEnvelope },

  data() {
    return {
      loading: false,
      allChecked: false,
      groups: [] as GroupComplete[]
    }
  },

  created() {
    this.$watch(() => this.$route.params, () => this.load(), { immediate: true })
  },

  watch: {
    allChecked() {
      this.groups.forEach((g) => g.checked = this.allChecked)
    }
  },

  methods: {
    load() {
      axios.post('/groups/complete/status', {status: "registered"}).then( response => {
        this.groups = response.data
      })
    },

    animateChevron(id: string) {
      const t = document.getElementById(id)
      if (t == null) return
      t.classList.contains("rotate-180") ? t.classList.remove("rotate-180") : t.classList.add("rotate-180")
    },

    sendReminder(id: string, type: string) {
      this.loading = true
      let data = {}
      // @ts-ignore
      data[type] = true

      axios.post(`/registered/${id}/reminder`, data).then(() => {
        this.load()
      }).finally(() => {
        this.loading = false
      })
    },

    levelUp() {
      const ids = this.groups.filter((g) => g.checked).map((g) => g.group.id)
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
    }
  }
})
</script>

<style scoped>
button.btn-sm {
  padding: 0;
  color: #212529;
  line-height: 0;
  vertical-align: text-top;
}
.transition-icon {
  transition: transform 0.3s ease;
}
.rotate-180 {
  transform: rotate(180deg);
}
table.bg-transparent thead,
table.bg-transparent tbody,
table.bg-transparent tr,
table.bg-transparent th,
table.bg-transparent td {
  background-color: transparent !important;
}
.btn.no-pointer-events {
  pointer-events: none;
  cursor: default;
}
</style>