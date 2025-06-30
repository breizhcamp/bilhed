<template>
  <form class="row row-cols-lg-auto g-3 align-items-center mt-2 mb-3" @submit.prevent="search()">
    <div class="col-12">
      <label class="form-label" for="lastname">Lastname</label>
      <input type="text" class="form-control" id="lastname" :value="f.lastname" @input="event => f.lastname = getValue(event)">
    </div>

    <div class="col-12">
      <label class="form-label" for="firstname">Firstname</label>
      <input type="text" class="form-control" id="firstname" :value="f.firstname" @input="event => f.firstname = getValue(event)">
    </div>

    <div class="col-12">
      <label class="form-label" for="email">Email</label>
      <input type="text" class="form-control" id="email" :value="f.email" @input="event => f.email = getValue(event)">
    </div>

    <div class="col-12">
      <label class="form-label" for="pass">Pass</label>
      <select class="form-select" id="pass" v-model="f.pass">
        <option :value="undefined"></option>
        <option value="TWO_DAYS">2j</option>
        <option value="THREE_DAYS">3j</option>
      </select>
    </div>

    <div class="col-12">
      <label class="form-label" for="pass">Payed</label>
      <select class="form-select" id="pass" v-model="f.payed">
        <option :value="undefined"></option>
        <option value="true">Yes</option>
        <option value="false">No</option>
      </select>
    </div>

    <div class="col-12 no-label">
      <button type="submit" class="btn btn-primary">Search</button>
    </div>

  </form>
</template>

<script lang="ts">
import type {PropType} from 'vue'
import {defineComponent} from 'vue'
import type {PersonFilter} from "@/dto/PersonFilter";


export default defineComponent({
  name: "AttendeesFilter",

  props: {
    filter: { type: Object as PropType<PersonFilter>, required: true },
  },
  emits: ['filter'],

  data() {
    return {
      f: { ...this.filter}
    }
  },

  methods: {
    search() {
      this.$emit('filter', this.f)
    },

    getValue(event: Event) {
      return (event.target as HTMLInputElement)?.value || undefined
    }
  }
})
</script>

<style scoped>
.no-label {
  margin-top: 45px;
}
</style>