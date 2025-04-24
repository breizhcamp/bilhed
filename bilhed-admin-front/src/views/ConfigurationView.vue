<template>
  <h1>Configuration délais & relances</h1>
  <section class="rounded p-3 mb-3" style="background-color: #bde4f3">
    <form class="g-3 mt-2 mb-3" @submit.prevent="updatePass()">
      <p>BreizhCamp session <DateView :date="getConfigByKey('bcOpenDate').value" format="YYYY" :sup="''"/>
        du <DateView :date="getConfigByKey('bcOpenDate').value" format="DD/MM/YYYY" />
        au <DateView :date="getConfigByKey('bcCloseDate').value" format="DD/MM/YYYY"/>.
        &emsp; Date limite des inscriptions <DateView :date="getConfigByKey('registeredCloseDate').value" format="D MMMM à HH[h]mm" />
      </p>
      <p></p>
      <h2>Billets</h2>
      <div class="d-flex flex-row align-items-center" id="form-ticket">
        <div class="form-group mb-4 me-5">
          <label class="form-label fw-bold" for="nbPassTwoDays">Nombre de billets 2J</label>
          <input type="number" class="form-control form-control-sm" id="nbPassTwoDays" v-model="getConfigByKey('nbPassTwoDays').value" >
        </div>
        <div class="form-group mb-4 me-5">
          <label class="form-label fw-bold" for="nbPassThreeDays">Nombre de billets 3J</label>
          <input type="number" class="form-control form-control-sm" id="nbPassThreeDays" v-model="getConfigByKey('nbPassThreeDays').value" >
        </div>

        <div class="form-group mb-4 me-5">
          <label class="form-label fw-bold" for="pricePassTwoDays">Prix des billets 2J</label>
          <div class="input-group input-group-sm">
            <input type="number" class="form-control" id="pricePassTwoDays" v-model="getConfigByKey('pricePassTwoDays').value" >
            <span class="input-group-text">€</span>
          </div>
        </div>
        <div class="form-group mb-4">
          <label class="form-label fw-bold" for="pricePassThreeDays">Prix des billets 3J</label>
          <div class="input-group input-group-sm">
            <input type="number" class="form-control" id="pricePassThreeDays" v-model="getConfigByKey('pricePassThreeDays').value" >
            <span class="input-group-text">€</span>
          </div>
        </div>
      </div>
      <input class="btn btn-primary btn-sm" type="submit" value="Enregistrer" />
    </form>
  </section>

  <section class="d-flex flex-row justify-content-between align-items-center mb-4">
    <div v-for="bloc in reminderBlocks"
        class="col-config rounded p-2 align-self-stretch"
        :style="{ backgroundColor: bloc.bgColor }" :key="'div' + bloc.type">
      <h3>{{ bloc.title }}</h3>
      <p>{{ bloc.text }}</p>
      <RemindersSection :reminders="getArrayByType(bloc.type, true)"
                 @submit="(up) => remindersManager(up, bloc.type)"
                 :reminderTime="toInt(getConfigByKey(`reminderTime${bloc.short}`).value)"
                 :reminderType="bloc.type"
                 @delete="(id) =>deleteReminder(id, bloc.type)"
                 :reminderBgColor="bloc.reminderBgColor"
                 :key="'section' + bloc.type"
      />
    </div>
  </section>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import axios from "axios";
import type {Config} from "@/dto/Config";
import DateView from "@/components/DateView.vue";
import dayjs from "dayjs";
import {
  type ReminderBloc,
  type ReminderConfig,
  type ReminderConfigRes,
  ReminderType,
  type ReminderUpdate
} from "@/dto/ReminderConfig";
import RemindersSection from "@/components/RemindersSection.vue";
import deepEqual from "deep-equal";
import {getShorterType, isReminderConfigRes, toastError, toastSuccess, toastWarning, toInt} from "@/utils/ReminderUtils"

