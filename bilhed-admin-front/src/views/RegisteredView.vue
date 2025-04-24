<template>
  <h1>
    Registered

    <span class="d-inline-block float-end">
      <button type="button" class="btn btn-primary" v-on:click="importModal = true">Import</button>
    </span>
  </h1>

  <div class="mb-3">
    <table class="table table-striped table-hover">
      <thead>
        <tr>
          <th scope="col"><input type="checkbox" v-model="allChecked"></th>
          <th scope="col">Lastname</th>
          <th scope="col">Firstname</th>
          <th scope="col">Email</th>
          <th scope="col">Telephone</th>
          <th scope="col">Pass</th>
          <th scope="col">Reg date</th>
          <th scope="col"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="r in registered" :key="r.id">
          <td><input type="checkbox" v-model="r.checked"></td>
          <td>{{ r.lastname }}</td>
          <td>{{ r.firstname }}</td>
          <td>{{ r.email }}</td>
          <td>{{ r.telephone }}</td>
          <td><Pass :pass="r.pass"/></td>
          <td><DateView :date="r.registrationDate"/></td>
          <td>
            <button type="button" class="btn btn-link btn-sm" title="Send SMS reminder" @click="sendReminder(r.id, 'sms')" :disabled="loading"><BiChatText/></button>
            <button type="button" class="btn btn-link btn-sm ms-1" title="Send email reminder" @click="sendReminder(r.id, 'email')" :disabled="loading"><BiEnvelope/></button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>

  <div class="mb-3">
    <button type="button" class="btn btn-primary" v-on:click="levelUp()" :disabled="loading">Level up to participant</button>
  </div>

  <ModalForm v-model:open="importModal" :loading="loading" title="Import" @save="importCsv()">
    <div class="mb-3">
      Format (with header): lastname, firstname, email, telephone
    </div>

    <div class="mb-3">
      <label for="file" class="form-label">File</label>
      <input type="file" class="form-control" id="file" accept="text/csv" @change="onFileSelected($event)">
    </div>
  </ModalForm>
</template>

<script lang="ts">
/// <reference types="vite-svg-loader" />

import type { Registered } from '@/dto/Registered';
import { defineComponent } from 'vue'
import axios from 'axios'
import DateView from '@/components/DateView.vue'
import ModalForm from '@/components/ModalForm.vue'
import BiChatText from 'bootstrap-icons/icons/chat-text.svg?component'
import BiEnvelope from 'bootstrap-icons/icons/envelope.svg?component'
import Pass from "@/components/Pass.vue";

export default defineComponent({
  name: "RegisteredView",
  components: {Pass, ModalForm, DateView, BiChatText, BiEnvelope },

  data() {
    return {
      registered: [] as Registered[],
      importModal: false,
      loading: false,
      file: null as File | null,
      allChecked: false,
    }
  },

  created() {
    this.$watch(() => this.$route.params, () => this.load(), { immediate: true })
  },

  watch: {
    allChecked() {
      this.registered.forEach((r) => r.checked = this.allChecked)
    }
  },

  methods: {
    load() {
      axios.get('/registered').then((response) => {
        this.registered = response.data
      })
    },

    sendReminder(id: string, type: string) {
      this.loading = true
      let data = {}
      // @ts-ignore
      data[type] = true

      axios.post(`/registered/${id}/reminder`, data).then(() => {
        this.load()
      }).finally(() => {
        this.loading = false
      })
    },

    onFileSelected(event: Event) {
      const target = event.target as HTMLInputElement
      this.file = target.files?.item(0) ?? null
    },

    importCsv() {
      if (!this.file) {
        return
      }

      this.loading = true

      const formData = new FormData()
      formData.append('file', this.file)

      axios.post('/registered/import', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(() => {
        this.importModal = false
        this.load()
      }).catch(() => {
        this.importModal = false
      }).finally(() => {
        this.loading = false
      })
    },

    levelUp() {
      const ids = this.registered.filter((r) => r.checked).map((r) => r.id)
      this.loading = true
      axios.post('/registered/level-up', ids).then(() => {
        this.load()
      }).finally(() => {
        this.loading = false
      })
    }
  }
})
</script>

<style scoped>
button.btn-sm {
  padding: 0;
  color: #212529;
  line-height: 0;
  vertical-align: text-top;
}
</style>