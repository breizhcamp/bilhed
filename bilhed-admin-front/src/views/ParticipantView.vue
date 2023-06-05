<template>
  <h1>
    Participants

    <span class="d-inline-block float-end">
      <button type="button" class="btn btn-primary" v-on:click="draw()" :disabled="loading">Draw</button>
<!--      <button type="button" class="btn btn-primary ms-2" v-on:click="notify()" :disabled="loading">Notif. success</button>-->
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
        <th scope="col">Or.</th>
        <th scope="col">Limit date</th>
        <th scope="col"></th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="p in participants" :key="p.id">
        <td><input type="checkbox" v-model="p.checked"></td>
        <td>{{ p.lastname }}</td>
        <td>{{ p.firstname }}</td>
        <td>{{ p.email }}</td>
        <td>{{ p.telephone }}</td>
        <td>{{ p.pass }}</td>
        <td><DateView :date="p.participationDate"/></td>
        <td>{{ p.drawOrder }}</td>
        <td><DateView :date="p.confirmationLimitDate"/></td>
        <td>
          <button type="button" class="btn btn-link btn-sm" title="Notify success" @click="notifyOne(p.id, 'success')" :disabled="loading"><BiSendCheck/></button>
          <button type="button" class="btn btn-link btn-sm" title="Notify waiting" @click="notifyOne(p.id, 'waiting')" :disabled="loading"><BiSendExclamation/></button>
          <button type="button" class="btn btn-link btn-sm" title="Notify failed" @click="notifyOne(p.id, 'failed')" :disabled="loading"><BiSendX/></button>
        </td>
      </tr>
      </tbody>
    </table>

    <div class="mb-3">
      <button type="button" class="btn btn-primary" v-on:click="notifySel('success')" :disabled="loading">Notify success</button>
      <button type="button" class="btn btn-primary" v-on:click="notifySel('waiting')" :disabled="loading">Notify waiting</button>
      <button type="button" class="btn btn-primary" v-on:click="notifySel('failed')" :disabled="loading">Notify failed</button>
    </div>
  </div>
</template>

<script lang="ts">
/// <reference types="vite-svg-loader" />

import DateView from '@/components/DateView.vue'
import type { Participant } from '@/dto/Participant';
import axios from 'axios'
import BiSendCheck from 'bootstrap-icons/icons/send-check.svg?component'
import BiSendExclamation from 'bootstrap-icons/icons/send-exclamation.svg?component'
import BiSendX from 'bootstrap-icons/icons/send-x.svg?component'
import { defineComponent } from 'vue'

export default defineComponent({
  name: "ParticipantView",
  components: { DateView, BiSendCheck, BiSendExclamation, BiSendX },

  data() {
    return {
      participants: [] as Participant[],
      allChecked: false,
      loading: false,
    }
  },

  created() {
    this.$watch(() => this.$route.params, () => this.load(), { immediate: true })
  },

  watch: {
    allChecked() {
      this.participants.forEach((r) => r.checked = this.allChecked)
    }
  },

  methods: {
    load() {
      axios.get('/participants').then((response) => {
        this.participants = response.data
      })
    },

    draw() {
      this.loading = true
      axios.post('/participants/draw').then(() => {
        this.load()
      }).finally(() => {
        this.loading = false
      })
    },

    notifyOne(id: string, type: string) {
      this.sendNotify([id], type);
    },

    notifySel(type: string) {
      const ids = this.participants.filter((p) => p.checked).map((p) => p.id)
      this.sendNotify(ids, type);
    },

    sendNotify(ids: string[], type: string) {
      this.loading = true
      axios.post('/participants/notif/' + type, ids).then(() => {
        this.load()
      }).finally(() => {
        this.loading = false
      })
    },

    notifyAll() {
      this.loading = true
      axios.post('/participants/notif').then(() => {
        this.load()
      }).finally(() => {
        this.loading = false
      })
    },
  }
})
</script>
