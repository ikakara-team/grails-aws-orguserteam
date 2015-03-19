/* Copyright 2014-2015 Allen Arakaki.  All Rights Reserved.
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

  static allowedMethods = [
    save: "POST", update: "PUT", delete: "DELETE"]

  def orgUserTeamService;

  //////////////////////////////////////////////////////////////////////////////
  // IdUser
  //////////////////////////////////////////////////////////////////////////////

  def index(Integer max) {
    println "index: ${params}"

    params.max = Math.min(max ?: 10, 100)
    List results = new IdUser().queryByType();

    int count = results != null ? results.size() : 0;

    def json = results as JSON
    //println ">>>>>>>>>>>>>>>>>>>>>>>>>" + json

    respond results, model: [userList: results, userCount: count]
  }

  def show(IdUser userInstance) {
    println "show: ${params} instance: ${userInstance.id}"

    if(params.id) {
      userInstance.setId(params.id);
    }

    userInstance.load();

    def listOrg = orgUserTeamService.listOrg(userInstance)
    userInstance.orgListAdd(listOrg)

    def listTeam = orgUserTeamService.listTeam(userInstance)
    userInstance.teamListAdd(listTeam)

    respond userInstance, model: [userInstance: userInstance]
  }

  def create() {
    def config = new IdUser(params);

    render view: 'create', model:[userInstance: config]
  }

  def save(IdUser userInstance) {
    if (userInstance == null) {
      notFoundUser()
      return
    }

    // fix the incoming parameters
    def newalias = params.aliasId

    userInstance = orgUserTeamService.createUser(userInstance, userInstance.name, userInstance.initials, userInstance.description, newalias)
    if(!userInstance) {
      flash.message = "Failed to create: ${userInstance.getId()}"
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
        flash.message = message(code: 'default.created.message', args: [message(code: 'adminApp.label', default: 'IdUser'), userInstance.getId()])
        redirect action: 'show', id: userInstance.getId()
      }
    }
  }

  def edit(IdUser userInstance) {
    if(params.id) {
      userInstance.setId(params.id);
    } else {
      response.sendError(404);
      return;
    }

    userInstance.load();

    render view: 'edit', model:[userInstance: userInstance]
  }

  def update(IdUser userInstance) {
    if (userInstance == null) {
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
        flash.message = message(code: 'default.updated.message', args: [message(code: 'IdUser.label', default: 'IdUser'), userInstance.getId()])
        redirect action: 'show', id: userInstance.getId()
      }
    }
  }

  def delete(IdUser userInstance) {
    if (userInstance == null) {
      notFoundUser()
      return
    }

    if(params.id) {
      userInstance.setId(params.id);
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
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'IdUser.label', default: 'IdUser'), userInstance.getId()])
        redirect action:"index", method:"GET"
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
        redirect action: "index", method: "GET"
      }
    }
  }

}
