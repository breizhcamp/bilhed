<template>
	<div>
    <div class="row justify-content-center">
      <div class="col-md-8 bg-light rounded-3 px-5 py-3 mb-5">
        <p class="lead text-center fw-bold mb-0">
          Inscription à la loterie du BreizhCamp 2023
        </p>
        <p class="text-center small">Du 28 au 30 juin 2023</p>

        <p class="text-center">
          Victime de notre succès, et étant limité par la place disponible, l'équipe du BreizhCamp à décidé de mettre en place
            un tirage au sort pour l'achat des places.
        </p>

        <p class="text-center">
          Vous pouvez remplir ce formulaire pour participer au tirage au sort.
        </p>

        <p class="text-center">
          Une seule participation par personne physique / téléphone,<br>
          <strong>vous ne pourrez pas changer le nom du billet une fois validé.</strong><br>
          Si une tentative de contournement est détectée, les inscriptions seront sorties du tirage au sort.
        </p>

        <p class="lead text-center fw-bold">
            Fermeture des inscriptions le 21 mai à 23h59
        </p>
      </div>
    </div>

    <div v-if="error" class="row justify-content-center" ref="error">
      <div class="col-md-8 alert alert-danger px-5 py-3 mb-5">
        <p class="lead text-center fw-bold mb-0">{{error}}</p>
      </div>
    </div>


    <form @submit.prevent="save()">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="mb-3 row">
            <label for="lastname" class="col-sm-3 col-form-label">Nom</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" name="lastname" id="lastname" required :disabled="loading" v-model="registered.lastname">
            </div>
          </div>

          <div class="mb-3 row">
            <label for="firstname" class="col-sm-3 col-form-label">Prénom</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" name="firstname" id="firstname" required :disabled="loading" v-model="registered.firstname">
            </div>
          </div>

          <div class="mb-3 row">
            <label for="email" class="col-sm-3 col-form-label">E-Mail</label>
            <div class="col-sm-9">
              <input type="email" class="form-control" name="email" id="email" required :disabled="loading" v-model="registered.email">
            </div>
          </div>

          <div class="mb-4 row">
            <label for="telephone" class="col-sm-3 col-form-label">Tel. mobile</label>
            <div class="col-sm-9">
              <input type="tel" class="form-control" name="telephone" id="telephone" placeholder="ex: 061234567 / Numéro fr uniquement"
                     required minlength="10" maxlength="10"
                     :disabled="loading" v-model="registered.telephone">
            </div>
            <div id="telephoneHelp" class="form-text text-end">Utilisé uniquement pour valider l'inscription et vous prévenir du tirage au sort.</div>
          </div>

          <fieldset class="mb-4 row">
            <legend class="col-sm-3 col-form-label">Billet souhaité</legend>
            <div class="col-sm-9">
              <div class="form-check">
                <input class="form-check-input" type="radio" name="pass" id="pass2j" value="TWO_DAYS" required v-model="registered.pass">
                <label class="form-check-label" for="pass2j">
                  2 jours / 75 € <small>(jeudi 29 et vendredi 30 juin)</small>
                </label>
              </div>

              <div class="form-check">
                <input class="form-check-input" type="radio" name="pass" id="pass3j" value="THREE_DAYS" required v-model="registered.pass">
                <label class="form-check-label" for="pass3j">
                  3 jours / 90 € <small>(mercredi 28, jeudi 29 et vendredi 30 juin)</small>
                </label>
              </div>

            </div>
          </fieldset>

          <div class="row" :class="{ 'mb-4': !haveKids, 'mb-2': haveKids }">
            <div class="col-sm-12">
              <div class="form-check">
                <input class="form-check-input" type="checkbox" id="kidsCheck" v-model="haveKids">
                <label class="form-check-label" for="kidsCheck">
                  Nous évaluons la possibilité d'une garde d'enfants pendant le BreizhCamp ; cochez cette case si la garde d'enfants aiderait à votre venue.
                </label>
              </div>
            </div>
          </div>

          <div class="mb-4 row" v-if="haveKids">
            <label for="kids" class="form-label">Veuillez renseigner les âges et les créneaux de garde nécessaires, s'il vous plaît :</label>
            <textarea class="form-control" id="kids" rows="4" required v-model="registered.kids" placeholder="Ex:
- 5 ans, mercredi de 14h à 18h
- 10 ans, jeudi de 18h à 20h30
etc...
"></textarea>
          </div>

          <div class="row text-center mb-3">
            <button type="submit" class="btn btn-lg btn-primary" :disabled="loading">
                <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" v-if="loading"></span>
                Participer au tirage au sort
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
import { Registered } from '@/dto/Registered';
import { defineComponent } from 'vue'
import axios from 'axios'

export default defineComponent({
  name: "RegisterView",

  data() {
    return {
      loading: false,
      registered: new Registered(),
      haveKids: false,
      error: ""
    }
  },

  methods: {
    save() {
      this.loading = true
      this.error = ""

      axios.post('/register', this.registered).then(res => {
        this.$router.push('/' + res.data.id)
      }).catch(this.displayError).finally(() => this.loading = false)
    },

    displayError(err: any) {
      if (err.data && err.data.error) {
        this.error = err.data.error
      } else {
        this.error = "Une erreur est survenue, merci de réessayer dans quelques instants"
      }

      let element = this.$refs['error']
      let top = element.offsetTop
      window.scrollTo(0, top)
    }
  }
})
</script>