grails {
  plugin {
    awsorguserteam {
      homePath = "/"
      invalidAccessRedirectUri = "/welcome"
      userNotFoundRedirectUri = "/profile"
      dataSource {
        dbPrefix="DEV"
        dbCreate="create" //'create', 'create-drop',''
      }
    }
  }
}
