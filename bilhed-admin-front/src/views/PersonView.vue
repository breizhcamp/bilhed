<template>
  <div v-if="error" class="row justify-content-center" ref="error">
    <div class="col-md-8 alert alert-danger px-5 py-3">
      <p class="lead text-center fw-bold mb-0">{{error}}</p>
    </div>
  </div>
  <section style="background-color: #bde4f3" class="rounded p-3 mb-3">
    <h2>Informations sur {{ person.firstname }} {{ person.lastname }}</h2>
    <!--    infos globales -->
    <table class="table table-borderless m-0 table-transparent">
      <thead>
      <tr>
        <th scope="col">Email</th>
        <th scope="col">Telephone</th>
        <th scope="col">Pass</th>
        <th scope="col">Enfants</th>
        <th scope="col"></th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td><input class="form-control form-control-sm" type="text" id="mail" v-model="person.email" /></td>
        <td><input class="form-control form-control-sm" type="text" id="phone" v-model="person.telephone" /></td>
        <td><Pass :pass="person.pass"/></td>
        <td>
          <button type="button" class="btn btn-primary btn-sm" title="Modify Person" @click="updatePerson()">Modifier</button>
        </td>
      </tr>
      </tbody>
    </table>
  </section>
  <section :style="`background-color: ${bgColor}`" class="rounded p-3 mb-3" >
    <h2>Statut {{ person._status }}</h2>
    <table v-if="person._status === PersonStatus.REGISTERED && registered" class="table table-borderless m-0 table-transparent">
      <thead>
        <tr>
          <th scope="col">Date d'inscription</th>
          <th scope="col">Status SMS</th>
          <th scope="col">Nb Sms envoyés</th>
          <th scope="col">Date dernier Sms envoyé</th>
          <th scope="col">Sms erreur</th>
          <th scope="col">Token</th>
          <th scope="col">Nb essais token</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td><DateView :date="registered.registrationDate" /> </td>
          <td>{{ registered.smsStatus }}</td>
          <td>{{ registered.nbSmsSent }}</td>
          <td><DateView :date="registered.lastSmsSentDate" /></td>
          <td>{{ registered.smsError }}</td>
          <td>{{ registered.token }}</td>
          <td>{{ registered.nbTokenTries }}</td>
        </tr>
      </tbody>
    </table>
    <table v-if="person._status === PersonStatus.PARTICIPANT" class="table table-borderless m-0 table-transparent">
      <thead>
      <tr>
        <th scope="col">Date de participation</th>
        <th scope="col">Ordre tirage</th>
        <th scope="col">Date envoi notification tirage</th>
      </tr>
      </thead>
      <tbody>
      <tr v-if="participant">
        <td><DateView :date="participant.participationDate" /></td>
        <td>{{ participant.drawOrder }}</td>
        <td><DateView :date="participant.notificationConfirmSentDate" /></td>
      </tr>
      </tbody>
    </table>
    <table v-if="person._status === PersonStatus.ATTENDEE || person._status === PersonStatus.ATTENDEE_FULL" class="table table-borderless m-0 table-transparent">
      <thead>
      <tr>
        <th scope="col">Entreprise</th>
        <th scope="col">Taille t-shirt</th>
        <th scope="col">Coupe t-shirt</th>
        <th scope="col">Vegan ?</th>
        <th scope="col">Meet and Greet ?</th>
        <th scope="col">Code postal</th>
        <th scope="col">Date de confirmation participant</th>
        <th scope="col">Payé ?</th>
      </tr>
      </thead>
      <tbody>
      <tr v-if="attendee">
        <template v-if="attendeeFull">
          <td>{{ attendeeFull.company }}</td>
          <td>{{ attendeeFull.tshirtSize }}</td>
          <td>{{ attendeeFull.tshirtCut }}</td>
          <td>{{ getBoolStr(attendeeFull.vegan) }}</td>
          <td>{{ getBoolStr(attendeeFull.meetAndGreet) }}</td>
          <td>{{ attendeeFull.postalCode }}</td>
        </template>
        <template v-else>
          <td/><td/><td/><td/><td/><td/>
        </template>
        <td><DateView :date="attendee.participantConfirmationDate" /></td>
        <td>{{ getBoolStr(attendee.payed) }}</td>
      </tr>
      </tbody>
    </table>
  </section>
  <section>
