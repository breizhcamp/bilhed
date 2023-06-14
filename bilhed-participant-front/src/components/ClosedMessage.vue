<template>
	<div>
    <div class="row justify-content-center" v-if="!loading">
      <div class="col-md-8 bg-light rounded-3 px-5 py-3 mb-5 mt-3">
        <p class="lead text-center fw-bold">
          L'inscription pour le tirage au sort de la billetterie est fermée.
        </p>

        <p class="text-center">
          Rendez-vous l'année prochaine !
        </p>

        <p class="text-center">
          Merci :)
        </p>

        <p class="text-center">
          L'équipe du BreizhCamp
        </p>
      </div>
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
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: "ClosedMessage",
  props: {
    "loading": {
      type: Boolean,
      required: true
    }
  },
})
</script>