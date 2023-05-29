<template>
  <slot v-if="date">{{day.format(format)}}<sup>{{day.format('Z')}}</sup></slot>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import dayjs from 'dayjs'

export default defineComponent({
  name: "DateView",

  props: {
    date: { type: String, required: false },
    format: { type: String, required: false, default: 'DD/MM/YYYY HH:mm:ss' },
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