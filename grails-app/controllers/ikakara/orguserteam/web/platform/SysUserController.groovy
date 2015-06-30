/* Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ikakara.orguserteam.web.platform

import static org.springframework.http.HttpStatus.*
import grails.converters.JSON
import grails.converters.XML

import ikakara.orguserteam.dao.dynamo.IdUser

//import grails.plugin.springsecurity.annotation.Secured

//@Secured(['ROLE_ADMIN'])
class SysUserController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def orgUserTeamService

  //////////////////////////////////////////////////////////////////////////////
  // IdUser
  //////////////////////////////////////////////////////////////////////////////

  def index(Integer max) {

    params.max = Math.min(max ?: 10, 100)
    List results = new IdUser().queryByType()

    int count = results ? results.size() : 0

    def json = results as JSON
    //log.debug ">>>>>>>>>>>>>>>>>>>>>>>>>$json"

    respond results, model: [userList: results, userCount: count]
  }

  def show(IdUser userInstance) {

    if(params.id) {
      userInstance.id = params.id
    }

    userInstance.load()

    userInstance.orgListAdd(orgUserTeamService.listOrg(userInstance))

    userInstance.folderListAdd(orgUserTeamService.listFolder(userInstance))

    respond userInstance, model: [userInstance: userInstance]
  }

  def create() {
    render view: 'create', model:[userInstance: new IdUser(params)]
  }

  def save(IdUser userInstance) {
    if (!userInstance) {
      notFoundUser()
      return
    }

    // fix the incoming parameters
    def newalias = params.aliasId

    userInstance = orgUserTeamService.createUser(userInstance, userInstance.name, userInstance.initials, userInstance.description, newalias)
    if(!userInstance) {
      flash.message = "Failed to create: $userInstance.id"
      render view: 'create', model:[userInstance: userInstance]
      return
    }

    request.withFormat {
      json {
        render userInstance as JSON, [status: CREATED]
      }
      xml {
        render userInstance as XML, [status: CREATED]
      }
      '*' {
        flash.message = message(code: 'default.created.message', args: [message(code: 'adminApp.label', default: 'IdUser'), userInstance.id])
        redirect action: 'show', id: userInstance.id
      }
    }
  }

  def edit(IdUser userInstance) {
    if(!params.id) {
      response.sendError(404)
      return
    }

    userInstance.id = params.id
    userInstance.load()

    render view: 'edit', model:[userInstance: userInstance]
  }

  def update(IdUser userInstance) {
    if (!userInstance) {
      notFoundUser()
      return
    }

    // fix the incoming parameters
    def newalias = params.aliasId
    //userInstance.aliasId = params.curalias

    userInstance = orgUserTeamService.updateUser(userInstance, userInstance.name, userInstance.initials, userInstance.description, newalias)

    request.withFormat {
      json {
        render userInstance as JSON, [status: OK]
      }
      xml {
        render userInstance as XML, [status: OK]
      }
      '*' { // form wasn't working
        flash.message = message(code: 'default.updated.message', args: [message(code: 'IdUser.label', default: 'IdUser'), userInstance.id])
        redirect action: 'show', id: userInstance.id
      }
    }
  }

  def delete(IdUser userInstance) {
    if (!userInstance) {
      notFoundUser()
      return
    }

    if(params.id) {
      userInstance.id = params.id
    }

    orgUserTeamService.deleteUser(userInstance)

    request.withFormat {
      json {
        render status: NO_CONTENT
      }
      xml {
        render status: NO_CONTENT
      }
      '*' {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'IdUser.label', default: 'IdUser'), userInstance.id])
        redirect action:"index"
      }
    }
  }

  protected void notFoundUser() {
    request.withFormat {
      json {
        render status: NOT_FOUND
      }
      xml {
        render status: NOT_FOUND
      }
      '*' {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'adminApp.label', default: 'IdUser'), params.id])
        redirect action: "index"
      }
    }
  }
}
