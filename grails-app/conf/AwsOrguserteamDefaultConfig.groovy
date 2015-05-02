grails {
  plugin {
    awsorguserteam {
      homePath = "/"
      invalidAccessRedirectUri = "/welcome"
      userNotFoundRedirectUri = "/profile"
      defaultJoinReturnUri = "/welcome"
      dataSource {
        dbPrefix="DEV"
        dbCreate="create" //'create', 'create-drop',''
      }
    }
  }
}
