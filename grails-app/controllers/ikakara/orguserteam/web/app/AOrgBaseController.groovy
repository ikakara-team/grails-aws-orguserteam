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
abstract class AOrgBaseController extends ABaseController implements IAccessController {
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

    setAttributeMembers(user)

    ///////////////////////////////////////////////////////////////////////////
    // validate access
    ///////////////////////////////////////////////////////////////////////////

    def orgId = getOrgSlugId()

    // validate that the slug is for an org
    def org = IdOrg.fromSlug(orgId)
    if(!org) {
      flash.message = "Failed to find '${orgId}'"
      redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.invalidAccessRedirectUri
      return false
    }

    def teamId = getTeamSlugId()
    boolean teamaccess = false
    boolean orgaccess = false

    // check to see if user is a member of the org
    def userorg = org.hasMember(user)
    if(userorg) {
      orgaccess = true

      // load org only if user has access
      def bload = org.load()
      if(!bload) {
        flash.message = "Failed to load '${orgId}'"
        redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.invalidAccessRedirectUri
        return false
      }

      request.setAttribute(ORG_KEY, org)

      if(teamId) { // see if we need team access
        // we allow team access to org owners and admins
        teamaccess = orgUserTeamService.haveOrgRole(org, user, IdUserOrg.TEAM_VISIBLE)
      }
    } else {
      // we do not have org access
    }

    if(teamId) { // verify teamId
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

      // make sure team owner is correct
      if(!team.ownerEquals(org)) {
        flash.message = "Invalid owner for '${teamId}'"
        redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.invalidAccessRedirectUri
        return false
      }

      // verify team access
      if(!teamaccess && !orgUserTeamService.isTeamVisible(team, user, orgaccess)) {
        flash.message = "Failed to find '${teamId}'"
        redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.invalidAccessRedirectUri
        return false
      }

      request.setAttribute(TEAM_KEY, team)
    } else if(!orgaccess) {
      // we don't have org or team access
      flash.message = "Failed to find '${orgId}'"
      redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.invalidAccessRedirectUri
      return false
    }

    return true
  }

}
