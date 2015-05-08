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

import ikakara.orguserteam.dao.dynamo.AIdBase
import ikakara.orguserteam.dao.dynamo.IdSlug
import ikakara.orguserteam.dao.dynamo.IdFolder
import ikakara.orguserteam.dao.dynamo.IdOrg
import ikakara.orguserteam.dao.dynamo.IdOrgFolder
import ikakara.orguserteam.dao.dynamo.IdUser
import ikakara.orguserteam.dao.dynamo.AIdUserGroup
import ikakara.orguserteam.dao.dynamo.IdUserOrg
import ikakara.orguserteam.dao.dynamo.IdUserFolder
import ikakara.orguserteam.dao.dynamo.IdEmail
import ikakara.orguserteam.dao.dynamo.AIdEmailGroup
import ikakara.orguserteam.dao.dynamo.IdEmailFolder
import ikakara.orguserteam.dao.dynamo.IdEmailOrg

@CompileStatic
class OrgUserTeamService {
  static transactional = false

  AIdBase findIdObjBySlugId(String slugId) {
    def list = new IdSlug().queryByAlias(slugId)
    if(list?.size() == 1) {
      return list[0]
    }

    log.error("findIdObjBySlugId - invalid result for $slugId")
  }

  boolean exist(AIdBase id) {
    return id?.load()
  }

  AIdEmailGroup exist(IdEmail email, AIdBase group) {
    if(group instanceof IdOrg) {
      return exist(email, (IdOrg)group)
    } else if(group instanceof IdFolder) {
      return exist(email, (IdFolder)group)
    }
    return null
  }

  IdEmailOrg exist(IdEmail email, IdOrg org) {
    def emailorg = (IdEmailOrg)new IdEmailOrg().withMember(email).withGroup(org)
    if(!emailorg.load()) {
      return null
    }
    return emailorg
  }

  IdEmailFolder exist(IdEmail email, IdFolder folder) {
    def emailfolder = (IdEmailFolder)new IdEmailFolder().withMember(email).withGroup(folder)
    if(!emailfolder.load()) {
      return null
    }
    return emailfolder
  }

  /////////////////////////////////////////////////////////////////////////////
  // User
  /////////////////////////////////////////////////////////////////////////////
  IdUser user(String userId, instance=true) {
    def user = new IdUser(id: userId)
    def load = user.load()
    if(!load) {
      // doesn't exist
      log.debug("User Not Found: $userId")
      if(!instance) {
        return null
      }
    }

    return user
  }

  List<IdUserOrg> listUser(IdOrg org) {
    List list = new IdUserOrg().withGroup(org).queryByGroupAndType()
    return list
  }

  List<IdUserFolder> listUser(IdFolder folder) {
    List list = new IdUserFolder().withGroup(folder).queryByGroupAndType()
    return list
  }

  IdUser createUser(IdUser user, String name, String initials, String desc, String shortName) {
    // create org
    user.slugify(shortName)
    .withCreatedUpdated()

    user.name = name
    user.initials = initials
    user.description = desc

    def create = user.create()
    if(!create) {
      // failed
      return null
    }

    // create slug
    def slug = new IdSlug(id: user.aliasId)
    .withAlias(user)
    .withCreatedUpdated()

    create = slug.create()
    if(!create) {
      // failed
      user.delete()
      return null
    }

    return user
  }

  IdUser updateUser(IdUser user, String name, String initials, String desc, String shortName) {
    def load = user.load()
    if(!load) {
      // not found, create a user
      user = createUser(user, name, initials, desc, shortName)
      return user
    }

    def oldslug
    def newslug

    if(shortName) {
      if(user.aliasId != shortName) {
        // create new slug
        newslug = (IdSlug)new IdSlug().withSlugId(shortName)
        .withAlias(user)
        .withCreatedUpdated()

        def create = newslug.create()
        if(!create) {
          // failed
          return null
        }

        oldslug = user.alias
        user.withAlias(newslug)
      }
    }

    if(name) {
      user.name  = name
    }

    user.description = desc
    user.initials = initials

    user.withUpdated()
    def save = user.save()
    if(save) {
      // cleanup old slug
      if(oldslug) {
        oldslug.delete()
      }
    } else {
      // cleanup new slug
      if(newslug) {
        newslug.delete()
      }
    }

    return user
  }

