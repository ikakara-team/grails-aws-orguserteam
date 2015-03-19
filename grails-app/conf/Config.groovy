// configuration for plugin testing - will not be included in the plugin zip

log4j = {

  error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
         'org.codehaus.groovy.grails.web.pages', //  GSP
         'org.codehaus.groovy.grails.web.sitemesh', //  layouts
         'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
         'org.codehaus.groovy.grails.web.mapping', // URL mapping
         'org.codehaus.groovy.grails.commons', // core / classloading
         'org.codehaus.groovy.grails.plugins', // plugins
         'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
         'org.springframework',
         'org.hibernate',
         'net.sf.ehcache.hibernate'
}

// for testing
grails.validateable.classes = [
  // orguserteam
  ikakara.orguserteam.dao.dynamo.IdUser,
  ikakara.orguserteam.dao.dynamo.IdOrg,
  ikakara.orguserteam.dao.dynamo.IdTeam,
  ikakara.orguserteam.dao.dynamo.IdEmail,
  ikakara.orguserteam.dao.dynamo.IdSlug,
  ikakara.orguserteam.dao.dynamo.IdUserOrg,
  ikakara.orguserteam.dao.dynamo.IdUserTeam,
  ikakara.orguserteam.dao.dynamo.IdOrgTeam,
]
