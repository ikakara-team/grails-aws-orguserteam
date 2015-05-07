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

import ikakara.orguserteam.dao.dynamo.IdFolder

//import grails.plugin.springsecurity.annotation.Secured

//@Secured(['ROLE_ADMIN'])
class SysFolderController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def orgUserTeamService

  //////////////////////////////////////////////////////////////////////////////
  // IdFolder
  //////////////////////////////////////////////////////////////////////////////

  def index(Integer max) {

    params.max = Math.min(max ?: 10, 100)
    //List results = new IdFolder([app_id: params.long('id')]).findByApp()
    List results = new IdFolder().queryByType()

    int count = results ? results.size() : 0
    //render view: 'index', model:[appList: results, appCount: count]

    def json = results as JSON
    //log.debug ">>>>>>>>>>>>>>>>>>>>>>>>>$json"

    respond results, model: [appList: results, appCount: count]
  }

  def show(IdFolder folderInstance) {

    if(params.id) {
      folderInstance.id = params.id
    }

    folderInstance.load()
    respond folderInstance, model: [folderInstance: folderInstance]
  }

  def create() {
    render view: 'create', model:[folderInstance: new IdFolder(params)]
  }

  def save(IdFolder folderInstance) {
    if (!folderInstance) {
      notFoundFolder()
      return
    }

    if (folderInstance.hasErrors()) {
      respond folderInstance.errors, view:'create'
      return
    }

    folderInstance.withCreatedUpdated()

    boolean created = folderInstance.create()
    if(!created) {
      flash.message = "Failed to create: $folderInstance.id"
      render view: 'create', model:[folderInstance: folderInstance]
      return
    }

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: 'adminApp.label', default: 'IdFolder'), folderInstance.id])
        redirect action: 'show', id: folderInstance.id
      }
      '*' { respond folderInstance, [status: CREATED] }
    }
  }

  def edit(IdFolder folderInstance) {
    if(!params.id) {
      response.sendError(404)
      return
    }

    folderInstance.id = params.id
    folderInstance.load()

    render view: 'edit', model:[folderInstance: folderInstance]
  }

  def update(IdFolder folderInstance) {
    if (!folderInstance) {
      notFoundFolder()
      return
    }

    log.debug "update>>>>>>>>>>>>>>>>$folderInstance.id"

    if (folderInstance.hasErrors()) {
      respond folderInstance.errors, view:'edit'
      return
    }

    folderInstance.withUpdated()
    folderInstance.save()

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'IdFolder.label', default: 'IdFolder'), folderInstance.id])
        redirect action: 'show', id: folderInstance.id
      }
      '*'{ respond folderInstance, [status: OK] }
    }
  }

  def delete(IdFolder folderInstance) {
    if (!folderInstance) {
      notFoundFolder()
      return
    }

    if(params.id) {
      folderInstance.id = params.id
    }

    folderInstance.delete()

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'IdFolder.label', default: 'IdFolder'), folderInstance.id])
        redirect action:"index"
      }
      '*'{ render status: NO_CONTENT }
    }
  }

  protected void notFoundFolder() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'adminApp.label', default: 'IdFolder'), params.id])
        redirect action: "index"
      }
      '*'{ render status: NOT_FOUND }
    }
  }
}
