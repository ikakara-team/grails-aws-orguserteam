package ikakara.orguserteam.web.app

import grails.compiler.GrailsCompileStatic
import groovy.transform.TypeCheckingMode

import ikakara.orguserteam.dao.dynamo.IdUser
import ikakara.orguserteam.dao.dynamo.IdUserOrg
import ikakara.orguserteam.dao.dynamo.IdOrg
import ikakara.orguserteam.dao.dynamo.IdSlug
import ikakara.orguserteam.dao.dynamo.IdFolder

import ikakara.awsinstance.util.StringUtil

@GrailsCompileStatic
abstract class ABaseFolderController extends ABaseController implements IAccessController {
  def beforeInterceptor = [action: this.&validateAccess]

  // insure that user has access:
  // org access requires that user is member of org
  // folder access requires that user is
  // 1) member of folder
  // 2) an org owner/admin
  // 3) member of org and folder is visible to org members
  protected validateAccess() {
    def userId    = getUserId()

    def user = ((OrgUserTeamService)orgUserTeamService).user(userId, false)
    if(!user) {
      redirectUserNotFoundRedirectUri()
      return false
    }

    request.setAttribute(USER_KEY, user)

    setAttributeUserEmailAndInvited()

    setAttributeMemberFolder(user)

    ///////////////////////////////////////////////////////////////////////////
    // validate access
    ///////////////////////////////////////////////////////////////////////////

    def folderId = getFolderSlugId()

    // validate that the slug is for an folder
    def folder = IdFolder.fromSlug(folderId)
    if(!folder) {
      flash.message = "Failed to find '${folderId}'"
      redirectInvalidAccessRedirectUri()
      return false
    }

    // load folder
    def bload = folder.load()
    if(!bload) {
      flash.message = "Failed to load '${folderId}'"
      redirectInvalidAccessRedirectUri()
      return false
    }

    def visible = ((OrgUserTeamService)orgUserTeamService).isFolderVisible(folder, user)
    if(!visible) {
      flash.message = "Failed to find '${folderId}'"
      redirectInvalidAccessRedirectUri()
      return false
    }

    request.setAttribute(FOLDER_KEY, folder)

    if(folder.isOwnerOrg() && ((OrgUserTeamService)orgUserTeamService).isOrgVisible((IdOrg)folder.owner, user)) {
      request.setAttribute(ORG_KEY, folder.owner)
    }

    return true
  }

}
