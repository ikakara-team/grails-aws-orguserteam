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

import ikakara.orguserteam.dao.dynamo.IdOrg

//import grails.plugin.springsecurity.annotation.Secured

//@Secured(['ROLE_ADMIN'])
class SysOrgController {

  static allowedMethods = [
    save: "POST", update: "PUT", delete: "DELETE"]

  def orgUserTeamService;

  //////////////////////////////////////////////////////////////////////////////
  // IdOrg
  //////////////////////////////////////////////////////////////////////////////

  def index(Integer max) {
    println "index: ${params}"

    params.max = Math.min(max ?: 10, 100)
    List results = new IdOrg().queryByType();

    int count = results != null ? results.size() : 0;

    def json = results as JSON
    //println ">>>>>>>>>>>>>>>>>>>>>>>>>" + json

    respond results, model: [orgList: results, orgCount: count]
  }

  def show(IdOrg orgInstance) {
    println "show: ${params} instance: ${orgInstance.id}"

    if(params.id) {
      orgInstance.setId(params.id);
    }

    orgInstance.load();

    def listUser = orgUserTeamService.listUser(orgInstance)
    userInstance.userListAdd(listUser)

    def listTeam = orgUserTeamService.listTeam(orgInstance)
    userInstance.teamListAdd(listTeam)

    respond orgInstance, model: [orgInstance: orgInstance]
  }

  def create() {
    def config = new IdOrg(params);

    render view: 'create', model:[orgInstance: config]
  }

  def save(IdOrg orgInstance) {
    if (orgInstance == null) {
      notFoundOrg()
      return
    }

    orgInstance = orgUserTeamService.createOrg(null, params.name, params.description)

    if(!orgInstance) {
      flash.message = "Failed to create: ${orgInstance.getId()}"
      render view: 'create', model:[orgInstance: orgInstance]
      return
    }

    request.withFormat {
      json {
        render orgInstance as JSON, [status: CREATED]
      }
      xml {
        render orgInstance as XML, [status: CREATED]
      }
      '*' {
        flash.message = message(code: 'default.created.message', args: [message(code: 'adminApp.label', default: 'IdOrg'), orgInstance.getId()])
        redirect action: 'show', id: orgInstance.getId()
      }
    }
  }

  def edit(IdOrg orgInstance) {
    if(params.id) {
      orgInstance.setId(params.id);
    } else {
      response.sendError(404);
      return;
    }

    orgInstance.load();

    render view: 'edit', model:[orgInstance: orgInstance]
  }

  def update(IdOrg orgInstance) {
    if (orgInstance == null) {
      notFoundOrg()
      return
    }

    // fix the incoming parameters
    def newalias = params.aliasId
    //userInstance.aliasId = params.curalias

    orgInstance = orgUserTeamService.updateUser(userInstance, userInstance.name, userInstance.initials, userInstance.description, newalias)

    request.withFormat {
      json {
        render orgInstance as JSON, [status: OK]
      }
      xml {
        render orgInstance as XML, [status: OK]
      }
      '*' {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'IdOrg.label', default: 'IdOrg'), orgInstance.getId()])
        redirect action: 'show', id: orgInstance.getId()
      }
    }
  }

  def delete(IdOrg orgInstance) {
    if (orgInstance == null) {
      notFoundOrg()
      return
    }

    if(params.id) {
      orgInstance.setId(params.id);
    }

    orgInstance.delete()

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'IdOrg.label', default: 'IdOrg'), orgInstance.getId()])
        redirect action:"index", method:"GET"
      }
      '*'{ render status: NO_CONTENT }
    }
  }

  protected void notFoundOrg() {
    request.withFormat {
      json {
        render status: NOT_FOUND
      }
      xml {
        render status: NOT_FOUND
      }
      '*' { // form multipartForm
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'adminApp.label', default: 'IdOrg'), params.id])
        redirect action: "index", method: "GET"
      }
    }
  }

}
