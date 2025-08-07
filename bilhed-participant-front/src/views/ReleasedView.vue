<template>
	<div>
    <div class="row justify-content-center">
      <div class="col-md-8 bg-light rounded-3 px-5 py-3 mb-5 mt-3">
        <p class="lead text-center fw-bold">
          Nous avons bien enregistré votre souhait de libérer {{ nbPlaces > 1 ? `vos ${nbPlaces} places` : 'votre place' }}.
        </p>

        <p class="text-center" v-if="nbPlaces > 1">
          Elles feront des heureux·ses et seront attribuées à d'autres personnes.
        </p>
        <p class="text-center" v-else>
          Elle fera un heureux ou une heureuse et sera attribué à une autre personne.
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
  name: "ReleasedView",

  data() {
    return {
      nbPlaces: -1
    }
  },

  mounted() {
    this.nbPlaces = history.state.nbPlaces ?? 1
  }
})
</script>