<template>
  <div>
    <div class="row justify-content-center">
      <div class="col-md-8 bg-light rounded-3 px-5 py-3 mb-5">
        <div v-if="registered.telephone">
          <p class="lead text-center fw-bold mb-0">
            Un SMS vous a été envoyé au {{registered.telephone}}
          </p>

          <p class="text-center">
            Afin de valider votre inscription, merci de saisir le code reçu par SMS.
          </p>

          <p class="text-center">
            Le SMS peut mettre quelques secondes à arriver.
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
    </div>

    <div v-if="error" class="row justify-content-center">
      <div class="col-md-8 alert alert-danger px-5 py-3 mb-5">
        <p class="lead text-center fw-bold mb-0">{{error}}</p>
      </div>
    </div>


    <form @submit.prevent="save()">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="mb-5 row">
            <label for="code" class="col-sm-3 col-form-label">Code</label>
            <div class="col-sm-9">
              <input type="text" class="form-control" name="code" id="code" required minlength="6" maxlength="6" :disabled="loading" v-model="code">
            </div>
          </div>

          <div class="row text-center mb-2">
            <button type="submit" class="btn btn-lg btn-primary" :disabled="loading">
              <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" v-if="loading"></span>
              Valider mon inscription
            </button>
          </div>

          <div class="row text-center">
            <button class="btn btn-link btn-sm">Une erreur dans le numéro de téléphone ? Cliquez ici pour le modifier</button>
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script lang="ts">
import { Registered } from '@/dto/Registered';
import axios from 'axios'
import { defineComponent } from 'vue'

export default defineComponent({
  name: "SmsView",

  data() {
    return {
      loading: false,
      registered: new Registered(),
      code: "",
      error: ""
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

      axios.get('/register/' + this.id).then(res => {
        if (res.data.error) this.error = res.data.error
        this.registered = res.data
      })
        .catch(() => this.error = "Une erreur est survenue, merci de réessayer dans quelques instants")
    },

    save() {
      this.loading = true
      this.error = ""

      axios.post('/register/' + this.id, { code: this.code }).then(res => {
        if (res.data.error) this.error = res.data.error
        this.$router.push('/confirmed')
      }).catch(() => {
        this.error = "Une erreur est survenue, merci de réessayer dans quelques instants"
      }).finally(() => this.loading = false)

    }
  }
})
</script>