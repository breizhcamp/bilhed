<template>
  <slot v-if="pass == 'TWO_DAYS'">2j</slot>
  <slot v-if="pass == 'THREE_DAYS'">3j</slot>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import dayjs from 'dayjs'

export default defineComponent({
  name: "Pass",

  props: {
    pass: { type: String, required: false },
  },
})
</script>

<style scoped>

</style>