  // return true if deleted, returns false if not found
  boolean deleteUser(IdUser user) {
    def load = user.load()
    if(!load) {
      // not found
      return false
    }

    // delete all org references
    def list = listOrg(user)
    list.each{
      it.delete() // delete only connection
    }

    // delete all folder references
    list = listFolder(user)
    list.each{
      it.delete() // delete only connection
    }

    // delete slug
    new IdSlug(id: user.aliasId).delete()
    // delete user
    user.delete()

    return true
  }

  /////////////////////////////////////////////////////////////////////////////
  // Org
  /////////////////////////////////////////////////////////////////////////////
  IdOrg org(String orgId, instance=true) {
    IdOrg org = (IdOrg)new IdOrg().withId(orgId)
    def load = org.load()
    if(!load) {
      // doesn't exist
      log.debug("Org Not Found: $orgId")
      if(!instance) {
        return null
      }
    }

    return org
  }

  boolean isOrgVisible(IdOrg org, IdUser user) {
    // org is visible to user when ...
    if(org.ownerEquals(user)) { // doesn't require a network call
      return true
    }

    // user is member of org
    return org.hasMember(user)
  }

  // NOSQL compromise for 1 to many, using many to many table
  List<IdOrgFolder> listOrg(IdFolder folder) {
    List list = new IdOrgFolder().withGroup(folder).queryByGroupAndType()
    return list
  }

  List<IdUserOrg> listOrg(IdUser user) {
    List list = new IdUserOrg().withMember(user).queryByMemberAndType()
    return list
  }

  List<IdEmailOrg> listOrg(IdEmail email) {
    List list = new IdEmailOrg().withMember(email).queryByMemberAndType()
    return list
  }

  IdOrg createOrg(IdUser user, String orgName, String orgDescription, IdOrg owner=null) {
    // create org
    IdOrg org = (IdOrg)new IdOrg(description: orgDescription)
    .initId()
    .slugify(orgName)
    .withCreatedUpdated()

    // org owner is either (another) org or user
    if(owner) {
      org.withOwner(owner)
    } else {
      org.withOwner(user)
    }

    def create = org.create()
    if(!create) {
      // failed
      return null
    }

    // create slug
    def slug = (IdSlug)new IdSlug(id: org.aliasId)
    .withAlias(org)
    .withCreatedUpdated()

    create = slug.create()
    if(!create) {
      // failed
      org.delete()
      return null
    }

    if(user) {
      // add user to org
      IdUserOrg userorg = (IdUserOrg)new IdUserOrg()
      .withMemberRoles(IdUserOrg.MEMBERROLE_OWNER)
      .withMember(user)
      .withGroup(org)
      .withCreatedUpdated()

      create = userorg.create()
      if(!create) {
        // failed
        org.delete()
        slug.delete()
        return null
      }
    }

    return org
  }

  String updateOrg(IdOrg org, String name, String desc, String web_url, String shortName) {
    def load = org.load()
    if(!load) {
      // not found
      return
    }

    def oldslug
    def newslug

    if(shortName) {
      if(org.aliasId != shortName) {
        // create new slug
        newslug = (IdSlug)new IdSlug().withSlugId(shortName)
        .withAlias(org)
        .withCreatedUpdated()

        def create = newslug.create()
        if(!create) {
          // failed
          return null
        }

        oldslug = org.alias
        org.withAlias(newslug)
      }
    }

    if(name) {
      org.name  = name
    }

    org.description = desc
    org.webUrl = web_url

    org.withUpdated()
    def save = org.save()
    if(save) {
      // cleanup old slug
      if(oldslug) {
        oldslug.delete()
      }
    } else {
      // cleanup new slug
      if(newslug) {
        newslug.delete()
      }
    }

    return org.aliasId
  }

  // return true if deleted, returns false if not found
  boolean deleteOrg(IdOrg org) {
    def load = org.load()
    if(!load) {
      // not found
      return false
    }

    // delete all folders and references
    def list = listFolder(org)
    list.each{
      IdFolder folder = (IdFolder)it.group
      deleteFolder(folder) // deletes both folder and folder-connections
    }

    // delete all user references
    list = listUser(org)
    list.each{
      it.delete() // delete only connection
    }

    // delete all email references
    list = listEmail(org)
    list.each{
      it.delete() // delete only connection
    }

    // delete slug
    new IdSlug(id: org.aliasId).delete()
    // delete org
    org.delete()

    return true
  }

  /////////////////////////////////////////////////////////////////////////////
  // Folder
  /////////////////////////////////////////////////////////////////////////////

