package ikakara.orguserteam.web.app

import ikakara.orguserteam.web.app.ABaseOrgController

class TestOrgController extends ABaseOrgController {

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
