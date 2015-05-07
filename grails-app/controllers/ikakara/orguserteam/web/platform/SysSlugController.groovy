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

import ikakara.orguserteam.dao.dynamo.IdSlug

//import grails.plugin.springsecurity.annotation.Secured

//@Secured(['ROLE_ADMIN'])
class SysSlugController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def orgUserTeamService

  //////////////////////////////////////////////////////////////////////////////
  // IdSlug
  //////////////////////////////////////////////////////////////////////////////

  def index(Integer max) {

    params.max = Math.min(max ?: 10, 100)
    //List results = new IdSlug([app_id: params.long('id')]).findByApp()
    List results = new IdSlug().queryByType()

    int count = results ? results.size() : 0
    //render view: 'index', model:[slugList: results, slugCount: count]

    def json = results as JSON
    //log.debug ">>>>>>>>>>>>>>>>>>>>>>>>>$json"

    respond results, model: [slugList: results, slugCount: count]
  }

  def show(IdSlug slugInstance) {

    if(params.id) {
      slugInstance.id = params.id
    }

    slugInstance.load()
    respond slugInstance, model: [slugInstance: slugInstance]
  }

  def create() {
    render view: 'create', model:[slugInstance: new IdSlug(params)]
  }

  def save(IdSlug slugInstance) {
    if (!slugInstance) {
      notFoundSlug()
      return
    }

    if (slugInstance.hasErrors()) {
      respond slugInstance.errors, view:'create'
      return
    }

    slugInstance.withCreatedUpdated()

    boolean created = slugInstance.create()
    if(!created) {
      flash.message = "Failed to create: $slugInstance.id"
      render view: 'create', model:[slugInstance: slugInstance]
      return
    }

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: 'adminApp.label', default: 'IdSlug'), slugInstance.id])
        redirect action: 'show', id: slugInstance.id
      }
      '*' { respond slugInstance, [status: CREATED] }
    }
  }

  def edit(IdSlug slugInstance) {
    if(!params.id) {
      response.sendError(404)
      return
    }

    slugInstance.id = params.id
    slugInstance.load()

    render view: 'edit', model:[slugInstance: slugInstance]
  }

  def update(IdSlug slugInstance) {
    if (!slugInstance) {
      notFoundSlug()
      return
    }

    log.debug "update>>>>>>>>>>>>>>>>$slugInstance.id"

    if (slugInstance.hasErrors()) {
      respond slugInstance.errors, view:'edit'
      return
    }

    slugInstance.withUpdated()
    slugInstance.save()

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'IdSlug.label', default: 'IdSlug'), slugInstance.id])
        redirect action: 'show', id: slugInstance.id
      }
      '*'{ respond slugInstance, [status: OK] }
    }
  }

  def delete(IdSlug slugInstance) {
    if (!slugInstance) {
      notFoundSlug()
      return
    }

    if(params.id) {
      slugInstance.id = params.id
    }

    slugInstance.delete()

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'IdSlug.label', default: 'IdSlug'), slugInstance.id])
        redirect action:"index"
      }
      '*'{ render status: NO_CONTENT }
    }
  }

  protected void notFoundSlug() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'adminApp.label', default: 'IdSlug'), params.id])
        redirect action: "index"
      }
      '*'{ render status: NOT_FOUND }
    }
  }
}
