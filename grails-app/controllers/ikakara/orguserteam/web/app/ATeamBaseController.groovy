package ikakara.orguserteam.web.app

import grails.compiler.GrailsCompileStatic
import groovy.transform.TypeCheckingMode

import ikakara.orguserteam.dao.dynamo.IdUser
import ikakara.orguserteam.dao.dynamo.IdUserOrg
import ikakara.orguserteam.dao.dynamo.IdOrg
import ikakara.orguserteam.dao.dynamo.IdSlug
import ikakara.orguserteam.dao.dynamo.IdTeam

import ikakara.awsinstance.util.StringUtil

//@GrailsCompileStatic
abstract class ATeamBaseController extends ABaseController implements IAccessController {
  def beforeInterceptor = [action: this.&validateAccess]

  // insure that user has access:
  // org access requires that user is member of org
  // team access requires that user is
  // 1) member of team
  // 2) an org owner/admin
  // 3) member of org and team is visible to org members
  private validateAccess() {
    def userId    = getUserId()

    def user = orgUserTeamService.user(userId, false)
    if(!user) {
      redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.userNotFoundRedirectUri
      return false
    }

    request.setAttribute(USER_KEY, user)

    setAttributeUserEmailAndInvited()

    setAttributeMemberTeam(user)

    ///////////////////////////////////////////////////////////////////////////
    // validate access
    ///////////////////////////////////////////////////////////////////////////

    def teamId = getTeamSlugId()

    // validate that the slug is for an team
    def team = IdTeam.fromSlug(teamId)
    if(!team) {
      flash.message = "Failed to find '${teamId}'"
      redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.invalidAccessRedirectUri
      return false
    }

    // load team
    def bload = team.load()
    if(!bload) {
      flash.message = "Failed to load '${teamId}'"
      redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.invalidAccessRedirectUri
      return false
    }

    def visible = orgUserTeamService.isTeamVisible(team, user)
    if(!visible) {
      flash.message = "Failed to find '${teamId}'"
      redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.invalidAccessRedirectUri
      return false
    }

    request.setAttribute(TEAM_KEY, team)

    if(team.isOwnerOrg() && orgUserTeamService.isOrgVisible(team.owner, user)) {
      request.setAttribute(ORG_KEY, team.owner)
    }

    return true
  }

}
