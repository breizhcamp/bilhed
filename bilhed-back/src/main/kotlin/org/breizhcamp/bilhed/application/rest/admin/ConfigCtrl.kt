package org.breizhcamp.bilhed.application.rest.admin

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.breizhcamp.bilhed.application.dto.admin.ConfigReq
import org.breizhcamp.bilhed.application.dto.admin.ConfigRes
import org.breizhcamp.bilhed.domain.entities.Config
import org.breizhcamp.bilhed.domain.use_cases.ConfigCrud
import org.breizhcamp.bilhed.domain.use_cases.ConfigDate
import org.breizhcamp.bilhed.domain.use_cases.ConfigTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController("adminConfigController")
@RequestMapping("/admin/config")
class ConfigCtrl(
    private val configDate: ConfigDate,
    private val configCrud: ConfigCrud,
    private val configTemplates: ConfigTemplate,
) {

    @GetMapping
    fun config(): ResponseEntity<List<ConfigRes>> {
        val configList = mutableListOf<ConfigRes>()
        configList.add(ConfigRes("registeredCloseDate", configDate.getRegistrationCloseDate().toString()))
        configList.add(ConfigRes("bcCloseDate", configDate.getBreizhCampCloseDate().toString()))
        configList.add(ConfigRes("reminderTemplateMail", jacksonObjectMapper().writeValueAsString(configTemplates.getEmailTemplates())))
        configList.add(ConfigRes("reminderTemplateSms", jacksonObjectMapper().writeValueAsString(configTemplates.getSMSTemplates())))
        configList.addAll(configCrud.list().map { it.toConfigRes() })
        return ResponseEntity.ok(configList)
    }

    @GetMapping("/{key}")
    fun getConfig(@PathVariable key: String): ConfigRes = configCrud.getByKey(key).toConfigRes()

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    fun addConfig(@RequestBody configs: List<ConfigReq>): ResponseEntity<List<ConfigRes>> {
        val configList: MutableList<ConfigRes> = mutableListOf()
        for (config in configs) {
            configList.add(configCrud.add(config.toConfig()).toConfigRes())
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(configList)
    }

    @PutMapping @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateConfig(@RequestBody configs: List<ConfigReq>) {
        for (config in configs) {
            configCrud.update(config.toConfig())
        }
    }

    @DeleteMapping("/{key}") @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteConfig(@PathVariable key: String) {
        configCrud.delete(key)
    }

}

fun ConfigReq.toConfig() = Config(
    key = this.key,
    value = this.value,
)

fun Config.toConfigRes() = ConfigRes(
    key = this.key,
    value = this.value,
)