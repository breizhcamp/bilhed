<template>
	<div>
    <div class="row justify-content-center">
      <div class="col-md-8 bg-light rounded-3 px-5 py-3 mb-5 mt-3">
        <p class="lead text-center fw-bold">
          {{ infos.groupPayment && infos.nbMembers > 1 ?
            'Vos billets ont bien été enregistrés.' :
            'Votre billet a bien été enregistré.'
          }}

        </p>

        <p>Vous allez recevoir un e-mail de BilletWeb avec
          {{ infos.groupPayment && infos.nbMembers > 1 ?
           'vos billets. Vous pouvez les imprimer ou les présenter sur votre smartphone le jour de l\'événement pour récupérer vos badges.' :
           'votre billet. Vous pouvez l\'imprimer ou le présenter sur votre smartphone le jour de l\'évènement pour récupérer votre badge.'
            }}
        </p>

        <p>
          Si vous souhaitez une <strong>facture</strong>, vous pouvez l'éditer à l'aide du lien présent en bas de l'e-mail de BilletWeb. Vous pouvez aussi annuler la commande via le lien "Gérer ma commande" si vous ne pouvez finalement pas venir.
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
import axios from "axios";
import type {EndInfo} from "@/dto/PersonDataTicket";

export default defineComponent({
  name: "TicketEndView",

  created() {
    this.$watch(() => this.$route.params, () => this.load(), { immediate: true })
  },

  data() {
    return {
      infos: {} as EndInfo
    }
  },

  methods: {
    load() {
      const id = localStorage.getItem("personId")
      if (id === null) return

      axios.get('/persons/' + id + '/end')
          .then( res => this.infos = res.data )
    },
  }
})
</script>