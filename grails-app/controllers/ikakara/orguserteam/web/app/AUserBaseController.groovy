package ikakara.orguserteam.web.app

import grails.compiler.GrailsCompileStatic
import groovy.transform.TypeCheckingMode

import grails.converters.JSON
import grails.converters.XML

import ikakara.awsinstance.util.StringUtil

import ikakara.orguserteam.dao.dynamo.IdUser
import ikakara.orguserteam.dao.dynamo.IdUserOrg
import ikakara.orguserteam.dao.dynamo.IdUserTeam
import ikakara.orguserteam.dao.dynamo.IdOrg
import ikakara.orguserteam.dao.dynamo.IdSlug
import ikakara.orguserteam.dao.dynamo.IdTeam
import ikakara.orguserteam.dao.dynamo.IdEmail

@GrailsCompileStatic
abstract class AUserBaseController extends ABaseController implements IAccessController {
  static allowedMethods = [
    invitations: "GET", joinInvitation: "POST", deleteInvitationTBD: "DELETE",
    orgs: "GET", updateOrgTBD: "PUT", saveOrgTBD: "POST", deleteOrgTBD: "DELETE",
    teams: "GET", updateTeamTBD: "PUT", saveTeamTBD: "POST", deleteTeamTBD: "DELETE",
    groups: "GET"
  ]

  static responseFormats = ['json','xml']

  def beforeInterceptor = [action: this.&validateAccess]

  // Assumption: users can access their own info
  private validateAccess() {
    String userId = getUserId()

    IdUser user = ((OrgUserTeamService)orgUserTeamService).user(userId)
    request.setAttribute(USER_KEY, user)

    setAttributeUserEmailAndInvited()

    return true
  }

  //@GrailsCompileStatic(TypeCheckingMode.SKIP)
  def invitations() {
    IdEmail email = (IdEmail)request.getAttribute(USEREMAIL_KEY)

    if(params.id) {
      try {
        def joinGroup = ((OrgUserTeamService)orgUserTeamService).findIdObjBySlugId((String)params.id)
        if(!joinGroup) {
          respondError(404, "'${params.id}' Not Found")
          return
        }

        // check to see if user is a member of the org
        def emailgroup = ((OrgUserTeamService)orgUserTeamService).exist(email, joinGroup)
        if(!emailgroup) {
          log.error("User not invited to '${params.id}'")
          respondError(404, "'${params.id}' Not Found")
          return
        }

        if(!joinGroup.load()) {
          log.error("Failed to load '${params.id}'")
        }

        def map = [success: true, data: joinGroup]

        respond map
      } catch(e) {
        log.error("Exception '${params.id}': " + e.message)
        respondError(500, "Whoops, we've got issues")
        return
      }
    } else {
      List list = ((OrgUserTeamService)orgUserTeamService).listGroup(email)
      def listGroup = list?.collect { it -> it.group }

      def map = [success: true, data: listGroup]

      respond map
    }
  }

  @GrailsCompileStatic(TypeCheckingMode.SKIP)
  def joinInvitation() {
    def user = request.getAttribute(USER_KEY)
    def email = request.getAttribute(USEREMAIL_KEY)

    def joinGroup = orgUserTeamService.findIdObjBySlugId(params.id)
    if(joinGroup) {
      def emailgroup = orgUserTeamService.exist(email, joinGroup)
      if(emailgroup) {
        def added = orgUserTeamService.addUserToGroup(emailgroup.invitedBy, user, joinGroup, null)
        if(added) {
          flash.message = "Successfully joined ${params.id}"
          emailgroup.delete()
        } else {
          flash.message = "Failed to join ${params.id}"
        }
      } else {
        flash.message = "Invalid join ${params.id}"
      }
    } else {
      flash.message = "Failed to find ${params.id}"
    }

    redirect uri: grailsApplication.config.grails.plugin.awsorguserteam?.defaultJoinReturnUri
  }

  def orgs() {
    IdUser user = (IdUser)request.getAttribute(USER_KEY)

    if(params.id) {
      try {
        IdOrg org = IdOrg.fromSlug((String)params.id)
        if(!org) {
          respondError(404, "'${params.id}' Not Found")
          return
        }

        // check to see if user is a member of the org
        def userorg = org.hasMember(user)
        if(!userorg) {
          log.error("User not member of '${params.id}'")
          respondError(404, "'${params.id}' Not Found")
          return
        }

        if(!org.load()) {
          log.error("Failed to load '${params.id}'")
        }

        def map = [success: true, data: org]

        respond map
      } catch(e) {
        log.error("Exception '${params.id}': " + e.message)
        respondError(500, "Whoops, we've got issues")
        return
      }
    } else {
      List list = ((OrgUserTeamService)orgUserTeamService).listOrg(user)
      def listOrg = list?.collect { IdUserOrg it -> it.group }

      def map = [success: true, data: listOrg]

      respond map
    }
  }

  def teams() {
    IdUser user = (IdUser)request.getAttribute(USER_KEY)

    if(params.id) {
      try {
        IdTeam team = IdTeam.fromSlug((String)params.id)
        if(!team) {
          respondError(404, "'${params.id}' Not Found")
          return
        }

        // check to see if user is a member of the team
        def userteam = team.hasMember(user)
        if(!userteam) {
          log.error("User not member of '${params.id}'")
          respondError(404, "'${params.id}' Not Found")
          return
        }

        if(!team.load()) {
          log.error("Failed to load '${params.id}'")
        }

        def map = [success: true, data: team]

        respond map
      } catch(e) {
        log.error("Exception '${params.id}': " + e.message)
        respondError(500, "Whoops, we've got issues")
        return
      }
    } else {
      List list = ((OrgUserTeamService)orgUserTeamService).listTeam(user)
      def listTeam = list?.collect { IdUserTeam it -> it.group }

      def map = [success: true, data: listTeam]

      respond map
    }
  }

  def groups() {
    IdUser user = (IdUser)request.getAttribute(USER_KEY)

    List list = ((OrgUserTeamService)orgUserTeamService).listGroup(user)
    def listGroup = list?.collect { it -> it.group }

    def map = [success: true, data: listGroup]

    respond map
  }

}
