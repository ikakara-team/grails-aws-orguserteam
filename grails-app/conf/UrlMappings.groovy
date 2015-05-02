class UrlMappings {

  static mappings = {
    // for testing
    "/my-invitations/$id?(.$format)?"(controller: "userTest", parseRequest: true) {
      action = [GET: "invitations", POST: "joinInvitation", DELETE: "deleteInvitation"]
    }
    "/my-orgs/$id?(.$format)?"(controller: "userTest", parseRequest: true) {
      action = [GET: "orgs", PUT: "updateOrg", POST: "saveOrg", DELETE: "deleteOrg"]
    }
    "/my-teams/$id?(.$format)?"(controller: "userTest", parseRequest: true) {
      action = [GET: "teams", PUT: "updateTeam", POST: "saveTeam", DELETE: "deleteTeam"]
    }
    "/my-groups(.$format)?"(controller: "userTest", action: "groups")

    "/$controller/$action?/$id?(.$format)?"{
      constraints {
        // apply constraints here
      }
    }

    "/"(view:"/index")
    "500"(view:'/error')
  }
}
