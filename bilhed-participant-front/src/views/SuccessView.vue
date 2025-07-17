<template>
  <div>
    <div class="row justify-content-center">
      <div class="col-md-12 col-lg-8 bg-light rounded-3 px-5 py-3 mb-5 mt-3" v-if="ref.firstname">
        <p class="lead text-center fw-bold">
          Bonne nouvelle {{ ref.firstname }}, vous avez été sélectionné pour la billetterie du BreizhCamp !
        </p>

        <p class="mt-4 mb-4" v-if="!dataTicket.hasTicket">
          Vous pouvez confirmer votre venue et acheter votre billet,
          ou bien libérer votre place pour qu'elle soit attribuée à une autre personne si vous n'êtes plus disponible.
        </p>

        <p class="mt-4 mb-4">
          Vous aviez choisi un pass
          <span v-if="ref.pass === 'TWO_DAYS'"><strong>2 jours</strong> (jeudi 27 et vendredi 28 juin)</span>
            <span v-else-if="ref.pass === 'THREE_DAYS'"><strong>3 jours</strong> (mercredi 26, jeudi 27 et vendredi 28 juin)</span>
          lors de votre inscription à la loterie.
        </p>

        <p class="fw-bold" v-if="!dataTicket.hasTicket">
          Vous avez jusqu'au <DateView :date="confirmInfos.confirmationLimitDate" /> pour confirmer votre choix.
        </p>
      </div>

      <div class="placeholder-glow" v-else-if="!error">
        <p class="lead text-center fw-bold mb-0">
          <span class="placeholder col-6"></span>
        </p>

        <p class="text-center">
          <span class="placeholder col-8"></span>
        </p>

        <p class="text-center">
          <span class="placeholder col-4"></span>
        </p>
      </div>
    </div>


    <div v-if="error" class="row justify-content-center" ref="error">
      <div class="col-md-8 alert alert-danger px-5 py-3 mb-5">
        <p class="lead text-center fw-bold mb-0">{{error}}</p>
      </div>
    </div>


    <div class="row" v-if="ref.firstname && !showForm && !showCancelConfirm">

      <div class="col-md-6 text-center mb-2">
        <button class="btn btn-light btn-lg" @click="cancelConfirm()" :disabled="loading">
          {{ confirmInfos.members.length > 1 ?
            `Mon groupe n'est plus disponible, libérer ${confirmInfos.members.length} places` :
            'Je ne suis plus disponible, libérer ma place' }}
        </button>
      </div>

      <div class="col-md-6 text-center mb-2">
        <button class="btn btn-primary btn-lg" @click="confirm()" :disabled="loading">
          {{ confirmInfos.members.length > 1 ?
            `Confirmer la venue du groupe et acheter ${confirmInfos.members.length} billets` :
            'Confirmer ma venue et acheter mon billet' }}
        </button>
      </div>

    </div>

    <div class="row justify-content-center" v-if="showCancelConfirm">
      <div class="col-md-6">
        <div class="row justify-content-center mt-4 mb-4">
          <h3 class="col-md-8 text-center">{{ confirmInfos.members.length > 1 ?
              `Libérez ${confirmInfos.members.length} places` :
              'Libérez ma place' }}</h3>
        </div>

        <div class="row text-center mb-3">
          <button type="button" class="btn btn-lg btn-primary" :disabled="loading" @click="cancel()">
            <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" v-if="loading"></span>
            {{ confirmInfos.members.length > 1 ?
              `Oui, je libère les ${confirmInfos.members.length} places d'autres personnes` :
              'Oui, je libère ma place pour une autre personne' }}
          </button>
        </div>

        <div class="row text-center mb-3">
          <button type="button" class="btn btn-lg btn-light" :disabled="loading" @click="back()">
            <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" v-if="loading"></span>
            Non, je reviens à la page précédente
          </button>
        </div>
      </div>
    </div>

    <form @submit.prevent="save()" v-if="showForm">
      <div class="row justify-content-center mb-3">
        <p class="col-md-8 text-center">
          {{ confirmInfos.members.length > 1 ?
            `Les billets sont nominatifs il n\'est pas possible de modifier les noms, prénoms ou e-mails.` :
            'Les billets sont nominatifs, il n\'est pas possible de modifier votre nom, prénom ou e-mail.' }}
          </p>
      </div>
      <div class="row justify-content-center" v-for="(mem, i) in confirmInfos.members" :key="mem.id">
      <h2 class="col-md-8 mb-3" v-if="confirmInfos.members.length > 1">{{ `Informations ${mem.firstname}` }}</h2>

        <div class="col-md-6">
          <div class="mb-3 row">
            <label :for="'lastname-'+mem.id" class="col-sm-3 col-form-label col-form-label-sm">Nom</label>
            <div class="col-sm-9">
              <input type="text" class="form-control form-control-sm" :name="'lastname-'+mem.id" :id="'lastname-'+mem.id" disabled v-model="mem.lastname">
            </div>
          </div>

          <div class="mb-3 row">
            <label :for="'firstname-'+mem.id" class="col-sm-3 col-form-label col-form-label-sm">Prénom</label>
            <div class="col-sm-9">
              <input type="text" class="form-control form-control-sm" :name="'firstname-'+mem.id" :id="'firstname-'+mem.id" disabled v-model="mem.firstname">
            </div>
          </div>

          <div class="mb-3 row">
            <label :for="'email-'+mem.id" class="col-sm-3 col-form-label col-form-label-sm">E-Mail</label>
            <div class="col-sm-9">
              <input type="email" class="form-control form-control-sm" :name="'email-'+mem.id" :id="'email-'+mem.id" disabled v-model="mem.email">
            </div>
          </div>

          <div class="mb-3 row">
            <label :for="'company-'+mem.id" class="col-sm-3 col-form-label col-form-label-sm">Entreprise</label>
            <div class="col-sm-9">
              <input type="text" class="form-control form-control-sm" :name="'company-'+mem.id" :id="'company-'+mem.id" :disabled="loading" v-model="attendeeData[i].company">
            </div>
          </div>

          <div class="mb-3 row">
            <label :for="'tShirtSize-'+mem.id" class="col-sm-3 col-form-label col-form-label-sm">Taille T-Shirt*</label>
            <div class="col-sm-9">
              <select class="form-select form-select-sm" :name="'tShirtSize-'+mem.id" :id="'tShirtSize-'+mem.id" required :disabled="loading" v-model="attendeeData[i].tShirtSize">
                <option></option>
                <option value="no">Je ne souhaite pas de T-Shirt</option>
                <option value="xs">XS</option>
                <option value="s">S</option>
                <option value="m">M</option>
                <option value="l">L</option>
                <option value="xl">XL</option>
                <option value="xxl">XXL</option>
                <option value="3xl">3XL</option>
              </select>

            </div>
          </div>

          <div class="mb-3 row" v-if="attendeeData[i].tShirtSize && attendeeData[i].tShirtSize != 'no'">
            <label :for="'tShirtCut-'+mem.id" class="col-sm-3 col-form-label col-form-label-sm">Coupe T-Shirt*</label>
            <div class="col-sm-9">
              <select class="form-select form-select-sm" :name="'tShirtCut-'+mem.id" :id="'tShirtCut-'+mem.id" required :disabled="loading" v-model="attendeeData[i].tShirtCut">
                <option></option>
                <option value="s">Droite</option>
                <option value="f">Cintrée</option>
              </select>
            </div>
          </div>

          <div class="mb-4 row">
            <label class="col-sm-3 col-form-label col-form-label-sm">Meet and Greet*</label>
            <div class="col-sm-9">
              <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" :name="'meetAndGreet-'+mem.id" :id="'meetAndGreetYes-'+mem.id" v-model="attendeeData[i].meetAndGreet" :value="true" required :disabled="loading">
                <label class="form-check-label" :for="'meetAndGreetYes-'+mem.id">Oui</label>
              </div>
              <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" :name="'meetAndGreet-'+mem.id" :id="'meetAndGreetNo-'+mem.id" v-model="attendeeData[i].meetAndGreet" :value="false" required :disabled="loading">
                <label class="form-check-label" :for="'meetAndGreetNo-'+mem.id">Non</label>
              </div>
              <div :id="'meetAndGreetHelp-'+mem.id" class="form-text">Souhaitez vous rester le jeudi soir (19h-21h) ? Le repas est offert.</div>
            </div>
          </div>

          <div class="mb-2 row">
            <label :for="'postalCode-'+mem.id" class="col-sm-3 col-form-label col-form-label-sm">Code postal</label>
            <div class="col-sm-9 mb-4">
              <input type="text" class="form-control form-control-sm" :name="'postalCode-'+mem.id" :id="'postalCode-'+mem.id" :disabled="loading" v-model="attendeeData[i].postalCode">
              <div :id="'postalCodeHelp-'+mem.id" class="form-text">Nous permet de savoir d'où vous venez.</div>
            </div>
            <hr>
          </div>
        </div>
      </div>
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="row text-center mb-3">
            <button type="submit" class="btn btn-lg btn-primary" :disabled="loading">
              <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" v-if="loading"></span>
              Enregistrer<span v-if="!dataTicket.hasPayed"> et payer</span>
            </button>
          </div>

          <div class="row text-center mb-3" v-if="!dataTicket.hasTicket">
            <button type="button" class="btn btn-lg btn-light" :disabled="loading" @click="back()">
              <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" v-if="loading"></span>
              Annuler et revenir à la page précédente
            </button>
          </div>

          <div class="row text-center">
            <p class="small"><router-link to="/data-usage" target="_blank">Utilisation des données personnelles</router-link></p>
          </div>
        </div>
      </div>
    </form>

  </div>