  IdFolder folder(String folderId, instance=true) {
    IdFolder folder = (IdFolder)new IdFolder().withId(folderId)
    def load = folder.load()
    if(!load) {
      // doesn't exist
      log.debug("Folder Not Found: $folderId")
      if(!instance) {
        return null
      }
    }

    return folder
  }

  // WARNING: this does not check if user is an orgMember w/ a role of owner/admin
  boolean isFolderVisible(IdFolder folder, IdUser user, boolean orgMember) {
    // folder is visible to user when ...
    if(folder.ownerEquals(user)) { // doesn't require a network call
      return true
    }

    if(folder.orgVisible && orgMember) {
      // user is member of org and folder is visible to org
      return true
    }
    // user is member of folder
    return folder.hasMember(user)
  }

  // This checks for everything
  boolean isFolderVisible(IdFolder folder, IdUser user) {
    if(folder.ownerEquals(user)) { // doesn't require a network call
      return true
    }

    // folder is visible to user when ...
    if(folder.hasMember(user)) { // network call
      // user is member of folder
      return true
    }

    // check if org is folder owner
    if(!folder.isOwnerOrg()) { // doesn't require network call
      return false
    }

    IdOrg org = (IdOrg)folder.owner // a network call

    IdUserOrg orguser = org?.hasMember(user) // a network call
    if(orguser && (folder.orgVisible || haveOrgRole(orguser, IdUserOrg.FOLDER_VISIBLE))) {
      // user is member of org and (folder is visible to org or/and user has folder-visible role)
      return true
    }

    return false
  }

  boolean haveOrgRole(IdUserOrg orguser, Set orgRoles) {
    boolean rolevisibility = false

    if(orgRoles) {
      if(orguser?.memberRoles) {
        def res = orgRoles.intersect(orguser.memberRoles)
        if(res.size() > 0) {
          rolevisibility = true
        }
      }
    }

    return rolevisibility
  }

  boolean haveOrgRole(IdOrg org, IdUser user, Set orgRoles) {
    def orguser = org?.hasMember(user)
    return haveOrgRole(orguser, orgRoles)
  }

  List<IdOrgFolder> listFolderVisible(IdOrg org, IdUser user, Set orgRoles=null) {
    List listFolder = []

    if(org) { // nothing to do
      List list = new IdOrgFolder().withMember(org).queryByMemberAndType()
      if(list) { // nothing to do
        def orguser = org.hasMember(user)
        def orgMember = orguser ? true : false
        if(orgMember && haveOrgRole(orguser, orgRoles)) {
          listFolder = list
        } else {
          // iterate through list to see which are visible to user
          for(orgobj in list) {
            IdFolder folder = (IdFolder)orgobj.group
            if(isFolderVisible(folder, user, orgMember)) {
              listFolder << orgobj
            }
          }
        }
      }
    }

    return listFolder
  }

  List<IdOrgFolder> listFolder(IdOrg org) {
    List list = new IdOrgFolder().withMember(org).queryByMemberAndType()
    return list
  }

  List<IdUserFolder> listFolder(IdUser user) {
    List list = new IdUserFolder().withMember(user).queryByMemberAndType()
    return list
  }

  List<IdEmailFolder> listFolder(IdEmail email) {
    List list = new IdEmailFolder().withMember(email).queryByMemberAndType()
    return list
  }

  // this is ridculous ... SQL is so much better at relationships
  List<IdOrg> listFolderByOrg(IdUser user, String myOrgName) {
    Map mapApp = [:]
    List<IdOrg> listOrg = [new IdOrg(name: myOrgName)]

    // get all the user folders and orgs
    List list = new IdUserFolder().withMember(user).queryByMember()
    // we can optimize this ...
    for(userobj in list) {
      if(userobj instanceof IdUserFolder) {
        IdFolder folder = (IdFolder)userobj.group
        folder.load()
        mapApp[folder.id] = folder
      } else if(userobj instanceof IdUserOrg) {
        IdOrg org = (IdOrg)userobj.group
        org.load()
        listOrg << org
      } else {
        // unknown class
      }
    }

    int size = listOrg.size()
    for(int i = 1; i < size; i++) {
      def org = listOrg[i]

      // get all the folders of the orgs that the user belongs to
      // query by member and privacy
      List list_orgfolder = new IdOrgFolder().withMember(org).queryByMemberAndType()
      for(orgfolder in list_orgfolder) {
        IdFolder folder = (IdFolder)orgfolder.group

        if(mapApp.containsKey(folder.id)) {
          folder = mapApp.remove(folder.id)
          org.folderListAdd(folder)
        } else {
          folder.load()
          if(folder.isOrgVisible()) {
            org.folderListAdd(folder)
          }
        }
      }
    }

    // add the remaining folders to 'my apps'
    for(folder in mapApp.values()){
      listOrg[0].folderListAdd((IdFolder)folder)
    }

    return listOrg
  }