<!--    reminders-->
    <table class="table table-striped table-borderless mb-4">
      <colgroup>
        <col style="width: 20%"/>
        <col style="width: 10%"/>
        <col style="width: 5%"/>
        <col style="width: 5%"/>
        <col style="width: 60%"/>
      </colgroup>
      <thead>
        <tr>
          <th scope="col" class="">Date d'envoi</th>
          <th scope="col">Template</th>
          <th scope="col">Méthode</th>
          <th scope="col">Origine</th>
          <th scope="col">Modèle</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="reminder in reminders" :key="reminder.id">
          <td><DateView :date="reminder.reminderDate" /></td>
          <td>{{ reminder.template }}</td>
          <td>{{ reminder.method }}</td>
          <td>{{ reminder.origin }}</td>
          <td>{{ reminder.model }}</td>
        </tr>
      </tbody>
    </table>
  </section>
  <nav v-if="person" class="navbar sticky-bottom bg-light">
    <div v-if="person._status === PersonStatus.REGISTERED">
      <button type="button" class="btn btn-outline-primary me-1" @click="sendRegisteredReminder(person.id, 'sms')" :disabled="loading"><BiChatText/> Remind SMS success</button>
      <button type="button" class="btn btn-outline-primary me-4" @click="sendRegisteredReminder(person.id, 'email')" :disabled="loading"><BiEnvelope/> Remind  Email success</button>
      <button type="button" class="btn btn-primary" v-on:click="levelUp()" :disabled="loading">Level up to participant</button>
    </div>
    <div v-if="person._status === PersonStatus.PARTICIPANT" class="container-fluid">
      <div>
        <button type="button" class="btn btn-primary me-1" @click="notify('success')" :disabled="loading"><BiSendCheck/> Notify success</button>
        <button type="button" class="btn btn-warning me-1" @click="notify('waiting')" :disabled="loading"><BiSendExclamation/> Notify waiting</button>
        <button type="button" class="btn btn-outline-danger me-4" @click="notify('failed')" :disabled="loading"><BiSendX/> Notify failed</button>

        <button type="button" class="btn btn-outline-primary me-4" @click="notify('success/reminder')" :disabled="loading"><BiSendCheck/> Remind success</button>

        <button type="button" class="btn btn-outline-primary me-1" @click="levelUp('attendee')" :disabled="loading"><BiArrowUp/> Level Up to attendee</button>
        <button type="button" class="btn btn-outline-warning me-1" @click="levelUp('release')" :disabled="loading"><BiArrowUp/> Level Up to release</button>

      </div>
    </div>
    <div v-if="person._status === PersonStatus.ATTENDEE || person._status === PersonStatus.ATTENDEE_FULL" class="container-fluid">
      <div>
        <button v-if="!attendeeFull?.payed || !attendee?.payed"
                type="button"
                class="btn btn-primary me-1"
                v-on:click="notify('payed/reminder/mail')"
                :disabled="loading"><BiSendCheck/> Remind payed mail</button>
        <button v-if="!attendeeFull?.payed || !attendee?.payed"
                type="button"
                class="btn btn-outline-warning me-1"
                v-on:click="notify('payed/reminder/sms')" :disabled="loading"><BiSendCheck/> Remind payed SMS</button>
        <button type="button" class="btn btn-outline-danger me-1" v-on:click="levelUp('release')" :disabled="loading"><BiArrowUp/> Level Up to release</button>
      </div>
    </div>
  </nav>
</template>

<script lang="ts">
import {defineComponent} from 'vue'
import axios from "axios";
import BiSendCheck from 'bootstrap-icons/icons/send-check.svg?component'
import BiSendExclamation from 'bootstrap-icons/icons/send-exclamation.svg?component'
import BiSendX from 'bootstrap-icons/icons/send-x.svg?component'
import BiArrowUp from 'bootstrap-icons/icons/arrow-bar-up.svg?component'
import BiChatText from 'bootstrap-icons/icons/chat-text.svg?component'
import BiEnvelope from 'bootstrap-icons/icons/envelope.svg?component'
import {type Person, PersonStatus} from "@/dto/Person";
import Pass from "@/components/Pass.vue";
import DateView from "@/components/DateView.vue";
import type {Registered} from "@/dto/Registered";
import type {Participant} from "@/dto/Participant";
import type {Attendee, AttendeeData} from "@/dto/Attendee";
import {toastSuccess, toastWarning} from "@/utils/ReminderUtils";
import type {Reminder} from "@/dto/Reminder";

