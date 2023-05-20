<template>
  <h1>Participants</h1>
  <button type="button" class="btn btn-primary" @click="load()">Charger</button>

</template>

<script lang="ts">
import { defineComponent } from 'vue'
import axios from 'axios'

export default defineComponent({
  name: "ParticipantView",

  data() {
    return {
      participants: []
    }
  },

  mounted() {
    this.load()
  },

  methods: {
    load() {
      axios.get('/participants').then((response) => {
        this.participants = response.data
      })
    }
  }
})
</script>