  IdFolder createFolder(IdUser user, String folderName, Integer privacy, String orgId) {
    IdOrg org
    if(orgId) {
      // check if org exist
      org = new IdOrg(id: orgId)
      def load = org.load()
      if(!load) {
        // failed
        return null
      }
    }
    return createFolder(user, folderName, privacy, org)
  }

  IdFolder createFolder(IdUser user, String folderName, Integer privacy, IdOrg org) {
    // create folder
    IdFolder folder = (IdFolder)new IdFolder()
    .initId()
    .slugify(folderName)
    .withCreatedUpdated()

    // folder owner is either org or user
    if(org) {
      folder.withOwner(org)
    } else {
      folder.withOwner(user)
    }

    folder.privacy = privacy

    def create = folder.create()
    if(!create) {
      // failed
      return null
    }

    // create slug
    def slug = new IdSlug(id: folder.aliasId)
    .withAlias(folder)
    .withCreatedUpdated()

    create = slug.create()
    if(!create) {
      // failed
      folder.delete()
      return null
    }

    // add user to folder
    IdUserFolder userfolder = (IdUserFolder)new IdUserFolder()
    .withMemberRoles(IdUserFolder.MEMBERROLE_ADMIN)
    .withMember(user)
    .withGroup(folder)
    .withCreatedUpdated()

    create = userfolder.create()
    if(!create) {
      // failed
      folder.delete()
      slug.delete()
      return null
    }

    if(org) {
      // add org to folder
      def orgfolder = new IdOrgFolder()
      .withMember(org)
      .withGroup(folder)
      .withCreatedUpdated()

      create = orgfolder.create()
      if(!create) {
        // failed
        userfolder.delete()
        folder.delete()
        slug.delete()
        return null
      }
    }

    return folder
  }

  //user, params.name, params.int('privacy'), params.org, params.aliasId
  String updateFolder(IdFolder folder, String name, Integer privacy, String description, String shortName) {
    //def load = folder.load()
    //if(!load) {
    // not found
    // return
    //}

    def oldslug
    def newslug

    if(shortName) {
      if(folder.aliasId != shortName) {
        // create new slug
        newslug = (IdSlug)new IdSlug().withSlugId(shortName)
        .withAlias(folder)
        .withCreatedUpdated()

        def create = newslug.create()
        if(!create) {
          // failed
          return null
        }

        oldslug = folder.alias
        folder.withAlias(newslug)
      }
    }

    if(name) {
      folder.name  = name
    }

    folder.privacy = privacy
    folder.description = description

    folder.withUpdated()
    def save = folder.save()
    if(save) {
      // cleanup old slug
      if(oldslug) {
        oldslug.delete()
      }
    } else {
      // cleanup new slug
      if(newslug) {
        newslug.delete()
      }
    }

    return folder.aliasId
  }

  // ASSUMES: folder loaded
  //user, params.name, params.int('privacy'), params.org, params.aliasId
  boolean updateFolderOwner(IdFolder folder, String orgId) {
    //def load = folder.load()
    //if(!load) {
    // not found
    // return false
    //}

    def curAccount = folder.owner
    if((orgId || curAccount) && (curAccount?.id != orgId)) {
      IdOrg org
      if(orgId) {
        // check if org exist
        org = new IdOrg(id: orgId)
        def load = org.load()
        if(!load) {
          // failed
          return false
        }
      }

      if(curAccount && folder.isOwnerOrg()) {
        // delete org
        def curorgfolder = new IdOrgFolder()
        .withMember(curAccount)
        .withGroup(folder)
        def del = curorgfolder.delete()
        if(!del) {
          // failed
          return false
        }
      }

      if(org) {
        // update owner
        folder.owner = org
        def saved = folder.save()
        if(saved) {
          // add org to folder
          def orgfolder = new IdOrgFolder()
          .withMember(org)
          .withGroup(folder)
          .withCreatedUpdated()

          def create = orgfolder.create()
          if(!create) {
            // failed
            return false
          }
        }
      }
    }

    return true
  }