export default defineComponent({
  name: "PersonView",
  computed: {
    PersonStatus() {
      return PersonStatus
    },
    registered(): Registered {
      return this.person as Registered
    },
    participant(): Participant {
      return this.person as Participant
    },
    attendee(): Attendee {
      return this.person as Attendee
    },
    attendeeFull(): AttendeeData | undefined {
      return this.person._status === PersonStatus.ATTENDEE_FULL ? this.person as AttendeeData : undefined
    }
  },
  components: {DateView, Pass, BiChatText, BiSendCheck, BiSendX, BiArrowUp, BiSendExclamation, BiEnvelope},

  data() {
    return {
      person: {} as Person,
      personFromDB: {} as Person,
      bgColor: '',
      error: "",
      reminders: [] as Reminder[],
      loading: false
    }
  },

  created() {
    this.$watch(() => this.$route.params, () => this.load(), {immediate: true})
  },

  methods: {
    load() {
      axios.get(`/person/${this.$route.params.id}`).then(personRes => {
        this.person = personRes.data
        switch (this.person._status) {
          case PersonStatus.REGISTERED:
            this.bgColor = "#EACBEA"
            break;
          case PersonStatus.PARTICIPANT:
            this.bgColor = "#F8D2B2"
            break;
          case PersonStatus.ATTENDEE:
          case PersonStatus.ATTENDEE_FULL:
            this.bgColor = "#F6F5CBFF"
            break;
        }
        this.personFromDB = {...this.person}
      })
      axios.get(`/person/${this.$route.params.id}/reminders`).then(rem => {
        this.reminders = rem.data
      })
    },

    getBoolStr(state: boolean | undefined): string {
      return state ? 'oui' : 'non'
    },

    updatePerson() {
      this.error = ""
      if (this.person.email === this.personFromDB.email && this.person.telephone === this.personFromDB.telephone) {
        toastWarning("Aucune modification")
        return
      }
      if (!confirm(`Voulez vous modifier les contacts de ${this.person.firstname} ${this.person.lastname} ?`))
        return

      axios.put(`/person/${this.person.id}`, {
        email: this.person.email,
        telephone: this.person.telephone
      }).then(() => toastSuccess("Le changement a bien été effectué")).catch(this.displayError)
    },

    displayError(err: any) {
      if (err.response.data && err.response.data.error) {
        this.error = err.response.data.error
      } else {
        this.error = "Une erreur est survenue, merci de réessayer dans quelques instants"
      }

      let element = this.$refs['error'] as HTMLElement
      let top = element.offsetTop
      window.scrollTo(0, top)
    },

    notify(type: string) {
      if (!confirm(`Voulez vous envoyer une notification ${type} à ${this.person.firstname} ${this.person.lastname} ?`))
        return

      this.loading = true
      const status = this.person._status === PersonStatus.PARTICIPANT ? 'participants' : 'attendees'
      axios.post(`${status}/notif/${type}`, [this.person.id]).then(() => {
        this.load()
        toastSuccess("Notification envoyée.")
      }).finally(() => {
        this.loading = false
      })
    },

    levelUp(level?: string) {
      if (!confirm(`Voulez vous changer le statut de ${this.person.firstname} ${this.person.lastname} en ${level} ?`))
        return

      this.loading = true
      let url = ""
      switch (this.person._status) {
        case PersonStatus.REGISTERED:
          url = "/registered/levelUp"
          break;
        case PersonStatus.PARTICIPANT:
          url = `/participants/levelUp/${level}`
          break;
        case PersonStatus.ATTENDEE_FULL:
        case PersonStatus.ATTENDEE:
          url = `/attendees/levelUp/${level}`
          break;
      }

      axios.post(url, [this.person.id]).then(() => {
        this.load()
        toastSuccess("Changement de statut effectué.")
      }).finally(() => {
        this.loading = false
      })
    },

    sendRegisteredReminder(id: string, type: string) {
      if (!confirm(`Voulez vous envoyer un rappel d'inscription à ${this.person.firstname} ${this.person.lastname}`))
        return


      this.loading = true
      let data = {}
      // @ts-ignore
      data[type] = true

      axios.post(`/registered/${id}/reminder`, data).then(() => {
        this.load()
        toastSuccess("Rappel envoyé.")
      }).finally(() => {
        this.loading = false
      })
    },
  }
})
</script>

<style scoped>
.table-transparent, .table-transparent td, .table-transparent th {
  background-color: transparent!important;
}
</style>