package ikakara.orguserteam.web.app

import grails.compiler.GrailsCompileStatic
import groovy.transform.TypeCheckingMode

import ikakara.orguserteam.dao.dynamo.IdUser
import ikakara.orguserteam.dao.dynamo.IdUserOrg
import ikakara.orguserteam.dao.dynamo.IdOrg
import ikakara.orguserteam.dao.dynamo.IdSlug
import ikakara.orguserteam.dao.dynamo.IdFolder

import ikakara.awsinstance.util.StringUtil

//@GrailsCompileStatic
abstract class ABaseOrgController extends ABaseController implements IAccessController {
  def beforeInterceptor = [action: this.&validateAccess]

  // insure that user has access:
  // org access requires that user is member of org
  // folder access requires that user is
  // 1) member of folder
  // 2) an org owner/admin
  // 3) member of org and folder is visible to org members
  private validateAccess() {
    def userId    = getUserId()

    def user = orgUserTeamService.user(userId, false)
    if(!user) {
      redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.userNotFoundRedirectUri
      return false
    }

    request.setAttribute(USER_KEY, user)

    setAttributeUserEmailAndInvited()

    setAttributeMemberAll(user)

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

    def folderId = getFolderSlugId()
    boolean folderaccess = false
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

      if(folderId) { // see if we need folder access
        // we allow folder access to org owners and admins
        folderaccess = orgUserTeamService.haveOrgRole(org, user, IdUserOrg.FOLDER_VISIBLE)
      }
    } else {
      // we do not have org access
    }

    if(folderId) { // verify folderId
      // validate that the slug is for an folder
      def folder = IdFolder.fromSlug(folderId)
      if(!folder) {
        flash.message = "Failed to find '${folderId}'"
        redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.invalidAccessRedirectUri
        return false
      }

      // load folder
      def bload = folder.load()
      if(!bload) {
        flash.message = "Failed to load '${folderId}'"
        redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.invalidAccessRedirectUri
        return false
      }

      // make sure folder owner is correct
      if(!folder.ownerEquals(org)) {
        flash.message = "Invalid owner for '${folderId}'"
        redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.invalidAccessRedirectUri
        return false
      }

      // verify folder access
      if(!folderaccess && !orgUserTeamService.isFolderVisible(folder, user, orgaccess)) {
        flash.message = "Failed to find '${folderId}'"
        redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.invalidAccessRedirectUri
        return false
      }

      request.setAttribute(FOLDER_KEY, folder)
    } else if(!orgaccess) {
      // we don't have org or folder access
      flash.message = "Failed to find '${orgId}'"
      redirect uri: grailsApplication.config.grails.plugin.awsorguserteam.invalidAccessRedirectUri
      return false
    }

    return true
  }

}
