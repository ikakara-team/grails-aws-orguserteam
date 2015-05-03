class UrlMappings {

  static mappings = {
    // for testing
    "/my-invitations/$id?(.$format)?"(controller: "testUser", parseRequest: true) {
      action = [GET: "invitations", POST: "joinInvitation", DELETE: "deleteInvitation"]
    }
    "/my-orgs/$id?(.$format)?"(controller: "testUser", parseRequest: true) {
      action = [GET: "orgs", PUT: "updateOrg", POST: "saveOrg", DELETE: "deleteOrg"]
    }
    "/my-folders/$id?(.$format)?"(controller: "testUser", parseRequest: true) {
      action = [GET: "folders", PUT: "updateFolder", POST: "saveFolder", DELETE: "deleteFolder"]
    }
    "/my-groups(.$format)?"(controller: "testUser", action: "groups")

    "/$controller/$action?/$id?(.$format)?"{
      constraints {
        // apply constraints here
      }
    }

    "/"(view:"/index")
    "500"(view:'/error')
  }
}
