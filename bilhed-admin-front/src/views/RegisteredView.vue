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
        </tr>
      </thead>
      <tbody>
        <tr v-for="r in registered" :key="r.id">
          <td><input type="checkbox" v-model="r.checked"></td>
          <td>{{ r.lastname }}</td>
          <td>{{ r.firstname }}</td>
          <td>{{ r.email }}</td>
          <td>{{ r.telephone }}</td>
          <td>{{ r.pass }}</td>
          <td><DateView :date="r.registrationDate"/></td>
        </tr>
      </tbody>
    </table>
  </div>

  <div class="mb-3">
    <button type="button" class="btn btn-primary" v-on:click="levelUp()" :disabled="loading">Level up to participant</button>
  </div>

  <ModalForm v-model:open="importModal" :loading="loading" title="Import" @save="importCsv()">
    <div class="mb-3">
      <label for="file" class="form-label">File</label>
      <input type="file" class="form-control" id="file" accept="text/csv" @change="onFileSelected($event)">
    </div>
  </ModalForm>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'
import axios from 'axios'
import dayjs from 'dayjs'
import DateView from '@/components/DateView.vue'
import ModalForm from '@/components/ModalForm.vue'

export default defineComponent({
  name: "RegisteredView",
  components: { ModalForm, DateView },

  data() {
    return {
      registered: [],
      importModal: false,
      loading: false,
      file: null as File | null,
      allChecked: false,
    }
  },

  mounted() {
    this.load()
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
