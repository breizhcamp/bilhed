<template>
  <div v-if="error" class="row justify-content-center" ref="error">
    <div class="col-md-8 alert alert-danger px-5 py-3">
      <p class="lead text-center fw-bold mb-0">{{error}}</p>
    </div>
  </div>
  <section style="background-color: #bde4f3" class="rounded p-3 mb-3" v-if="!loading">
    <h2>Membres du groupe ({{ group.members.length }})</h2>
    <table class="table m-0 table-transparent border-dark">
      <thead>
      <tr>
        <th scope="col">Nom</th>
        <th scope="col">Prénom</th>
        <th scope="col">Adresse mail</th>
        <th scope="col">Téléphone</th>
        <th scope="col">Date de participation</th>
        <th scope="col">Payé ?</th>
        <th scope="col" class="text-end">Action</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="comp in group.members" :key="comp.id">
        <td>{{ comp.lastname }}</td>
        <td>{{ comp.firstname }}</td>
        <td class="break-email">{{ comp.email }}</td>
        <td>{{ comp.telephone }}</td>
        <td><DateView :date="getConfirmationDate(comp.id)" format="DD/MM HH:mm" sup="" /></td>
        <td>{{ getBoolStr(comp.payed) }}</td>
        <td class="p-0 align-middle text-end">
          <router-link :to="`/person/${comp.id}`" class="nav-link ms-1  icon-small"><BiPencil/></router-link>
        </td>
      </tr>
      </tbody>
    </table>
  </section>

  <section style="background-color: #F8D2B2" class="rounded p-3 mb-3" v-if="!loading">
    <h2>Informations Générales</h2>

    <div class="container">
      <div class="row fw-bold">
        <div class="col">Date d'inscription</div>
        <div class="col">Ordre tirage au sort</div>
        <div class="col">Paiement groupé ?</div>
        <div class="col">Pass</div>
      </div>
      <div class="row">
        <div class="col"><DateView :date="group.referentInfos.registrationDate" /></div>
        <div class="col">{{ group.group.drawOrder ?? 'Non tiré' }}</div>
        <div class="col">{{ getBoolStr(group.group.groupPayment) }}</div>
        <div class="col"><Pass :pass="group.members[0].pass"/></div>
      </div>
    </div>


  </section>

  <section style="background-color: #EACBEA" class="rounded p-3 mb-3" v-if="!loading">
    <h2>Inscription</h2>
    <div class="container">
      <div class="row fw-bold">
        <div class="col">Status sms</div>
        <div class="col">Nb sms envoyés</div>
        <div class="col">Date dernier sms envoyé</div>
        <div class="col">Sms erreur</div>
        <div class="col">Token</div>
        <div class="col">Nb essais token</div>
      </div>
      <div class="row">
        <div class="col">{{ group.referentInfos.smsStatus }}</div>
        <div class="col">{{ group.referentInfos.nbSmsSent }}</div>
        <div class="col"><DateView :date="group.referentInfos.lastSmsSentDate" sup=""/></div>
        <div class="col">{{ group.referentInfos.smsError }}</div>
        <div class="col">{{ group.referentInfos.token }}</div>
        <div class="col">{{ group.referentInfos.nbTokenTries }}</div>
      </div>
    </div>
  </section>

  <section style="background-color: #F6F5CBFF" class="rounded p-3 mb-3" v-if="!loading && partInfos.length > 0">
    <h2>Participation</h2>
    <table class="table border-dark m-0 table-transparent">
      <thead>
      <tr>
        <th scope="col">Nom</th>
        <th scope="col">Status SMS</th>
        <th scope="col">Nb Sms envoyés</th>
        <th scope="col">Sms erreur</th>
        <th scope="col">Date envoi notification</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="pi in partInfos" :key="pi.personId">
        <td>{{ group.members.find(m => m.id === pi.personId)?.lastname }}</td>
        <td>{{ pi.smsStatus }}</td>
        <td>{{ pi.nbSmsSent }}</td>
        <td>{{ pi.smsError }}</td>
        <td><DateView :date="pi.notificationConfirmSentDate" /></td>
      </tr>
      </tbody>
    </table>
  </section>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import Pass from "@/components/Pass.vue";
import type {GroupCompleteParticipant} from "@/dto/Group";
import axios from "axios";
import DateView from "@/components/DateView.vue";
import {getBoolStr} from "@/utils/Global";
import BiPencil from "bootstrap-icons/icons/pencil.svg?component";
import type {ParticipationInfos} from "@/dto/Person";

export default defineComponent({
  name: "GroupView",
  components: {DateView, Pass, BiPencil},

  data() {
    return {
      group: { } as GroupCompleteParticipant,
      error: "",
      loading: true,
      partInfos: [] as ParticipationInfos[]
    }
  },

  created() {
    this.$watch(() => this.$route.params, () => this.load(), {immediate: true})
  },

  methods: {
    getBoolStr,
    load() {
      axios.get(`/groups/${this.$route.params.id}/complete`).then(groupRes => {
        this.group = groupRes.data
        if (groupRes.data.group.drawOrder != null) {
          axios.get(`/participations/group/${groupRes.data.group.id}`).then(res => {
            this.partInfos = res.data
          })
        }
      }).finally(() => this.loading = false)
    },

    getConfirmationDate(personId: string): string | undefined {
      /**
       * Récupère la date de confirmation dans le part Infos de la person
       * Si pas de partInfos (= groupPayment), on prend la date du référent
       */
      if (this.partInfos.length === 0) return undefined
      return this.partInfos.find(pi => pi.personId === personId)?.confirmationDate || this.partInfos[0].confirmationDate
    }
  }
})
</script>

<style scoped>

</style>