  // return true if deleted, returns false if not found
  boolean deleteFolder(IdFolder folder) {
    def load = folder.load()
    if(!load) {
      // not found
      return false
    }

    // delete all org references
    def list = listOrg(folder)
    list.each{
      it.delete() // delete only connection
    }

    // delete all user references
    list = listUser(folder)
    list.each{
      it.delete() // delete only connection
    }

    // delete all email references
    list = listEmail(folder)
    list.each{
      it.delete() // delete only connection
    }

    // delete slug
    new IdSlug(id: folder.aliasId).delete()
    // delete folder
    folder.delete()

    return true
  }

  /////////////////////////////////////////////////////////////////////////////
  // Email
  /////////////////////////////////////////////////////////////////////////////
  IdEmail email(String emailId, instance=true) {
    IdEmail email = (IdEmail)new IdEmail().withId(emailId)
    def load = email.load()
    if(!load) {
      // doesn't exist
      log.debug("Email Not Found: $emailId")
      if(!instance) {
        return null
      }
    }

    return email
  }

  List<IdEmailOrg> listEmail(IdOrg org) {
    List list = new IdEmailOrg().withGroup(org).queryByGroupAndType()
    return list
  }

  List<IdEmailFolder> listEmail(IdFolder folder) {
    List list = new IdEmailFolder().withGroup(folder).queryByGroupAndType()
    return list
  }

  IdEmail createEmail(String emailId, IdUser user=null) {
    // create org
    IdEmail email = (IdEmail)new IdEmail(id: emailId).withCreatedUpdated()

    if(user) {
      email.withAlias(user)
    }

    def create = email.create()
    if(!create) {
      // failed
      return null
    }

    return email
  }

  IdEmail updateEmail(IdEmail email, IdUser user) {
    def load = email.load()
    if(!load) {
      // not found
      return
    }

    email.withAlias(user).withUpdated().save()

    return email
  }

  // return true if deleted, returns false if not found
  boolean deleteEmail(IdEmail email) {
    def load = email.load()
    if(!load) {
      // not found
      return false
    }

    // delete all org references
    def list = listOrg(email)
    list.each{
      it.delete() // delete only connection
    }

    // delete all folder references
    list = listFolder(email)
    list.each{
      it.delete() // delete only connection
    }

    // delete email
    email.delete()

    return true
  }

  // return IdUserOrg and/or IdUserFolder
  List<AIdUserGroup> listGroup(IdUser user) {
    List list = new IdUserOrg().withMember(user).queryByMember()
    return list
  }

  // return IdEmailOrg and/or IdEmailFolder
  List<AIdEmailGroup> listGroup(IdEmail email) {
    List list = new IdEmailOrg().withMember(email).queryByMember()
    return list
  }

  boolean addUserToGroup(IdUser invitedBy, IdUser user, IdOrg org, String... roles) {
    IdUserOrg userorg = (IdUserOrg)new IdUserOrg().withMember(user).withGroup(org)
    if(userorg.load()) {
      userorg.withUpdated()
    } else {
      userorg.withCreatedUpdated()
    }

    userorg.withInvitedBy(invitedBy).withMemberRoles(roles).save()
  }

  boolean addEmailToGroup(IdUser invitedBy, String invitedName, IdEmail email, IdOrg org) {
    IdEmailOrg emailorg = (IdEmailOrg)new IdEmailOrg().withMember(email).withGroup(org)
    if(emailorg.load()) {
      emailorg.withUpdated()
    } else {
      emailorg.withCreatedUpdated()
    }
    emailorg.withInvitedName(invitedName).withInvitedBy(invitedBy).save()
  }

  boolean addUserToGroup(IdUser invitedBy, IdUser user, IdFolder folder, String... roles) {
    IdUserFolder userfolder = (IdUserFolder)new IdUserFolder().withMember(user).withGroup(folder)
    if(userfolder.load()) {
      userfolder.withUpdated()
    } else {
      userfolder.withCreatedUpdated()
    }
    userfolder.withInvitedBy(invitedBy).withMemberRoles(roles).save()
  }

  boolean addEmailToGroup(IdUser invitedBy, String invitedName, IdEmail email, IdFolder folder) {
    IdEmailFolder emailfolder = (IdEmailFolder)new IdEmailFolder().withMember(email).withGroup(folder)
    if(emailfolder.load()) {
      emailfolder.withUpdated()
    } else {
      emailfolder.withCreatedUpdated()
    }
    emailfolder.withInvitedName(invitedName).withInvitedBy(invitedBy).save()
  }

}
