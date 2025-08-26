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
            Fermeture des inscriptions le <DateView :date="closeDate" format="D MMMM à HH[h]mm" />
        </p>
      </div>
    </div>
    <ClosedMessage :loading="!closeDate" v-else />

    <form @submit.prevent="save()" v-if="isOpen">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <fieldset class="mb-4 row">
            <legend class="col-sm-3 col-form-label">Type d'inscription :</legend>
            <div class="col-sm-9">
              <div class="form-check">
                <input class="form-check-input" type="radio" name="typeReg" id="alone" :value="false" :disabled="loading" required v-model="groupReg">
                <label class="form-check-label" for="alone">
                  Seul <small></small>
                </label>
              </div>

              <div class="form-check">
                <input class="form-check-input" type="radio" name="typeReg" id="group" :value="true" :disabled="loading" required v-model="groupReg">
                <label class="form-check-label" for="group">
                  En groupe <small>(Soit tout le groupe est tiré au sort, soit personne)</small>
                </label>
              </div>

            </div>
          </fieldset>
          <hr>

          <h2 v-if="groupReg" class="mb-1">Informations du référent</h2>
          <p v-if="groupReg" class="mb-4"><i>Le référent sera le seul interlocuteur du groupe pour <b>l'inscription</b> au tirage au sort.</i></p>
          <div class="mb-3 row">
            <label for="refLastname" class="col-sm-3 col-form-label">Nom {{ groupReg ? 'référent' : '' }}</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" name="refLastname" id="refLastname" required :disabled="loading" v-model="r.lastname">
            </div>
          </div>

          <div class="mb-3 row">
            <label for="refFirstname" class="col-sm-3 col-form-label">Prénom {{ groupReg ? 'référent' : '' }}</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" name="refFirstname" id="refFirstname" required :disabled="loading" v-model="r.firstname">
            </div>
          </div>

          <div class="mb-3 row">
            <label for="refEmail" class="col-sm-3 col-form-label">Email {{ groupReg ? 'référent' : '' }}</label>
            <div class="col-sm-9">
              <input type="email" class="form-control" name="refEmail" id="email" required :disabled="loading" v-model="r.email">
            </div>
          </div>

          <div class="mb-4 row">
            <label for="refTelephone" class="col-sm-3 col-form-label">Tel. mobile {{ groupReg ? 'référent' : '' }}</label>
            <div class="col-sm-9">
              <input type="tel" class="form-control" name="refTelephone" id="telephone" placeholder="ex: 061234567 / Numéro fr uniquement"
                     required minlength="10" maxlength="10"
                     :disabled="loading" v-model="r.telephone">
            </div>
            <div id="telephoneHelp" class="form-text text-end">Utilisé uniquement pour valider l'inscription et vous prévenir du tirage au sort.</div>
          </div>

          <fieldset class="mb-4 row">
            <legend class="col-sm-3 col-form-label">Billet souhaité</legend>
            <div class="col-sm-9">
              <div class="form-check">
                <input class="form-check-input" type="radio" name="pass" id="pass2j" :value="PassType.TWO_DAYS" :disabled="loading" required v-model="p">
                <label class="form-check-label" for="pass2j">
                  2 jours / 75 € <small>(jeudi 27 et vendredi 28 juin)</small>
                </label>
              </div>

              <div class="form-check">
                <input class="form-check-input" type="radio" name="pass" id="pass3j" :value="PassType.THREE_DAYS" :disabled="loading" required v-model="p">
                <label class="form-check-label" for="pass3j">
                  3 jours / 90 € <small>(mercredi 26, jeudi 27 et vendredi 28 juin)</small>
                </label>
              </div>

            </div>
          </fieldset>
        </div>
      </div>
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="row text-center mb-3">
            <button type="submit" class="btn btn-lg" :class="groupReg ? 'btn-primary': 'btn-success'" :disabled="loading">
              <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" v-if="loading"></span>
              {{ groupReg ? 'Ajouter des accompagnants' : 'Participer au tirage au sort' }}
            </button>
          </div>
        </div>
      </div>
    </form>
	</div>
</template>

<script lang="ts">
import ClosedMessage from '@/components/ClosedMessage.vue';
import DateView from '@/components/DateView.vue';
import dayjs from 'dayjs';
import {defineComponent, type PropType} from 'vue'
import type {PersonReq} from "@/dto/Person";
import {PassType} from "@/dto/ConfirmInfos";

export default defineComponent({
  name: "ReferentStep",
  components: {DateView, ClosedMessage},
  emits: ['saveRef'],
  props: {
    loading: { type: Boolean, required: true },
    closeDate: { type: String, required: true, default: '' },
    referent: { type: Object as PropType<PersonReq>, required: true },
    groupRegistration: { type: Boolean, required: true },
    pass: { type: String as PropType<PassType>, required: true },
  },

  data() {
    return {
      r: this.referent,
      groupReg: this.groupRegistration,
      p: this.pass
    }
  },

  computed: {
    PassType() {
      return PassType
    },
    isOpen() {
      return this.closeDate && dayjs(this.closeDate).isAfter(dayjs())
    }
  },

  methods: {
    save() {
      this.$emit('saveRef', {ref: this.r, groupReg: this.groupReg, pass: this.p})
    },
  }
})
</script>
