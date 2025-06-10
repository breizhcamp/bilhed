<template>
	<div>
    <div class="row justify-content-center" v-if="isOpen">
      <div class="col-md-8 bg-light rounded-3 px-5 py-3 mb-5">
        <p class="lead text-center fw-bold mb-0">
          Inscription à la loterie du BreizhCamp 2024
        </p>
        <p class="text-center small">Du 26 au 28 juin 2024</p>

        <p>
          Victime de notre succès, et étant limité par la place disponible, l'équipe du BreizhCamp a décidé de mettre en place
            un tirage au sort pour l'achat des places.
        </p>

        <p>
          Vous pouvez remplir ce formulaire pour participer au tirage au sort.
        </p>

        <p>
          Une seule participation par personne physique / téléphone,<br>
          <strong>vous ne pourrez pas changer le nom du billet une fois validé.</strong><br>
          Si une tentative de contournement est détectée, les inscriptions seront sorties du tirage au sort.
        </p>

        <p class="lead text-center fw-bold">
            Fermeture des inscriptions le <DateView :date="config.closeDate" format="D MMMM à HH[h]mm" />
        </p>
      </div>
    </div>
    <ClosedMessage :loading="!config.closeDate" v-else />

    <div v-if="error" class="row justify-content-center" ref="error">
      <div class="col-md-8 alert alert-danger px-5 py-3 mb-5">
        <p class="lead text-center fw-bold mb-0">{{error}}</p>
      </div>
    </div>


    <form @submit.prevent="save()" v-if="isOpen">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="d-flex flex-row">
            <p>Type d'inscription : &nbsp;</p>
            <div class="form-check form-check-inline">
              <input class="form-check-input" type="radio" name="typeReg" id="alone" checked v-model="groupReg" :value="false">
              <label class="form-check-label" for="alone">Seul</label>
            </div>
            <div class="form-check form-check-inline">
              <input class="form-check-input" type="radio" name="typeReg" id="group" v-model="groupReg" :value="true">
              <label class="form-check-label" for="group">En groupe</label>
            </div>
          </div>

          <h2 v-if="groupReg" class="mb-4">Informations du référent</h2>
          <div class="mb-3 row">
            <label for="refLastname" class="col-sm-3 col-form-label">Nom {{ groupReg ? 'référent' : '' }}</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" name="refLastname" id="refLastname" required :disabled="loading" v-model="referent.lastname">
            </div>
          </div>

          <div class="mb-3 row">
            <label for="refFirstname" class="col-sm-3 col-form-label">Prénom {{ groupReg ? 'référent' : '' }}</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" name="refFirstname" id="refFirstname" required :disabled="loading" v-model="referent.firstname">
            </div>
          </div>

          <div class="mb-3 row">
            <label for="refEmail" class="col-sm-3 col-form-label">Email {{ groupReg ? 'référent' : '' }}</label>
            <div class="col-sm-9">
              <input type="email" class="form-control" name="refEmail" id="email" required :disabled="loading" v-model="referent.email">
            </div>
          </div>

          <div class="mb-4 row">
            <label for="refTelephone" class="col-sm-3 col-form-label">Tel. mobile {{ groupReg ? 'référent' : '' }}</label>
            <div class="col-sm-9">
              <input type="tel" class="form-control" name="refTelephone" id="telephone" placeholder="ex: 061234567 / Numéro fr uniquement"
                     required minlength="10" maxlength="10"
                     :disabled="loading" v-model="referent.telephone">
            </div>
            <div id="telephoneHelp" class="form-text text-end">Utilisé uniquement pour valider l'inscription et vous prévenir du tirage au sort.</div>
          </div>

          <fieldset class="mb-4 row">
            <legend class="col-sm-3 col-form-label">Billet souhaité</legend>
            <div class="col-sm-9">
              <div class="form-check">
                <input class="form-check-input" type="radio" name="pass" id="pass2j" value="TWO_DAYS" required v-model="referent.pass">
                <label class="form-check-label" for="pass2j">
                  2 jours / 75 € <small>(jeudi 27 et vendredi 28 juin)</small>
                </label>
              </div>

              <div class="form-check">
                <input class="form-check-input" type="radio" name="pass" id="pass3j" value="THREE_DAYS" required v-model="referent.pass">
                <label class="form-check-label" for="pass3j">
                  3 jours / 90 € <small>(mercredi 26, jeudi 27 et vendredi 28 juin)</small>
                </label>
              </div>

            </div>
          </fieldset>
          <fieldset class="mb-4 row" v-if="groupReg">
            <legend class="col-sm-3 col-form-label">Type de paiement</legend>
            <div class="col-sm-9">
              <div class="form-check">
                <input class="form-check-input" type="radio" name="groupPayment" id="paySep" :value="false" required v-model="groupPayment">
                <label class="form-check-label" for="paySep">
                  Séparé <small>(chaque membre paie son billet)</small>
                </label>
              </div>

              <div class="form-check">
                <input class="form-check-input" type="radio" name="groupPayment" id="payGr" :value="true" required v-model="groupPayment">
                <label class="form-check-label" for="payGr">
                  Groupé <small>(le référent paiera pour tout le groupe)</small>
                </label>
              </div>

            </div>
          </fieldset>
        </div>
      </div>
      <div v-if="groupReg" class="row justify-content-center">
        <div class="col-md-6">
          <hr>
          <h2 class="mb-4">Membres du groupe</h2>

          <div v-for="(comp, i) in companions" :key="'comp-'+i" class="bg-light rounded px-3 mb-3 py-1">
            <div class="d-flex flex-row justify-content-between mb-2 align-items-center">
              <h4>Membre {{ i+1 }}</h4>
              <button type="button" class="btn btn-danger rounded-circle" title="Supprimer le membre"
                      @click="() => deleteComp(i)"
                      style="--bs-btn-padding-y: .1rem; --bs-btn-padding-x: .3rem; --bs-btn-font-size: .75rem;">
                <i class="bi bi-trash"></i>
              </button>
            </div>
            <div class="mb-3 row">
              <label :for="'lastname-comp-'+i" class="col-sm-3 col-form-label">Nom </label>
              <div class="col-sm-9 align-content-center">
                <input type="text" class="form-control form-control-sm" :name="'lastname-comp-'+i" :id="'lastname-comp-'+i" required :disabled="loading" v-model="comp.lastname">
              </div>
            </div>

            <div class="mb-3 row">
              <label :for="'firstname-comp-'+i" class="col-sm-3 col-form-label">Prénom </label>
              <div class="col-sm-9 align-content-center">
                <input type="text" class="form-control form-control-sm" :name="'firstname-comp-'+i" :id="'firstname-comp-'+i" required :disabled="loading" v-model="comp.firstname">
              </div>
            </div>

            <div class="mb-3 row">
              <label :for="'email-comp-'+i" class="col-sm-3 col-form-label">Email </label>
              <div class="col-sm-9 align-content-center">
                <input type="email" class="form-control form-control-sm" :name="'email-comp-'+i" :id="'email-comp-'+i" required :disabled="loading" v-model="comp.email">
              </div>
            </div>

            <div class="mb-3 row" v-if="!groupPayment">
              <label :for="'tel-comp-'+i" class="col-sm-3 col-form-label">Tel </label>
              <div class="col-sm-9 align-content-center">
                <input type="tel" class="form-control form-control-sm" :name="'tel-comp-'+i" :id="'tel-comp-'+i" placeholder="ex: 061234567 / Numéro fr uniquement"
                       required minlength="10" maxlength="10"
                       :disabled="loading" v-model="comp.telephone">
              </div>
            </div>
          </div>

          <button type="button" class="btn btn-sm btn-block btn-info shadow rounded mb-4"
                  @click="addCompanion" style="width: 100%">Ajouter une personne</button>
        </div>
      </div>
      <div class="row justify-content-center">
        <div class="col-md-6">
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
import ClosedMessage from '@/components/ClosedMessage.vue';
import DateView from '@/components/DateView.vue';
import type {Config} from '@/dto/config';
import dayjs from 'dayjs';
import {defineComponent} from 'vue'
import axios from 'axios'
import type {PersonReq, ReferentReq} from "@/dto/PersonReq";

