<template>
	<div>
    <div class="row justify-content-center">
      <div class="col-md-8 bg-light rounded-3 px-5 py-3 mb-5 mt-3">
        <p class="lead text-center fw-bold">
          Votre participation a bien été enregistrée.
        </p>

        <p class="text-center">
          Vous allez recevoir un e-mail de confirmation pour votre participation.
        </p>

        <p class="text-center">
          Si vous êtes tiré au sort, vous receverez un SMS et un e-mail pour confirmer définitivement votre participation et acheter votre billet.
        </p>

        <p class="text-center">
          Merci :)
        </p>

        <p class="text-center">
          L'équipe du BreizhCamp
        </p>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: "ConfirmedView",
})
</script>