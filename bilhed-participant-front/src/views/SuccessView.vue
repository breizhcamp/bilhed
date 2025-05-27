<template>
  <div>
    <div class="row justify-content-center">
      <div class="col-md-12 col-lg-8 bg-light rounded-3 px-5 py-3 mb-5 mt-3" v-if="participant.firstname">
        <p class="lead text-center fw-bold">
          Bonne nouvelle {{participant.firstname}}, votre place pour la billeterie du BreizhCamp a été tirée au sort !
        </p>

        <p class="mt-4 mb-4" v-if="!dataTicket.hasTicket">
          Vous pouvez confirmer votre venue et acheter votre billet,
          ou bien libérer votre place pour qu'elle soit attribuée à une autre personne si vous n'êtes plus disponible.
        </p>

        <p class="mt-4 mb-4">
          Vous aviez choisi un pass
          <span v-if="participant.pass === 'TWO_DAYS'"><strong>2 jours</strong> (jeudi 27 et vendredi 28 juin)</span>
            <span v-else-if="participant.pass === 'THREE_DAYS'"><strong>3 jours</strong> (mercredi 26, jeudi 27 et vendredi 28 juin)</span>
          lors de votre inscription à la loterie.
        </p>

        <p class="fw-bold" v-if="!dataTicket.hasTicket">
          Vous avez jusqu'au <DateView :date="participant.confirmationLimitDate" /> pour confirmer votre choix.
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


    <div class="row" v-if="participant.firstname && !showForm && !showCancelConfirm">

      <div class="col-md-6 text-center mb-2">
        <button class="btn btn-light btn-lg" @click="cancelConfirm()" :disabled="loading">
          Je ne suis plus disponible, libérer ma place
        </button>
      </div>

      <div class="col-md-6 text-center mb-2">
        <button class="btn btn-primary btn-lg" @click="confirm()" :disabled="loading">
          Confirmer ma venue et acheter mon billet
        </button>
      </div>

    </div>

    <div class="row justify-content-center" v-if="showCancelConfirm">
      <div class="col-md-6">
        <div class="row justify-content-center mt-4 mb-4">
          <h3 class="col-md-8 text-center">Libérez ma place ?</h3>
        </div>

        <div class="row text-center mb-3">
          <button type="button" class="btn btn-lg btn-primary" :disabled="loading" @click="cancel()">
            <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" v-if="loading"></span>
            Oui, je libère ma place pour une autre personne
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
        <p class="col-md-8 text-center">Les billets sont nominatifs, il n'est pas possible de modifier votre nom, prénom ou e-mail.</p>
      </div>

      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="mb-3 row">
            <label for="lastname" class="col-sm-3 col-form-label">Nom</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" name="lastname" id="lastname" disabled v-model="participant.lastname">
            </div>
          </div>

          <div class="mb-3 row">
            <label for="firstname" class="col-sm-3 col-form-label">Prénom</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" name="firstname" id="firstname" disabled v-model="participant.firstname">
            </div>
          </div>

          <div class="mb-3 row">
            <label for="email" class="col-sm-3 col-form-label">E-Mail</label>
            <div class="col-sm-9">
              <input type="email" class="form-control" name="email" id="email" disabled v-model="participant.email">
            </div>
          </div>

          <div class="mb-3 row">
            <label for="company" class="col-sm-3 col-form-label">Entreprise</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" name="company" id="company" :disabled="loading" v-model="participant.company">
            </div>
          </div>

          <div class="mb-3 row">
            <label for="tShirtSize" class="col-sm-3 col-form-label">Taille T-Shirt*</label>
            <div class="col-sm-9">
              <select class="form-select" name="tShirtSize" id="tShirtSize" required :disabled="loading" v-model="participant.tShirtSize">
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

          <div class="mb-3 row" v-if="participant.tShirtSize && participant.tShirtSize != 'no'">
            <label for="tShirtCut" class="col-sm-3 col-form-label">Coupe T-Shirt*</label>
            <div class="col-sm-9">
              <select class="form-select" name="tShirtCut" id="tShirtCut" required :disabled="loading" v-model="participant.tShirtCut">
                <option></option>
                <option value="s">Droite</option>
                <option value="f">Cintrée</option>
              </select>
            </div>
          </div>

          <div class="mb-4 row">
            <label class="col-sm-3 col-form-label">Meet and Greet*</label>
            <div class="col-sm-9">
              <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="meetAndGreet" id="meetAndGreetYes" v-model="participant.meetAndGreet" :value="true" required :disabled="loading">
                <label class="form-check-label" for="meetAndGreetYes">Oui</label>
              </div>
              <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="meetAndGreet" id="meetAndGreetNo" v-model="participant.meetAndGreet" :value="false" required :disabled="loading">
                <label class="form-check-label" for="meetAndGreetNo">Non</label>
              </div>
              <div id="meetAndGreetHelp" class="form-text">Souhaitez vous rester le jeudi soir (19h-21h) ? Le repas est offert.</div>
            </div>
          </div>

          <div class="mb-4 row">
            <label for="postalCode" class="col-sm-3 col-form-label">Code postal</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" name="postalCode" id="postalCode" :disabled="loading" v-model="participant.postalCode">
              <div id="postalCodeHelp" class="form-text">Nous permet de savoir d'où vous venez.</div>
            </div>
          </div>

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
import type { ConfirmRes } from '@/dto/ConfirmRes';
import type { PersonDataTicket } from '@/dto/PersonDataTicket';
import { defineComponent } from 'vue'
import { Participant } from '@/dto/Participant'
import DateView from '@/components/DateView.vue'
import type { AxiosResponse } from 'axios'
import axios from 'axios'

export default defineComponent({
  name: "SuccessView",
  components: { DateView },

  data() {
    return {
      participant: new Participant(),
      dataTicket: {} as PersonDataTicket,
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

    loadParticipant() {
      return axios.get('/participants/' + this.id)
          .then(res => this.participant = res.data)
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

      axios.post('/participants/' + this.id + '/confirm', this.participant)
          .then(this.handleConfirm)
          .catch(this.displayError)
          .finally(() => this.loading = false)
    },

    cancel() {
      this.error = ""
      this.loading = true

      axios.post('/participants/' + this.id + '/cancel').then(res => {
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
