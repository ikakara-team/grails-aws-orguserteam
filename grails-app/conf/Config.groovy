import ikakara.orguserteam.dao.dynamo.IdUser
import ikakara.orguserteam.dao.dynamo.IdOrg
import ikakara.orguserteam.dao.dynamo.IdTeam
import ikakara.orguserteam.dao.dynamo.IdEmail
import ikakara.orguserteam.dao.dynamo.IdSlug
import ikakara.orguserteam.dao.dynamo.IdUserOrg
import ikakara.orguserteam.dao.dynamo.IdUserTeam
import ikakara.orguserteam.dao.dynamo.IdOrgTeam
import ikakara.orguserteam.dao.dynamo.IdEmailOrg
import ikakara.orguserteam.dao.dynamo.IdEmailTeam

log4j = {
  error 'org.codehaus.groovy.grails',
        'org.springframework'
}

// for testing
grails.validateable.classes = [IdUser, IdOrg, IdTeam, IdEmail, IdSlug, IdUserOrg, IdUserTeam, IdOrgTeam, IdEmailOrg, IdEmailTeam]
