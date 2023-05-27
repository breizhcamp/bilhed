<template>
  <header>
    <nav class="navbar navbar-expand-lg fixed-top navbar-dark bg-dark">
      <div class="container">
        <a class="navbar-brand">
          <Logo width="30" height="30" class="d-inline-block align-top" />
          Bilhed Admin
        </a>

        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav">
            <li class="nav-item">
              <router-link to="/registered" class="nav-link">Registered</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/participants" class="nav-link">Participants</router-link>
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
import { defineComponent } from 'vue'
import Logo from '@/assets/logo-breizhcamp-icone.svg'

export default defineComponent({
  name: "AppView",
  components: { Logo },
})
</script>

<style scoped>
</style>
