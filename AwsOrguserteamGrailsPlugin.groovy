import ikakara.orguserteam.dao.dynamo.IdOrg
import ikakara.orguserteam.dao.dynamo.IdFolder
import ikakara.orguserteam.dao.dynamo.IdUser
import ikakara.orguserteam.dao.dynamo.IdEmail
import ikakara.simplemarshaller.web.app.SimpleMarshallerService

class AwsOrguserteamGrailsPlugin {
  def version = "0.9.7"
  def grailsVersion = "2.2 > *"
  def loadAfter = ['aws-instance']
  def pluginExcludes = [
    "grails-app/i18n/*",
    "grails-app/views/error.gsp",
    "grails-app/views/index.gsp",
    "grails-app/controllers/ikakara/orguserteam/web/app/TestUserController.groovy",
    "grails-app/controllers/ikakara/orguserteam/web/app/TestOrgController.groovy",
    "web-app/**"
  ]
  def title = "AWS Org-User-Folder Plugin"
  def author = "Allen Arakaki"
  def description = '''Team is now Folder to disambiguate its relationship from Orgs and Users.

Org-User-Folder is a very common "design pattern" used in just about every SAAS app. In the past, the relationships would be easily represented in SQL. However, this meant that you would have to setup/configure/maintain a DB. Even in the cloud, there is still devops work. So welcome to modern development where services and plugins are the building blocks of apps, rather than "boxes" and libraries.

There are tradeoffs with using NOSQL (service) vs SQL (box):
* Boxes require devops! Prefer services to boxes :)
* NOSQL sucks at relationships ... Prefer services to boxes!!!\n\

This plugin takes care of the suckiness of NOSQL, in preference to keeping devops to a minimum.'''
  def documentation = "http://grails.org/plugin/aws-orguserteam"
  def license = "APACHE"
  def issueManagement = [url: 'https://github.com/ikakara-team/grails-aws-orguserteam/issues']
  def scm = [url: 'https://github.com/ikakara-team/grails-aws-orguserteam']

  def doWithApplicationContext = { appCtx ->
    println "Configuring AwsOrgUserTeam config ...$application.mergedConfig.conf.grails.plugin.awsorguserteam"

    if(appCtx) {
      //def sessionFactory = appCtx?.sessionFactory
      // do something here with session factory
      println 'Registering simpleMarshallerService ...'

      def simpleMarshallerService = appCtx.getBean(SimpleMarshallerService)

      // We need to register the object marshaller
      [IdOrg, IdUser, IdFolder, IdEmail].each { simpleMarshallerService.register it }

      println '... finished registering simpleMarshallerService'
    }

    println '... finished configuring AwsOrgUserTeam config'
  }

  def afterConfigMerge = {config, ctx ->
    // let's put the mergedConfig in ctx
    ctx.appConfig.grails.plugin.awsorguserteam.putAll(config.grails.plugin.awsorguserteam)
  }
}
