<template>
  <header>
    <nav class="navbar navbar-expand-lg fixed-top navbar-dark bg-dark">
      <div class="container">
        <a class="navbar-brand">
          <img src="@/assets/logo-breizhcamp-icone.svg" width="30" height="30" class="d-inline-block align-top" alt="">
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

export default defineComponent({
  name: "AppView",
})
</script>

<style scoped>
</style>
