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

import ikakara.orguserteam.dao.dynamo.IdOrg

//import grails.plugin.springsecurity.annotation.Secured

//@Secured(['ROLE_ADMIN'])
class SysOrgController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def orgUserTeamService

  //////////////////////////////////////////////////////////////////////////////
  // IdOrg
  //////////////////////////////////////////////////////////////////////////////

  def index(Integer max) {

    params.max = Math.min(max ?: 10, 100)
    List results = new IdOrg().queryByType()

    int count = results ? results.size() : 0

    def json = results as JSON
    //log.debug ">>>>>>>>>>>>>>>>>>>>>>>>>$json"

    respond results, model: [orgList: results, orgCount: count]
  }

  def show(IdOrg orgInstance) {

    if(params.id) {
      orgInstance.id = params.id
    }

    orgInstance.load()

    orgInstance.userListAdd(orgUserTeamService.listUser(orgInstance))

    orgInstance.folderListAdd(orgUserTeamService.listFolder(orgInstance))

    respond orgInstance, model: [orgInstance: orgInstance]
  }

  def create() {
    render view: 'create', model:[orgInstance: new IdOrg(params)]
  }

  def save(IdOrg orgInstance) {
    if (!orgInstance) {
      notFoundOrg()
      return
    }

    orgInstance = orgUserTeamService.createOrg(null, params.name, params.description)

    if(!orgInstance) {
      flash.message = "Failed to create: $orgInstance.id"
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
        flash.message = message(code: 'default.created.message', args: [message(code: 'adminApp.label', default: 'IdOrg'), orgInstance.id])
        redirect action: 'show', id: orgInstance.id
      }
    }
  }

  def edit(IdOrg orgInstance) {
    if(!params.id) {
      response.sendError(404)
      return
    }

    orgInstance.id = params.id
    orgInstance.load()

    render view: 'edit', model:[orgInstance: orgInstance]
  }

  def update(IdOrg orgInstance) {
    if (!orgInstance) {
      notFoundOrg()
      return
    }

    // fix the incoming parameters
    def newalias = params.aliasId
    //orgInstance.aliasId = params.curalias

    orgInstance = orgUserTeamService.updateUser(orgInstance, orgInstance.name, orgInstance.initials, orgInstance.description, newalias)

    request.withFormat {
      json {
        render orgInstance as JSON, [status: OK]
      }
      xml {
        render orgInstance as XML, [status: OK]
      }
      '*' {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'IdOrg.label', default: 'IdOrg'), orgInstance.id])
        redirect action: 'show', id: orgInstance.id
      }
    }
  }

  def delete(IdOrg orgInstance) {
    if (!orgInstance) {
      notFoundOrg()
      return
    }

    if(params.id) {
      orgInstance.id = params.id
    }

    orgInstance.delete()

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'IdOrg.label', default: 'IdOrg'), orgInstance.id])
        redirect action:"index"
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
        redirect action: "index"
      }
    }
  }
}
