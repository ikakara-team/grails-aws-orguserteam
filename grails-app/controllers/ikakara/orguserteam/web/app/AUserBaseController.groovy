package ikakara.orguserteam.web.app

import groovy.transform.CompileStatic

import ikakara.awsinstance.util.StringUtil

import ikakara.orguserteam.dao.dynamo.IdUser
import ikakara.orguserteam.dao.dynamo.IdUserOrg
import ikakara.orguserteam.dao.dynamo.IdOrg
import ikakara.orguserteam.dao.dynamo.IdSlug
import ikakara.orguserteam.dao.dynamo.IdTeam

@CompileStatic
abstract class AUserBaseController extends ABaseController implements IAccessController {
  def beforeInterceptor = [action: this.&validateAccess]

  // Assumption: users can access their own info
  private validateAccess() {
    String userId = getUserId()

    IdUser user = ((OrgUserTeamService)orgUserTeamService).user(userId)
    request.setAttribute(USER_KEY, user)

    setAttributeUserEmailAndInvited()

    return true
  }

}
