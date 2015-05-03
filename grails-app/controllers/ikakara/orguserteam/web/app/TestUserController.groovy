package ikakara.orguserteam.web.app

import ikakara.orguserteam.web.app.ABaseUserController

class TestUserController extends ABaseUserController {

  String getOrgSlugId() {
    return params.id
  }

  String getFolderSlugId() {
    return params.appId
  }

  String getUserEmail() {
    return ''
  }

  String getUserId(){
    return ''
  }

}