export default defineComponent({
  name: "ConfigurationView",
  components: {RemindersSection, DateView},

  data() {
    const reminderBlocksSke: ReminderBloc[] = [
      {
        type: ReminderType.REGISTERED,
        title: 'Inscrits',
        text: "Avant la date limite des inscriptions",
        bgColor: '#D0A7D0',
        reminderTimeKey: 'reminderTimeReg',
        reminderBgColor: '#eacbea'
      },
      {
        type: ReminderType.PARTICIPANT,
        title: 'Participants',
        text: 'Après le tirage au sort',
        bgColor: '#F2C9A6',
        reminderTimeKey: 'reminderTimePar',
        reminderBgColor: '#f3dece'
      },
      {
        type: ReminderType.ATTENDEE,
        title: 'Sélectionnés',
        text: 'Après avoir confirmé sa place, en attente de paiement',
        bgColor: '#F3F2BA',
        reminderTimeKey: 'reminderTimeAtt',
        reminderBgColor: '#fafae1'
      }
    ]

    const reminderBlocks = reminderBlocksSke.map(block => {
      return {
        ...block,
        short: getShorterType(block.type), // Ajout du type réduit
      }
    })

    return {
      configListFromDb: [] as Config[], // permet de sauvegarder ce qui est récupéré au load, pour savoir si une config a été modifié
      configList: [] as Config[],
      remindersConfigs: [] as ReminderConfig[],
      templateMailList: [] as String[],
      templateSmsList: [] as String[],
      reminderBlocks
    }
  },

  provide() {
    return {
      templateMailList: this.templateMailList,
      templateSmsList: this.templateSmsList
    }
  },

  created() {
    this.$watch(() => this.$route.params, () => this.load(), {immediate: true})
  },

  methods: {
    toInt,
    load() {
      axios.get('/config').then((response) => {
        this.configList = [...response.data]
        this.configList.push({key: "bcOpenDate",
          value: dayjs(this.getConfigByKey("bcCloseDate")?.value).subtract(2, 'day').format('YYYY-MM-DDTHH:mm:ssZ')})

        this.configListFromDb = this.configList.map(item => ({...item}))

        const templatesMail = this.getConfigByKey("reminderTemplateMail").value
        const objMail = JSON.parse(templatesMail)
        this.templateMailList.push('', ...Object.keys(objMail))

        const templatesSms = this.getConfigByKey("reminderTemplateSms").value
        const objSms = JSON.parse(templatesSms)
        this.templateSmsList.push('', ...Object.keys(objSms))
      })

      axios.get('/reminders/config').then((response) => {
        const data: ReminderConfigRes[] = response.data

        const converted = data.map(v => ({
          ...v,
          type: ReminderType[v.type]
        }))
        this.remindersConfigs = [...converted]
      })
    },

    getArrayByType(type: ReminderType, sort: boolean = false): ReminderConfig[] {
      const list = this.remindersConfigs.filter(v => v.type === type)
      if (sort)
        list.sort((a, b) => a.hours > b.hours ? -1 : 1)
      return list
    },

    getConfigByKey(key: String): Config {
      const found = this.configList.find(cr => cr.key === key)
      if (!found) {
        this.configList.push({key: key, value: ''} as Config) // si la BD est vide, la clé n'existe pas encore
      }
      return this.configList.find(cr => cr.key === key)! // la nouvelle clef est forcément dedans
    },

    async updateReminder(method: string, reminderConfig: ReminderConfig, id?: string): Promise<void> {
      return axios.request({ method, url: `/reminders/config${id ? '/' + id : ''}`, data: reminderConfig })
        .then(res => {
          const index = this.remindersConfigs.findIndex(re => isReminderConfigRes(re) && re.id == id)
          if (index !== -1)
            this.remindersConfigs.splice(index,1, res.data as ReminderConfigRes)
          else
            this.remindersConfigs.push(res.data as ReminderConfigRes)

          this.remindersConfigs.sort((a, b) => a.hours > b.hours ? -1 : 1)
        }).catch(() => toastError(`Erreur lors de ${(method === "post" ? "l'insertion" : "la modification")} d'un reminder.`))
    },

    deleteReminder(id: string, reminderConfigType: ReminderType) {
      axios.delete(`/reminders/config/${id}`)
        .then(() => { // on supprime de la liste des reminders
          const index = this.remindersConfigs.findIndex(re => isReminderConfigRes(re) && re.id === id)
          if (index > -1) this.remindersConfigs.splice(index, 1)
          toastSuccess(`Suppression d'un rappel ${reminderConfigType.toLowerCase()}`)
        }).catch(() => {
          toastError(`Erreur lors de la suppression d'un rappel ${reminderConfigType.toLowerCase()}`)
        })
    },

    remindersManager(update: ReminderUpdate, reminderConfigType: ReminderType) {
      // Centralise la gestion des rappels

      // Mise à jour du temps maximal
      const configKey: string = `reminderTime${getShorterType(reminderConfigType)}`
      const configTime: Config = this.getConfigByKey(configKey)

      let hasChanges: boolean = this.updateMaxTime(update, reminderConfigType, configKey, configTime)

      // Mise à jour / ajout des rappels
      const requestsModifs: Promise<any>[] = this.updateReminders(update, configTime)
      if (requestsModifs.length !== 0) hasChanges = true

      if (requestsModifs.length > 0)
        Promise.allSettled(requestsModifs).then(() => {
          toastSuccess(`Modification de ${requestsModifs.length} rappels ${reminderConfigType.toLowerCase()}`)
        })

      if (requestsModifs.length === 0 && !hasChanges)
        toastWarning('Aucune modification')
    },

    hasChanges(remindConfig: ReminderConfigRes): boolean {
      // Vérifie qu'une config de rappel a été modifié
      let oldRemindConfig: ReminderConfigRes | undefined
      oldRemindConfig = this.remindersConfigs.find(re => isReminderConfigRes(re) && re.id === remindConfig.id) as ReminderConfigRes
      return oldRemindConfig ? !deepEqual(oldRemindConfig, remindConfig) : false
    },

    updatePass() {
      // Fonction qui gère les modifications de la partie "Billets"
      const configsToUpdate: Config[] = []
      this.configList.forEach(config => {
        const oldvalue = this.configListFromDb.find(c => c.key === config.key)?.value
        if (config.value !== oldvalue) {
          configsToUpdate.push(config)
        }
      })

      axios.put('/config', configsToUpdate).then(() => {
        toastSuccess('Modification des configurations des billets')
      }).catch(() => {
        toastError('Erreur lors de la modification des configurations des billets')
      })
    },

    updateMaxTime(update: ReminderUpdate, reminderConfigType: String, configKey: string, configTime: Config): boolean {
      if (update.reminderTime === update.oldReminderTime || update.reminderTime < 0) {
        return false;
      }
      axios.put('/config', [{
        key: configKey,
        value: `${update.reminderTime}`
      }]).then(() => {
        toastSuccess(`Modification du temps maximal ${reminderConfigType}`)
        // Mise à jour dans la configList
        configTime.value = `${update.reminderTime}`
      }).catch(() => {
        toastError(`Erreur lors de la modification du temps maximal ${reminderConfigType.toLowerCase()}`)
      })
      return true
    },

    updateReminders(update: ReminderUpdate, configTime: Config): Promise<any>[] {
      const requestsModifs: Promise<any>[] = []
      update.reminders.forEach((remind, index) => {
        if (isReminderConfigRes(remind)) {
          // Mise à jour d'un reminderConfig existant
          const {id, ...remindWithoutId} = remind
          if (this.hasChanges(remind) && remind.hours >= 0 && remind.hours < toInt(configTime.value)
              && (remind.templateSms !== "" || remind.templateMail !== ""))
            requestsModifs.push(this.updateReminder('put', remindWithoutId, id))
        } else {
          // Création d'un nouveau reminderConfig
          if (remind.hours >= 0 && remind.hours <= toInt(configTime.value)
              && (remind.templateSms !== "" || remind.templateMail !== "")) {
            requestsModifs.push(this.updateReminder('post', remind))
          } else {
            toastError(`Erreur dans le formulaire du rappel ${index + 1}`)
          }
        }
      })
      return requestsModifs
    }
  }
})

</script>

<style scoped>
.col-config {
  flex-basis: 32%;
}
#form-ticket > * {
  flex-basis: 25%;
}
</style>