<template>
  <div class="row justify-content-center">
    <div class="col-md-6">
      <div class="my-2">
        <button type="button" class="btn btn-outline-dark rounded d-flex align-items-center"
                @click="backToRef" :disabled="loading">
          <BiArrowLeftCircle class="me-2"/>
          Revenir au référent</button>
      </div>
      <h2 class="mb-4">Membres du groupe</h2>
      <div class="alert alert-primary" role="alert">
       Le groupe est constitué du référent et d'accompagnants <br> <b>Inutile de mettre le référent en accompagnant !</b>
      </div>
      <!-- Referent -->
      <div class="bg-light rounded px-3 mb-3 py-1">
        <h4>Rappel du référent</h4>

        <div class="mb-3 row">
          <p class="mb-0 col-sm-3 align-self-center">Nom </p>

          <div class="col-sm-9 align-content-center">
            <input type="text" class="form-control form-control-sm" required disabled :value="referent.lastname">
          </div>
        </div>

        <div class="mb-3 row">
          <p class="mb-0 col-sm-3 align-self-center">Prénom </p>
          <div class="col-sm-9 align-content-center">
            <input type="text" class="form-control form-control-sm" required disabled :value="referent.firstname">
          </div>
        </div>

        <div class="mb-3 row">
          <p class="mb-0 col-sm-3 align-self-center">Email </p>
          <div class="col-sm-9 align-content-center">
            <input type="text" class="form-control form-control-sm" required disabled :value="referent.email">
          </div>
        </div>

        <div class="mb-3 row">
          <p class="mb-0 col-sm-3 align-self-center">Pass </p>
          <div class="col-sm-9 align-content-center">
            <p class="mb-0">{{ getPassString(pass) }}</p>
          </div>
        </div>

        <div class="mb-3 row">
          <p class="mb-0 col-sm-3 align-self-center">Téléphone </p>
          <div class="col-sm-9 align-content-center">
            <input type="tel" class="form-control form-control-sm" required disabled :value="referent.telephone">
          </div>
        </div>
      </div>

      <!-- Payment type -->
      <hr>
      <form @submit.prevent="save">
        <fieldset class="mb-4 row">
          <legend class="col-sm-3 col-form-label">Type de paiement</legend>
          <div class="col-sm-9">
            <div class="form-check">
              <input class="form-check-input" type="radio" name="groupPayment" id="paySep" :disabled="loading" :value="false" required v-model="groupPayment">
              <label class="form-check-label" for="paySep">
                Séparé <small>(chaque membre paiera son billet)</small>
              </label>
            </div>

            <div class="form-check">
              <input class="form-check-input" type="radio" name="groupPayment" id="payGr" :disabled="loading" :value="true" required v-model="groupPayment">
              <label class="form-check-label" for="payGr">
                Groupé <small>(le référent paiera pour tout le groupe)</small>
              </label>
            </div>
          </div>
        </fieldset>
        <hr>

        <!-- Companions -->
        <div v-for="(comp, i) in companions" :key="'comp-'+i" class="bg-light rounded px-3 mb-3 py-1">
          <div class="d-flex flex-row justify-content-between mb-2 align-items-center">
            <h4>Accompagnant {{ i+1 }}</h4>
            <button type="button" class="btn btn-danger rounded-circle" title="Supprimer l'accompagnant"
                    @click="() => deleteComp(i)"
                    :disabled="loading"
                    v-if="i !== 0"
                    style="--bs-btn-padding-y: .2rem; --bs-btn-padding-x: .3rem; --bs-btn-font-size: .75rem;">
              <BiTrash />
            </button>
          </div>
          <div class="mb-2 row">
            <label :for="'lastname-comp-'+i" class="col-sm-3 col-form-label">Nom </label>
            <div class="col-sm-9 align-content-center">
              <input type="text" class="form-control form-control-sm" :name="'lastname-comp-'+i" :id="'lastname-comp-'+i" required :disabled="loading" v-model="comp.lastname">
            </div>
          </div>

          <div class="mb-2 row">
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

          <div class="mb-2 row">
            <p class="col-sm-3">Pass </p>
            <div class="col-sm-9 align-content-center">
              <p>{{ getPassString(pass) }}</p>
            </div>
          </div>

          <div class="mb-2 row" v-if="groupPayment !== undefined && !groupPayment">
            <label :for="'tel-comp-'+i" class="col-sm-3 col-form-label">Téléphone </label>
            <div class="col-sm-9 align-content-center">
              <input type="tel" class="form-control form-control-sm" :name="'tel-comp-'+i" :id="'tel-comp-'+i" placeholder="ex: 061234567 / Numéro fr uniquement"
                     required minlength="10" maxlength="10"
                     :disabled="loading" v-model="comp.telephone">
              <div id="telephoneHelp" class="form-text">Utilisé uniquement pour vous prévenir du tirage au sort.</div>
            </div>
          </div>
        </div>

        <button type="button" class="btn btn-sm btn-block btn-primary shadow rounded mb-4"
                @click="addCompanion" style="width: 100%" :disabled="loading">Ajouter un accompagnant</button>

        <div class="row text-center mb-3">
          <button type="submit" class="btn btn-lg btn-success" :disabled="loading">
            <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" v-if="loading"></span>
            Participer au tirage au sort
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts">
import {defineComponent, type PropType} from 'vue'
import BiTrash from 'bootstrap-icons/icons/trash.svg?component'
import type {PersonReq} from "@/dto/Person";
import BiArrowLeftCircle from 'bootstrap-icons/icons/arrow-left-circle.svg?component'
import {PassType} from "@/dto/ConfirmInfos";

export default defineComponent({
  name: "CompanionStep",
  components: {BiTrash, BiArrowLeftCircle},
  emits: ["saveComp", "backToRef"],
  props: {
    referent: { type: Object as PropType<PersonReq>, required: true},
    loading: { type: Boolean, required: true},
    pass: { type: Number as PropType<PassType>, required: true },
  },

  data() {
    return {
      groupPayment: undefined,
      companions: [{ email: '', firstname: '', lastname: '', telephone: '' }] as PersonReq[],
    }
  },

  methods: {
    addCompanion() {
      this.companions.push({ email: '', firstname: '', lastname: '', telephone: '' })
    },

    deleteComp(index: number) {
      if(index !== 0) this.companions.splice(index, 1)
    },

    getPassString(pass: PassType) {
      if (pass === PassType.TWO_DAYS) return "2j"
      if (pass === PassType.THREE_DAYS) return "3j"
      return "UNKNOWN"
    },

    save() {
      this.$emit("saveComp", {comp: this.companions, groupPayment: this.groupPayment})
    },

    backToRef() {
      this.$emit("backToRef");
    }
  }
})
</script>

<style scoped>

</style>