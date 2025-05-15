<template>
  <div class="modal show d-block" tabindex="-1" v-if="open" @click="close()">
    <div class="modal-dialog" @click.stop>
      <div class="modal-content">
        <form @submit.prevent="save()">
          <div class="modal-header">
            <h5 class="modal-title">{{title}}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close" @click="close()"></button>
          </div>

          <div class="modal-body">
            <slot></slot>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="close()">Fermer</button>
            <button type="submit" class="btn btn-primary" :disabled="loading">
              <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true" v-if="loading"></span>
              Enregistrer
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
  <div class="modal-backdrop show" v-if="open"></div>
</template>

<script lang="ts">
import {defineComponent} from 'vue'

export default defineComponent({
  name: 'ModalForm',

  props: {
    open: {type: Boolean, required: true, default: false},
    loading: {type: Boolean, default: false},
    title: {type: String, required: true},
  },

  emits: ['update:open', 'save'],

  methods: {
    close() {
      this.$emit('update:open', false)
    },

    save() {
      this.$emit('save')
    }
  }
})
</script>

<style scoped>

</style>