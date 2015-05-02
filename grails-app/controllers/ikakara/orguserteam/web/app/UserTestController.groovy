package ikakara.orguserteam.web.app

import ikakara.orguserteam.web.app.AUserBaseController

class UserTestController extends AUserBaseController {

  String getOrgSlugId() {
    return params.id
  }

  String getTeamSlugId() {
    return params.appId
  }

  String getUserEmail() {
    return ''
  }

  String getUserId(){
    return ''
  }

}
