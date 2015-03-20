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

import ikakara.orguserteam.dao.dynamo.IdTeam

//import grails.plugin.springsecurity.annotation.Secured

//@Secured(['ROLE_ADMIN'])
class SysTeamController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def orgUserTeamService

  //////////////////////////////////////////////////////////////////////////////
  // IdTeam
  //////////////////////////////////////////////////////////////////////////////

  def index(Integer max) {

    params.max = Math.min(max ?: 10, 100)
    //List results = new IdTeam([app_id: params.long('id')]).findByApp()
    List results = new IdTeam().queryByType()

    int count = results ? results.size() : 0
    //render view: 'index', model:[appList: results, appCount: count]

    def json = results as JSON
    //log.debug ">>>>>>>>>>>>>>>>>>>>>>>>>$json"

    respond results, model: [appList: results, appCount: count]
  }

  def show(IdTeam teamInstance) {

    if(params.id) {
      teamInstance.id = params.id
    }

    teamInstance.load()
    respond teamInstance, model: [teamInstance: teamInstance]
  }

  def create() {
    render view: 'create', model:[teamInstance: new IdTeam(params)]
  }

  def save(IdTeam teamInstance) {
    if (!teamInstance) {
      notFoundTeam()
      return
    }

    if (teamInstance.hasErrors()) {
      respond teamInstance.errors, view:'create'
      return
    }

    teamInstance.withCreatedUpdated()

    boolean created = teamInstance.create()
    if(!created) {
      flash.message = "Failed to create: $teamInstance.id"
      render view: 'create', model:[teamInstance: teamInstance]
      return
    }

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: 'adminApp.label', default: 'IdTeam'), teamInstance.id])
        redirect action: 'show', id: teamInstance.id
      }
      '*' { respond teamInstance, [status: CREATED] }
    }
  }

  def edit(IdTeam teamInstance) {
    if(!params.id) {
      response.sendError(404)
      return
    }

    teamInstance.id = params.id
    teamInstance.load()

    render view: 'edit', model:[teamInstance: teamInstance]
  }

  def update(IdTeam teamInstance) {
    if (!teamInstance) {
      notFoundTeam()
      return
    }

    log.debug "update>>>>>>>>>>>>>>>>$teamInstance.id"

    if (teamInstance.hasErrors()) {
      respond teamInstance.errors, view:'edit'
      return
    }

    teamInstance.updatedDate = new Date()
    teamInstance.save()

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'IdTeam.label', default: 'IdTeam'), teamInstance.id])
        redirect action: 'show', id: teamInstance.id
      }
      '*'{ respond teamInstance, [status: OK] }
    }
  }

  def delete(IdTeam teamInstance) {
    if (!teamInstance) {
      notFoundTeam()
      return
    }

    if(params.id) {
      teamInstance.id = params.id
    }

    teamInstance.delete()

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'IdTeam.label', default: 'IdTeam'), teamInstance.id])
        redirect action:"index"
      }
      '*'{ render status: NO_CONTENT }
    }
  }

  protected void notFoundTeam() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'adminApp.label', default: 'IdTeam'), params.id])
        redirect action: "index"
      }
      '*'{ render status: NOT_FOUND }
    }
  }
}
