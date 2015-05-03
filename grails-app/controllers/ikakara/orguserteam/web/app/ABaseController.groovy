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
package ikakara.orguserteam.web.app

import groovy.transform.CompileStatic

import ikakara.orguserteam.dao.dynamo.IdUser

@CompileStatic
abstract class ABaseController implements IAccessController {
  static final String ORG_KEY       = "org"
  static final String USER_KEY      = "user"
  static final String USEREMAIL_KEY = "email"
  static final String FOLDER_KEY    = "folder"
  static final String MEMBER_KEY    = "member"
  static final String INVITED_KEY   = "invited"

  def orgUserTeamService

  def setAttributeUserEmailAndInvited() {
    def useremail = getUserEmail()

    def email = ((OrgUserTeamService)orgUserTeamService).email(useremail, false)
    if(email) {
      request.setAttribute(USEREMAIL_KEY, email)
      def listGroup = ((OrgUserTeamService)orgUserTeamService).listGroup(email)
      if(listGroup) {
        request.setAttribute(INVITED_KEY, listGroup)
      }
    }
  }

  def setAttributeMemberAll(IdUser user) {
    def userGroup = ((OrgUserTeamService)orgUserTeamService).listGroup(user)
    if(userGroup) {
      request.setAttribute(MEMBER_KEY, userGroup)
    }
  }

  def setAttributeMemberFolder(IdUser user) {
    def userGroup = ((OrgUserTeamService)orgUserTeamService).listFolder(user)
    if(userGroup) {
      request.setAttribute(MEMBER_KEY, userGroup)
    }
  }

  protected String getChanged(String newinput, String oldinput) {
    if(newinput == oldinput) {
      return null
    }
    return newinput
  }

  protected respondError(errorCode, errorMsg='', view="error${errorCode}") {
    def model = [ success: false, error: [ code: errorCode, text: errorMsg ] ]
    // JSON/XML/HTML appropriate response with detail
    respond model as Object, [ model: model, status: errorCode, view: view ]
  }

}
