package ikakara.orguserteam.web.app

import ikakara.orguserteam.web.app.ABaseUserController

class TestUserController extends ABaseUserController {

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
