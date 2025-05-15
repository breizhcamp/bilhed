<template>
  <form class="g-3 mt-2 mb-3" @submit.prevent="submitReminders">
    <div class="form-group mb-4">
      <label class="form-label fw-bold" :for="`time${rType}`">Temps maximal pour confirmer </label>
      <div class="input-group input-group-sm">
        <input type="number" class="form-control" :id="`time${rType}`" v-model="rTime" min="0">
        <span class="input-group-text">heures</span>
      </div>
    </div>
    <p class="m-0">Les rappels sont envoyés X heures avant la fin du temps maximal</p>

    <hr>
    <template v-for="(item, i) in r || []" :key="`reminder-${rType}-${i}`">
      <div class="d-flex flex-column mb-2 p-2 shadow rounded" :style="{ backgroundColor: reminderBgColor }" >
        <div class="form-group mb-3">
          <div class="d-flex flex-row justify-content-between mb-2">
            <label class="form-label fw-bold mb-0" :for="`remind${rType}-${i+1}`">Relance {{ i+1 }}</label>
            <button type="button" class="btn btn-danger rounded-circle"
                    @click="() => deleteReminder(item, i)"
                    style="--bs-btn-padding-y: .1rem; --bs-btn-padding-x: .3rem; --bs-btn-font-size: .75rem;">
              <i class="bi bi-trash"></i>
            </button>
          </div>
          <div class="input-group input-group-sm">
            <input type="number" class="form-control " v-bind:id="`remind${rType}-${i+1}`" v-model="item.hours" min="0" :max="rTime" />
            <span class="input-group-text">heures avant</span>
          </div>
        </div>
        <div class="d-flex flex-row justify-content-between">
          <div class="form-group me-2">
            <label :for="`reminderTemplateMail${rType}-${i+1}`">Template Mail</label>
            <select class="form-select form-select-sm input-xs" v-model="item.templateMail" v-bind:id="`reminderTemplateMail${rType}-${i+1}`">
              <option v-for="t in templateMailList" :value="t" :key="t" :selected="t === item.templateMail">{{ t }}</option>
            </select>
          </div>
          <div class="form-group">
            <label :for="`reminderTemplateSms${rType}-${i+1}`">Template SMS</label>
            <select class="form-select form-select-sm input-xs" v-model="item.templateSms" v-bind:id="`reminderTemplateSms${rType}-${i+1}`">
              <option v-for="t in templateSmsList" :value="t" :key="t" :selected="t === item.templateSms">{{ t }}</option>
            </select>
          </div>
        </div>
      </div>

    </template>
    <button type="button" class="btn btn-sm btn-block shadow rounded mb-4" :style="{ backgroundColor: reminderBgColor }"
            @click="addReminder" style="width: 100%">Ajouter un rappel</button>
    <button type="submit" class="btn btn-primary btn-sm">Enregistrer</button>
  </form>
</template>

<script lang="ts">
import {defineComponent, type PropType} from "vue";
import {type ReminderConfig, ReminderType, type ReminderUpdate} from "@/dto/ReminderConfig";
import {getShorterType, isReminderConfigRes} from "@/utils/ReminderUtils";

export default defineComponent({
  name: "RemindersSection",
  props : {
    reminders: { type: Array as PropType<ReminderConfig[]>, required: true},
    reminderTime: { type: Number, required: true},
    reminderType: { type: String as PropType<ReminderType>, required: true},
    reminderBgColor: { type: String, required: true }
  },
  emits: ['submit', 'delete'],
  inject: ["templateMailList", "templateSmsList"],

  data() {
    return {
      r: this.reminders.map(reminder => ({ ...reminder })) as ReminderConfig[],
      rTime: this.reminderTime,
      rType: getShorterType(this.reminderType),
    }
  },

  watch: {
    reminders(newVal: ReminderConfig[]) {
      this.r = newVal.map(reminder => ({ ...reminder }))
    },
    reminderTime(newVal: number) {
      this.rTime = newVal
    },
    reminderType(newVal: ReminderType) {
      this.rType = getShorterType(newVal)
    }
  },

  methods: {
    submitReminders() {
      this.$emit('submit',
          { reminders: this.r, reminderTime: this.rTime,
            oldReminderTime: this.reminderTime } as ReminderUpdate)
    },

    deleteReminder(reminder: ReminderConfig, index: number) {
      if (isReminderConfigRes(reminder)) {
        this.$emit('delete', reminder)
      } else {
        this.r.splice(index, 1)
      }
    },

    addReminder() {
      this.r.push({type: this.reminderType, hours: 0, templateMail: "", templateSms: ""})
    }
  }

})
</script>


<style scoped>
.form-group {
  flex-basis: 50%;
}
.input-xs {
  height: 22px;
  padding: 2px 5px;
  font-size: 12px;
  line-height: 1.5; /* If Placeholder of the input is moved up, rem/modify this. */
  border-radius: 3px;
}
</style>