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

import ikakara.orguserteam.dao.dynamo.IdSlug

//import grails.plugin.springsecurity.annotation.Secured

//@Secured(['ROLE_ADMIN'])
class SysSlugController {

  static allowedMethods = [
    save: "POST", update: "PUT", delete: "DELETE"]

  def orgUserTeamService;

  //////////////////////////////////////////////////////////////////////////////
  // IdSlug
  //////////////////////////////////////////////////////////////////////////////

  def index(Integer max) {
    println "index: ${params}"

    params.max = Math.min(max ?: 10, 100)
    //List results = new IdSlug([app_id: params.long('id')]).findByApp();
    List results = new IdSlug().queryByType();

    int count = results != null ? results.size() : 0;
    //render view: 'index', model:[slugList: results, slugCount: count]

    def json = results as JSON
    //println ">>>>>>>>>>>>>>>>>>>>>>>>>" + json

    respond results, model: [slugList: results, slugCount: count]
  }

  def show(IdSlug slugInstance) {
    println "show: ${params} instance: ${slugInstance.id}"

    if(params.id) {
      slugInstance.setId(params.id);
    }

    slugInstance.load();
    respond slugInstance, model: [slugInstance: slugInstance]
  }

  def create() {
    def config = new IdSlug(params);

    render view: 'create', model:[slugInstance: config]
  }

  def save(IdSlug slugInstance) {
    if (slugInstance == null) {
      notFoundSlug()
      return
    }

    if (slugInstance.hasErrors()) {
      respond slugInstance.errors, view:'create'
      return
    }

    slugInstance.withCreatedUpdated()

    boolean bcreated = slugInstance.create()
    if(!bcreated) {
      flash.message = "Failed to create: ${slugInstance.getId()}"
      render view: 'create', model:[slugInstance: slugInstance]
      return
    }

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.created.message', args: [message(code: 'adminApp.label', default: 'IdSlug'), slugInstance.getId()])
        redirect action: 'show', id: slugInstance.getId()
      }
      '*' { respond slugInstance, [status: CREATED] }
    }
  }

  def edit(IdSlug slugInstance) {
    if(params.id) {
      slugInstance.setId(params.id);
    } else {
      response.sendError(404);
      return;
    }

    slugInstance.load();

    render view: 'edit', model:[slugInstance: slugInstance]
  }

  def update(IdSlug slugInstance) {
    if (slugInstance == null) {
      notFoundSlug()
      return
    }

    println "update>>>>>>>>>>>>>>>>" + slugInstance.id

    if (slugInstance.hasErrors()) {
      respond slugInstance.errors, view:'edit'
      return
    }

    slugInstance.setUpdatedDate(new Date())
    slugInstance.save()

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'IdSlug.label', default: 'IdSlug'), slugInstance.getId()])
        redirect action: 'show', id: slugInstance.getId()
      }
      '*'{ respond slugInstance, [status: OK] }
    }
  }

  def delete(IdSlug slugInstance) {
    if (slugInstance == null) {
      notFoundSlug()
      return
    }

    if(params.id) {
      slugInstance.setId(params.id);
    }

    slugInstance.delete()

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'IdSlug.label', default: 'IdSlug'), slugInstance.getId()])
        redirect action:"index", method:"GET"
      }
      '*'{ render status: NO_CONTENT }
    }
  }

  protected void notFoundSlug() {
    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'adminApp.label', default: 'IdSlug'), params.id])
        redirect action: "index", method: "GET"
      }
      '*'{ render status: NOT_FOUND }
    }
  }

}
