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

import ikakara.orguserteam.dao.dynamo.IdEmail

//import grails.plugin.springsecurity.annotation.Secured

//@Secured(['ROLE_ADMIN'])
class SysEmailController {

  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def orgUserTeamService

  //////////////////////////////////////////////////////////////////////////////
  // IdEmail
  //////////////////////////////////////////////////////////////////////////////

  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    List results = new IdEmail().queryByType()

    int count = results ? results.size() : 0

    respond results, model: [emailList: results, emailCount: count]
  }

  def show(IdEmail emailInstance) {
    emailInstance.urlDecodeId((String)params.id)

    emailInstance.load()
    respond emailInstance, model: [emailInstance: emailInstance]
  }

  def create() {
    render view: 'create', model:[emailInstance: new IdEmail(params)]
  }

  def save(IdEmail emailInstance) {
    if (!emailInstance) {
      notFoundEmail()
      return
    }

    emailInstance = orgUserTeamService.createEmail(emailInstance.id)

    if(!emailInstance) {
      flash.message = "Failed to create: ${params.id}"
      render view: 'create', model:[emailInstance: emailInstance]
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
        flash.message = message(code: 'default.created.message', args: [message(code: 'adminApp.label', default: 'IdEmail'), emailInstance?.id])
        redirect action: 'show', id: emailInstance?.urlEncodedId()
      }
    }
  }

  def edit(IdEmail emailInstance) {
    if(!params.id) {
      response.sendError(404)
      return
    }

    println "${emailInstance} ${params}"

    emailInstance.urlDecodeId((String)params.id)
    emailInstance.load()

    render view: 'edit', model:[emailInstance: emailInstance]
  }

  def update(IdEmail emailInstance) {
    if (emailInstance == null) {
      notFoundEmail()
      return
    }

    if(params.id) {
      emailInstance.urlDecodeId((String)params.id)
    }



    if (emailInstance.hasErrors()) {
      respond emailInstance.errors, view:'edit'
      return
    }

    emailInstance.withUpdated()

    println "update>>>>>>>>>>>>>>>>" + emailInstance.writeOverCreated

    emailInstance.save()

    request.withFormat {
      json {
        render emailInstance as JSON, [status: OK]
      }
      xml {
        render emailInstance as XML, [status: OK]
      }
      '*' {
        flash.message = message(code: 'default.updated.message', args: [message(code: 'IdEmail.label', default: 'IdEmail'), emailInstance.id])
        redirect action: 'show', id: emailInstance.urlEncodedId()
      }
    }
  }

  def delete(IdEmail emailInstance) {
    if (!emailInstance) {
      notFoundEmail()
      return
    }

    if(params.id) {
      emailInstance.urlDecodeId((String)params.id)
    }

    emailInstance.delete()

    request.withFormat {
      form multipartForm {
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'IdEmail.label', default: 'IdEmail'), emailInstance.id])
        redirect action:"index"
      }
      '*'{ render status: NO_CONTENT }
    }
  }

  protected void notFoundEmail() {
    request.withFormat {
      json {
        render status: NOT_FOUND
      }
      xml {
        render status: NOT_FOUND
      }
      '*' { // form multipartForm
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'adminApp.label', default: 'IdEmail'), params.id])
        redirect action: "index"
      }
    }
  }

}
