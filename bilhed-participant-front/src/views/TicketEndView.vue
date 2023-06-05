<template>
	<div>
    <div class="row justify-content-center">
      <div class="col-md-8 bg-light rounded-3 px-5 py-3 mb-5 mt-3">
        <p class="lead text-center fw-bold">
          Votre billet a bien été enregistré.
        </p>

        <p>
          Vous allez recevoir un e-mail de BilletWeb avec votre billet. Vous pouvez l'imprimer ou le présenter sur votre smartphone le jour de l'évènement pour récupérer votre badge.
        </p>

        <p>
          Si vous souhaitez une <strong>facture</strong>, vous pouvez l'éditer à l'aide du lien présent en bas de l'e-mail de BilletWeb. Vous pouvez aussi annuler le billet via le lien "Gérer ma commande" si vous ne pouvez finalement pas venir.
        </p>

        <p>
          Merci :)
        </p>

        <p>
          L'équipe du BreizhCamp
        </p>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: "TicketEndView",
})
</script>