export default defineComponent({
  name: "RegisterView",
  components: {DateView, ClosedMessage},

  data() {
    return {
      loading: false,
      referent: {} as ReferentReq,
      groupPayment: true,
      companions: [] as PersonReq[],
      config: {} as Config,
      error: "",
      groupReg: false
    }
  },

  created() {
    this.loadConfig()
  },

  computed: {
    isOpen() {
      return this.config && dayjs(this.config.closeDate).isAfter(dayjs())
    }
  },

  methods: {
    loadConfig() {
      this.loading = true
      this.error = ""

      axios.get('/config').then(res => {
        this.config = res.data
      }).catch(this.displayError).finally(() => this.loading = false)
    },

    save() {
      this.loading = true
      this.error = ""

      const body = {
        referent: this.referent,
        groupPayment: this.groupPayment,
        companions: this.companions
      };

      axios.post('/register', body).then(res => {
        this.$router.push('/' + res.data.id)
      }).catch(this.displayError).finally(() => this.loading = false)
    },

    displayError(err: any) {
      if (err.response.data && err.response.data.error) {
        this.error = err.response.data.error
      } else {
        this.error = "Une erreur est survenue, merci de réessayer dans quelques instants"
      }

      let element = this.$refs['error'] as HTMLElement
      let top = element.offsetTop
      window.scrollTo(0, top)
    },

    addCompanion() {
      this.companions.push({ email: '', firstname: '', lastname: '', telephone: '' })
    },

    deleteComp(index: number) {
        this.companions.splice(index, 1)
    }
  }
})
</script>
