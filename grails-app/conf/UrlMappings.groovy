class UrlMappings {

  static mappings = {
    // for testing
    "/my-invitations/$id?(.$format)?"(controller: "testUser", parseRequest: true) {
      action = [GET: "invitations", POST: "joinInvitation", DELETE: "deleteInvitation"]
    }
    "/my-orgs/$id?(.$format)?"(parseRequest: true) {
      //debug = { println "urlmap m: ${request.method} s: ${request.servletPath} r: ${request.getHeader('referer')} q: ${request.queryString} p: ${params}" }
      controller = [GET: "testUser", PUT: "testOrg", POST: "testUser", DELETE: "testOrg"]
      action = [GET: "orgs", PUT: "updateOrg", POST: "saveOrg", DELETE: "deleteOrg"]
    }
    "/my-folders/$id?(.$format)?"(parseRequest: true) {
      controller = [GET: "testUser", PUT: "testOrg", POST: "testOrg", DELETE: "testOrg"]
      action = [GET: "folders", PUT: "updateFolder", POST: "saveFolder", DELETE: "deleteFolder"]
    }
    "/my-groups(.$format)?"(controller: "testUser", action: "groups")

    // kept for posterity
    "/$controller/$action?/$id?(.$format)?"{
      constraints {
        // apply constraints here
      }
    }
    "/"(view:"/index")
    "500"(view:'/error')
  }
}
