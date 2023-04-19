<template>
	<div>
	  <div class="row justify-content-center">
      <div class="col-md-8 bg-light rounded-3 px-5 py-3 mb-5">
        <p class="lead text-center fw-bold mb-0">
          Inscription à la loterie du BreizhCamp 2023
        </p>
        <p class="text-center small">Du 28 au 30 juin 2023</p>

        <p class="text-center">
          Victime de notre succès, et étant limité par la place disponible, l'équipe du BreizhCamp à décidé de mettre en place
            un tirage au sort pour l'achat des places.
        </p>

        <p class="text-center">
          Merci de remplir ce formulaire pour participer au tirage au sort.
        </p>

        <p class="text-center">
          Une seule participation par personne physique, si une tentative de contournement est détectée, les inscriptions seront sorties du tirage au sort.
        </p>

        <p class="lead text-center fw-bold">
            Fermeture des inscriptions le 21 mai à 23h59
        </p>
      </div>
    </div>


    <form>
	    <div class="row justify-content-center">
          <div class="col-md-6">
            <div class="mb-3 row">
              <label for="lastname" class="col-sm-3 col-form-label">Nom</label>
              <div class="col-sm-9">
                <input type="text" class="form-control" name="lastname" id="lastname" required>
              </div>
            </div>

            <div class="mb-3 row">
              <label for="firstname" class="col-sm-3 col-form-label">Prénom</label>
              <div class="col-sm-9">
                <input type="text" class="form-control" name="firstname" id="firstname" required>
              </div>
            </div>

          <div class="mb-3 row">
            <label for="firstname" class="col-sm-3 col-form-label">E-Mail</label>
            <div class="col-sm-9">
              <input type="email" class="form-control" name="email" id="email" required>
            </div>
          </div>

          <div class="mb-4 row">
            <label for="firstname" class="col-sm-3 col-form-label">Tel. mobile</label>
            <div class="col-sm-9">
              <input type="tel" class="form-control" name="telephone" id="telephone" placeholder="ex: 061234567 / Numéro fr uniquement" required>
            </div>
    			  <div id="telephoneHelp" class="form-text text-end">Utilisé uniquement pour valider L'inscription et vous prévenir du tirage au sort.</div>
		  </div>

          <div class="row text-center mb-3">
            <button type="submit" class="btn btn-lg btn-primary">Participer au tirage au sort</button>
          </div>

          <div class="row text-center">
            <p class="small"><router-link to="/data-usage" target="_blank">Utilisation des données personnelles</router-link></p>
          </div>
        </div>
      </div>
    </form>
	</div>
</template>

<script setup lang="ts">
</script>