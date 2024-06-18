<template>
  <header>
    <nav class="navbar navbar-expand-lg fixed-top navbar-dark bg-dark">
      <div class="container">
        <a class="navbar-brand">
          <Logo width="30" height="30" class="d-inline-block align-top" />
          Bilhed Admin
        </a>

        <button class="navbar-toggler" :class="{collapsed: !navbar}" type="button" aria-expanded="false" aria-label="Toggle navigation"
            @click="navbar = !navbar">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" :class="{show: navbar}">
          <ul class="navbar-nav" @click="navbar = false">
            <li class="nav-item">
              <router-link to="/registered" class="nav-link">Registered</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/participants" class="nav-link">Participants</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/attendees" class="nav-link">Attendees</router-link>
            </li>
          </ul>
        </div>

      </div>
    </nav>
  </header>

  <div class="container main-container">
    <RouterView />
  </div>
</template>

<script lang="ts">
/// <reference types="vite-svg-loader" />
import { defineComponent } from 'vue'
import Logo from '@/assets/logo-breizhcamp-icone.svg?component'

export default defineComponent({
  name: "AppView",
  components: { Logo },
  data() {
    return {
      navbar: false,
    }
  },
})
</script>

<style scoped>
</style>
