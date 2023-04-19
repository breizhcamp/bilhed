<template>
	<header>
		<nav class="navbar navbar-expand-lg fixed-top navbar-dark bg-dark">
			<div class="container">
				<a class="navbar-brand">
					<img src="@/assets/logo-breizhcamp-icone.svg" width="30" height="30" class="d-inline-block align-top" alt="">
					Billetterie BreizhCamp
				</a>
				<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
					<span class="navbar-toggler-icon"></span>
				</button>

			</div>
		</nav>
	</header>

	<div class="container main-container">
	  <RouterView />
	</div>

</template>

<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<style scoped>

</style>
