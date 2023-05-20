<template>
  <header>
    <nav class="navbar navbar-expand-lg fixed-top navbar-dark bg-dark">
      <div class="container">
        <a class="navbar-brand">
          <img src="@/assets/logo-breizhcamp-icone.svg" width="30" height="30" class="d-inline-block align-top" alt="">
          Bilhed Admin
        </a>

      </div>
    </nav>
  </header>

  <div class="container main-container">
    <RouterView />
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<style scoped>
</style>
