<template>
  <div v-if="error" class="row justify-content-center" ref="error">
    <div class="col-md-8 alert alert-danger px-5 py-3 mb-5">
      <p class="lead text-center fw-bold mb-0">{{error}}</p>
    </div>
  </div>

  <ReferentStep v-if="!showMembers"
      :loading="loading"
      :close-date="config.closeDate"
      @save-ref="r => saveReferent(r)"
  />

  <CompanionStep v-if="groupReg"
      :referent="referent"
      :loading="loading"
      @save-comp="comp => saveGroup(comp.groupPayment, comp.comp)"
  />
  <div class="row text-center">
    <p class="small"><router-link to="/data-usage" target="_blank">Utilisation des données personnelles</router-link></p>
  </div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import type {PersonReq, ReferentReq} from "@/dto/Person";
import ReferentStep from "@/components/ReferentStep.vue";
import type {Config} from "@/dto/config";
import axios from "axios";
import CompanionStep from "@/components/CompanionStep.vue";

export default defineComponent({
  name: "RegisterView",
  components: {CompanionStep, ReferentStep},
  created() {
    this.load()
  },

  data() {
    return {
      referent: {} as ReferentReq,
      error: "",
      loading: false,
      config: {} as Config,
      groupReg: false,
      showMembers: false
    }
  },

  methods: {
    load() {
      axios.get('/config').then(res => {
        this.config = res.data
      }).catch(this.displayError)
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

    saveReferent(ref: {ref: ReferentReq, groupReg: boolean}) {
      this.referent = ref.ref
      this.groupReg = ref.groupReg

      if (!this.groupReg)
        this.saveGroup(false, [])
      else
        this.showMembers = true
    },

    saveGroup(groupPayment: boolean, companions: PersonReq[]) {
      const body = {
        referent: this.referent,
        groupPayment: groupPayment,
        companions: companions,
      };

      this.loading = true
      axios.post('/register', body).then(res => {
        this.$router.push('/' + res.data.id)
      }).catch(this.displayError).finally(() => this.loading = false)
    }
  }
})
</script>
