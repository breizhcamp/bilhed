<template>
  <div>
    <div class="row justify-content-center">
      <div class="col-md-12 col-lg-8 bg-light rounded-3 px-5 py-3 mb-5 mt-3" v-if="participant.firstname">
        <p class="lead text-center fw-bold">
          Bonne nouvelle {{participant.firstname}}, vous avez été sélectionné pour la billetterie du BreizhCamp !
        </p>

        <p class="mt-4 mb-4">
          Vous pouvez confirmer votre venue et acheter votre billet,
          ou bien libérer votre place pour qu'elle soit attribuée à une autre personne si vous n'êtes plus disponible.
        </p>

        <p class="fw-bold">
          Vous avez jusqu'au <DateView :date="participant.confirmationLimitDate" /> pour confirmer votre choix.
        </p>
      </div>

      <div class="placeholder-glow" v-else>
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


    <div class="row" v-if="participant.firstname">

      <div class="col-md-6 text-center mb-2">
        <button class="btn btn-light btn-lg " @click="confirm(false)">
          Je ne suis plus disponible, libérer ma place
        </button>
      </div>

      <div class="col-md-6 text-center mb-2">
        <button class="btn btn-primary btn-lg" @click="confirm(true)">
          Confirmer ma venue et acheter mon billet
        </button>
      </div>

    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { Participant } from '@/dto/Participant'
import DateView from '@/components/DateView.vue'
import axios from 'axios'

export default defineComponent({
  name: "SuccessView",
  components: { DateView },

  data() {
    return {
      participant: new Participant(),
      loading: false,
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

      axios.get('/participants/' + this.id).then(res => {
        this.participant = res.data
      }).catch(this.displayError)
        .finally(() => this.loading = false)
    },

    confirm(coming: boolean) {
      this.error = ""
      this.loading = true

      axios.patch('/participants/' + this.id, { coming: coming }).then(res => {

      }).catch(this.displayError)
        .finally(() => this.loading = false)
    },

    displayError(err: any) {
      console.log(err)
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