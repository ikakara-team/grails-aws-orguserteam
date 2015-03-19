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

import ikakara.orguserteam.dao.dynamo.IdTeam

//import grails.plugin.springsecurity.annotation.Secured

//@Secured(['ROLE_ADMIN'])
class SysTeamController {

  static allowedMethods = [
    save: "POST", update: "PUT", delete: "DELETE"]

  def orgUserTeamService;

  //////////////////////////////////////////////////////////////////////////////
  // IdTeam
  //////////////////////////////////////////////////////////////////////////////

  def index(Integer max) {
    println "index: ${params}"

    params.max = Math.min(max ?: 10, 100)
    //List results = new IdTeam([app_id: params.long('id')]).findByApp();
    List results = new IdTeam().queryByType();

    int count = results != null ? results.size() : 0;
    //render view: 'index', model:[appList: results, appCount: count]

    def json = results as JSON
    //println ">>>>>>>>>>>>>>>>>>>>>>>>>" + json

    respond results, model: [appList: results, appCount: count]
  }

  def show(IdTeam teamInstance) {
    println "show: ${params} instance: ${teamInstance.id}"

    if(params.id) {
      teamInstance.setId(params.id);
    }

    teamInstance.load();
    respond teamInstance, model: [teamInstance: teamInstance]
  }

  def create() {
    def config = new IdTeam(params);

    render view: 'create', model:[teamInstance: config]
  }

  def save(IdTeam teamInstance) {
    if (teamInstance == null) {
      notFoundTeam()
      return
    }

    if (teamInstance.hasErrors()) {
      respond teamInstance.errors, view:'create'
      return
    }

    teamInstance.withCreatedUpdated()

    boolean bcreated = teamInstance.create()
    if(!bcreated) {
      flash.message = "Failed to create: ${teamInstance.getId()}"
      render view: 'create', model:[teamInstance: teamInstance]
      return
    }

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: 'adminApp.label', default: 'IdTeam'), teamInstance.getId()])
        redirect action: 'show', id: teamInstance.getId()
      }
      '*' { respond teamInstance, [status: CREATED] }
    }
  }

  def edit(IdTeam teamInstance) {
    if(params.id) {
      teamInstance.setId(params.id);
    } else {
      response.sendError(404);
      return;
    }

    teamInstance.load();

    render view: 'edit', model:[teamInstance: teamInstance]
  }

  def update(IdTeam teamInstance) {
    if (teamInstance == null) {
      notFoundTeam()
      return
    }

    println "update>>>>>>>>>>>>>>>>" + teamInstance.id

    if (teamInstance.hasErrors()) {
      respond teamInstance.errors, view:'edit'
      return
    }

    teamInstance.setUpdatedDate(new Date())
    teamInstance.save()

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'IdTeam.label', default: 'IdTeam'), teamInstance.getId()])
        redirect action: 'show', id: teamInstance.getId()
      }
      '*'{ respond teamInstance, [status: OK] }
    }
  }

  def delete(IdTeam teamInstance) {
    if (teamInstance == null) {
      notFoundTeam()
      return
    }

    if(params.id) {
      teamInstance.setId(params.id);
    }

    teamInstance.delete()

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'IdTeam.label', default: 'IdTeam'), teamInstance.getId()])
        redirect action:"index", method:"GET"
      }
      '*'{ render status: NO_CONTENT }
    }
  }

  protected void notFoundTeam() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'adminApp.label', default: 'IdTeam'), params.id])
        redirect action: "index", method: "GET"
      }
      '*'{ render status: NOT_FOUND }
    }
  }

}
