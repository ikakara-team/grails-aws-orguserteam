grails {
  plugin {
    awsorguserteam {
      homePath = "/"
      invalidAccessRedirectUri = "/welcome"
      userNotFoundRedirectUri = "/my-profile"
      defaultReturnUri = "/welcome"
      dataSource {
        dbPrefix="DEV"
        dbCreate="create" //'create', 'create-drop',''
      }
    }
  }
}
