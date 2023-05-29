<template>
  <slot v-if="date">{{day.format('DD/MM/YYYY HH:mm:ss')}}<sup>{{day.format('Z')}}</sup></slot>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import dayjs from 'dayjs'

export default defineComponent({
  name: "DateView",

  props: {
    date: { type: String, required: false },
  },

  computed: {
    day() {
      return dayjs(this.date)
    }
  },
})
</script>

<style scoped>

</style>