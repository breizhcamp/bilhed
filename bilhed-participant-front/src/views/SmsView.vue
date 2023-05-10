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
              <input type="text" inputmode="numeric" class="form-control" name="code" id="code" required minlength="6" maxlength="6" :disabled="loading" v-model="code">
            </div>
          </div>

          <div class="row text-center mb-4 justify-content-center" v-if="displayResendSms">
            <button type="button" class="col-sm-10 btn btn-info" :disabled="resendLoading" @click="resendSMS()">
              <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" v-if="resendLoading"></span>
              SMS non reçu ? Renvoyer un SMS (3 max)
            </button>
          </div>

          <div class="row text-center mb-2">
            <button type="submit" class="btn btn-lg btn-primary" :disabled="loading">
              <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" v-if="loading"></span>
              Valider mon inscription
            </button>
          </div>

          <div class="row text-center">
            <button type="button" class="btn btn-link btn-sm" @click="openChangePhoneModal()">Une erreur dans le numéro de téléphone ? Cliquez ici pour le modifier</button>
          </div>
        </div>
      </div>
    </form>

    <ModalForm v-model:open="changePhoneModal" :loading="loading" title="Modifier mon numéro de téléphone" @save="changePhone()">
      <div class="mb-3">
        <label for="telephone" class="form-label">Numéro de téléphone</label>
        <input type="tel" class="form-control" name="telephone" id="telephone" required minlength="10" maxlength="10" v-model="registered.telephone">
      </div>
    </ModalForm>

  </div>
</template>

<script lang="ts">
import ModalForm from '@/components/ModalForm.vue';
import { Registered } from '@/dto/Registered';
import axios from 'axios'
import { defineComponent } from 'vue'

export default defineComponent({
  name: "SmsView",
  components: {ModalForm},

  data() {
    return {
      loading: false,
      resendLoading: false,
      displayResendSms: false,
      registered: new Registered(),
      code: "",
      error: "",
      changePhoneModal: false,
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
        this.registered = res.data
        setTimeout(() => this.displayResendSms = true, 30000)
      }).catch(this.displayError)
    },

    save() {
      this.loading = true
      this.error = ""

      axios.post('/register/' + this.id, { code: this.code }).then(() => {
        this.$router.push('/confirmed')
      }).catch(this.displayError).finally(() => this.loading = false)

    },

    openChangePhoneModal() {
      this.changePhoneModal = true
    },

    changePhone() {
      this.loading = true
      this.error = ""

      axios.put('/register/' + this.id + '/phone', {phone: this.registered.telephone }).catch(this.displayError)
        .finally(() => {
          this.changePhoneModal = false
          this.loading = false
        })
    },

    resendSMS() {
      this.resendLoading = true
      this.error = ""

      axios.post('/register/' + this.id + '/resend-sms')
        .then(() => {
          this.displayResendSms = false
          setTimeout(() => this.displayResendSms = true, 30000)
        })
        .catch(this.displayError)
        .finally(() => this.resendLoading = false)
    },

    displayError(err: any) {
      console.log(err)
      if (err.response.data && err.response.data.error) {
        this.error = err.response.data.error
      } else {
        this.error = "Une erreur est survenue, merci de réessayer dans quelques instants"
      }
    }
  }
})
</script>