</template>

<script lang="ts">
import type {ConfirmRes} from '@/dto/ConfirmRes';
import type {PersonDataTicket} from '@/dto/PersonDataTicket';
import {defineComponent} from 'vue'
import type {AttendeeData, ConfirmInfos} from '@/dto/ConfirmInfos'
import DateView from '@/components/DateView.vue'
import type {AxiosResponse} from 'axios'
import axios from 'axios'
import type {Person} from "@/dto/Person";

export default defineComponent({
  name: "SuccessView",
  components: { DateView },

  data() {
    return {
      confirmInfos: {} as ConfirmInfos,
      attendeeData: [] as AttendeeData[],
      dataTicket: {} as PersonDataTicket,
      ref: {} as Person,
      loading: false,
      showForm: false,
      showCancelConfirm: false,
      payed: false,
      error: "",
    }
  },

  computed: {
    id() { return this.$route.params.id },
  },

  created() {
    this.$watch(() => this.$route.params, () => this.load(), { immediate: true })
  },

  methods: {
    load() {
      this.error = ""
      this.loading = true

      axios.get('/persons/' + this.id + '/ticket')
          .then(this.handleDataTicketRes)
          .then(res => { if (res) return this.loadParticipant() })
          .catch(this.displayError)
          .finally(() => this.loading = false)
    },

    async loadParticipant() {
      return axios.get('/participants/' + this.id)
          .then(res => {
            this.confirmInfos = res.data
            this.ref = res.data.members[0]
            for (const member of res.data.members)
              this.attendeeData.push({ id: member.id })
          })
    },

    /**
     * - Attendee data, ticket paid -> redirect confirm paid
     * - Attendee data, ticket not paid -> redirect to pay
     * - No attendee data, no ticket -> display form, then create ticket, then redirect to pay
     * - No attendee data, ticket not paid -> display form, then redirect to pay
     * - No attendee data, ticket paid -> display form, then redirect confirm paid
     *
     * Return true if participant is to load, false to load nothing
     */
    handleDataTicketRes(res: AxiosResponse<PersonDataTicket>): Promise<Boolean> {
      if (res.status == 204) {
        this.$router.push({ name: 'released' })
        return Promise.resolve(false)
      }

      this.dataTicket = res.data
      if (!this.dataTicket.hasAttendeeData){
        if (this.dataTicket.hasTicket) this.showForm = true
        return Promise.resolve(true);
      }

      if (this.dataTicket.hasPayed) {
        this.$router.push({name: 'ticket'})
      } else if (res.data.payUrl) {
        window.location.href = res.data.payUrl
      } else {
        this.error = "Une erreur est survenue, merci de contacter l'équipe pour finaliser la commande (pas d'URL de paiement avec un ticket non payé)"
      }
      return Promise.resolve(false)
    },

    confirm() {
      this.showForm = true
    },

    cancelConfirm() {
      this.showCancelConfirm = true
    },

    back() {
      this.showForm = false
      this.showCancelConfirm = false
    },

    save() {
      this.error = ""
      this.loading = true

      axios.post('/participants/confirm', this.attendeeData)
          .then(this.handleConfirm)
          .catch(this.displayError)
          .finally(() => this.loading = false)
    },

    cancel() {
      this.error = ""
      this.loading = true

      axios.post('/participants/' + this.id + '/cancel').then(() => {
        this.$router.push({ name: 'released' })
      }).catch(this.displayError)
        .finally(() => this.loading = false)
    },

    handleConfirm: function (res: AxiosResponse<ConfirmRes>) {
      if (res.data.payed) {
        this.$router.push({ name: 'ticket' })
      } else if (res.data.payUrl) {
        window.location.href = res.data.payUrl
      } else {
        this.error = "Une erreur est survenue lors de la récupération de l'URL de paiement, contactez l'équipe pour finaliser le paiement"
      }
    },

    displayError(err: any) {
      if (err.response.data && err.response.data.error) {
        this.error = err.response.data.error
      } else {
        this.error = "Une erreur est survenue, merci de réessayer dans quelques instants"
      }
    },
  }
})
</script>

<style scoped>

</style>
