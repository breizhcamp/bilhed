<template>
  <div v-if="error" class="row justify-content-center" ref="error">
    <div class="col-md-8 alert alert-danger px-5 py-3">
      <p class="lead text-center fw-bold mb-0">{{error}}</p>
    </div>
  </div>
  <section style="background-color: #bde4f3" class="rounded p-3 mb-3" v-if="!loading">
    <h2>Informations du groupe</h2>
    <table class="table table-borderless m-0 table-transparent">
      <thead>
      <tr>
        <th scope="col">Date d'inscription</th>
        <th scope="col">Date de participation</th>
        <th scope="col">Ordre tirage au sort</th>
        <th scope="col">Paiement groupé ?</th>
        <th scope="col">Pass</th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td><DateView :date="group.referentInfos.registrationDate" /></td>
        <td> TODO</td>
        <td>{{ group.group.drawOrder ?? 'Non tiré' }}</td>
        <td>{{ getBoolStr(group.group.groupPayment) }}</td>
        <td><Pass :pass="group.members[0].pass"/></td>
      </tr>
      </tbody>
    </table>
  </section>
  <section style="background-color: #EACBEA" class="rounded p-3 mb-3" v-if="!loading">
    <h2>Informations du référent {{ group.members[0].firstname }} {{ group.members[0].lastname }}</h2>
    <!--    infos globales -->
    <table class="table table-borderless m-0 table-transparent">
      <thead>
      <tr>
        <th scope="col">Status SMS</th>
        <th scope="col">Nb Sms envoyé</th>
        <th scope="col">Date dernier Sms envoyé</th>
        <th scope="col">Sms erreur</th>
        <th scope="col">Token</th>
        <th scope="col">Nb essais token</th>
        <th scope="col">Email</th>
        <th scope="col">Telephone</th>
        <th scope="col"></th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td>{{ group.referentInfos.smsStatus }}</td>
        <td>{{ group.referentInfos.nbSmsSent }}</td>
        <td><DateView :date="group.referentInfos.lastSmsSentDate" /></td>
        <td>{{ group.referentInfos.smsError }}</td>
        <td>{{ group.referentInfos.token }}</td>
        <td>{{ group.referentInfos.nbTokenTries }}</td>
        <td><input class="form-control form-control-sm" type="text" id="mail" v-model="group.members[0].email" /></td>
        <td><input class="form-control form-control-sm" type="text" id="phone" v-model="group.members[0].telephone" /></td>
        <td>
          <button type="button" class="btn btn-primary btn-sm" title="Modify Person" @click="updateRef()">Modifier</button>
        </td>
      </tr>
      </tbody>
    </table>
  </section>
  <section style="background-color: #EACBEA" class="rounded p-3 mb-3" v-if="!loading && group.group.drawOrder">
    <h2>Informations du référent {{ group.members[0].firstname }} {{ group.members[0].lastname }}</h2>
    <!--    infos globales -->
    <table class="table table-borderless m-0 table-transparent">
      <thead>
      <tr>
        <th scope="col">Status SMS</th>
        <th scope="col">Nb Sms envoyé</th>
        <th scope="col">Date dernier Sms envoyé</th>
        <th scope="col">Sms erreur</th>
        <th scope="col">Token</th>
        <th scope="col">Nb essais token</th>
        <th scope="col">Email</th>
        <th scope="col">Telephone</th>
        <th scope="col"></th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td>{{ group.referentInfos.smsStatus }}</td>
        <td>{{ group.referentInfos.nbSmsSent }}</td>
        <td><DateView :date="group.referentInfos.lastSmsSentDate" /></td>
        <td>{{ group.referentInfos.smsError }}</td>
        <td>{{ group.referentInfos.token }}</td>
        <td>{{ group.referentInfos.nbTokenTries }}</td>
        <td><input class="form-control form-control-sm" type="text" id="mail" v-model="group.members[0].email" /></td>
        <td><input class="form-control form-control-sm" type="text" id="phone" v-model="group.members[0].telephone" /></td>
        <td>
          <button type="button" class="btn btn-primary btn-sm" title="Modify Person" @click="updateRef()">Modifier</button>
        </td>
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

export default defineComponent({
  name: "GroupView",
  components: {DateView, Pass},

  data() {
    return {
      group: { } as GroupCompleteParticipant,
      error: "",
      loading: true
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
      }).finally(() => this.loading = false)
      // TODO : recup les reminders pour chaque pers ?
      // axios.get(`/person/${this.$route.params.id}/reminders`).then(rem => {
      //   this.reminders = rem.data
      // })
    },

    updateRef() {

    }
  }
})
</script>

<style scoped>

</style>