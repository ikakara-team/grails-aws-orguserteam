import ikakara.orguserteam.dao.dynamo.IdUser
import ikakara.orguserteam.dao.dynamo.IdOrg
import ikakara.orguserteam.dao.dynamo.IdFolder
import ikakara.orguserteam.dao.dynamo.IdEmail
import ikakara.orguserteam.dao.dynamo.IdSlug
import ikakara.orguserteam.dao.dynamo.IdUserOrg
import ikakara.orguserteam.dao.dynamo.IdUserFolder
import ikakara.orguserteam.dao.dynamo.IdOrgFolder
import ikakara.orguserteam.dao.dynamo.IdEmailOrg
import ikakara.orguserteam.dao.dynamo.IdEmailFolder

//grails.mime.use.accept.header = true
grails.mime.types = [ // the first one is the default format
  all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
  atom:          'application/atom+xml',
  css:           'text/css',
  csv:           'text/csv',
  form:          'application/x-www-form-urlencoded',
  html:          ['text/html','application/xhtml+xml'],
  js:            'text/javascript',
  json:          ['application/json', 'text/json'],
  multipartForm: 'multipart/form-data',
  rss:           'application/rss+xml',
  text:          'text/plain',
  hal:           ['application/hal+json','application/hal+xml'],
  xml:           ['text/xml', 'application/xml']
]

log4j = {
  error 'org.codehaus.groovy.grails',
        'org.springframework'
  info  'grails.app'
}

// for testing
grails.validateable.classes = [IdUser, IdOrg, IdFolder, IdEmail, IdSlug, IdUserOrg, IdUserFolder, IdOrgFolder, IdEmailOrg, IdEmailFolder]
