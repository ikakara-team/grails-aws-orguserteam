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
package ikakara.orguserteam.dao.dynamo

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import grails.validation.Validateable

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.document.Item

import ikakara.awsinstance.util.StringUtil
import ikakara.simplemarshaller.annotation.SimpleMarshaller

/**
 * @author Allen
 */
@Validateable(nullable = true)
@SimpleMarshaller(includes = ["id", "type", "aliasId", "teamList", "userList", "visibility", "name", "imageUrl", "webUrl", "description", "createdDate", "updatedDate"])
@Slf4j("LOG")
@CompileStatic
class IdOrg extends AIdBase {

  public static final String ID_TYPE = "Org"
  public static final String ID_PREFIX = "@"

  public static final Integer VISIBILITY_PUBLIC = 0
  public static final Integer VISIBILITY_PRIVATE = 10

  @DynamoDBAttribute(attributeName = "Visibility")
  Number visibility = VISIBILITY_PRIVATE
  @DynamoDBAttribute(attributeName = "Name")
  String name
  @DynamoDBAttribute(attributeName = "ImageUrl")
  String imageUrl
  @DynamoDBAttribute(attributeName = "WebUrl")
  String webUrl
  @DynamoDBAttribute(attributeName = "Description")
  String description

  // transient
  List<IdTeam> teamList = []
  List<IdUser> userList = []

  @DynamoDBIgnore
  List<IdTeam> getTeamList() {
    return teamList
  }

  void teamListAdd(List<IdTeam> list) {
    teamList.addAll(list)
  }

  void teamListAdd(IdTeam team) {
    teamList << team
  }

  @DynamoDBIgnore
  List<IdUser> getUserList() {
    return userList
  }

  void userListAdd(List<IdUser> list) {
    userList.addAll(list)
  }

  void userListAdd(IdUser user) {
    userList << user
  }

  boolean isVisibilityPrivate() {
    return visibility.intValue() == VISIBILITY_PRIVATE
  }

  boolean isVisibilityPublic() {
    return visibility.intValue() == VISIBILITY_PUBLIC
  }

  @Override
  @DynamoDBIgnore
  String getTypePrefix() {
    return ID_PREFIX
  }

  @Override
  @DynamoDBAttribute(attributeName = "IdType")
  String getType() {
    return ID_TYPE
  }

  @Override
  void marshalAttributesIN(Item item) {
    super.marshalAttributesIN(item)
    //if (map) {
    if (item.isPresent("Visibility")) {
      visibility = item.getNumber("Visibility")
    }
    if (item.isPresent("Name")) {
      name = item.getString("Name")
    }
    if (item.isPresent("ImageUrl")) {
      imageUrl = item.getString("ImageUrl")
    }
    if (item.isPresent("WebUrl")) {
      webUrl = item.getString("WebUrl")
    }
    if (item.isPresent("Description")) {
      description = item.getString("Description")
    }
    //}
  }

  @Override
  Item marshalItemOUT(boolean removeAttributeNull) {
    Item outItem = super.marshalItemOUT(removeAttributeNull) ?: new Item()
    if (visibility != null) {
      outItem = outItem.withNumber("Visibility", visibility)
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("Visibility")
    }
    if (name) {
      outItem = outItem.withString("Name", name)
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("Name")
    }
    if (imageUrl) {
      outItem = outItem.withString("ImageUrl", imageUrl)
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("ImageUrl")
    }
    if (webUrl) {
      outItem = outItem.withString("WebUrl", webUrl)
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("WebUrl")
    }
    if (description) {
      outItem = outItem.withString("Description", description)
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("Description")
    }

    return outItem
  }

  @Override
  void initParameters(Map params) {
    super.initParameters(params)
    //if (params) {

    try {
      visibility = (Integer) params.visibility
      name = (String) params.name
      imageUrl = (String) params.imageUrl
      webUrl = (String) params.webUrl
      description = (String) params.description
    } catch (ignored) {
    }

    //}
  }

  IdOrg() {
  }

  IdOrg(Map params) {
    initParameters(params)
  }

  IdOrg initId() {
    id = StringUtil.getRandomChars(6)
    return this
  }

  IdOrg slugify(String str) {
    if (str) {
      name = str
    }

    if (!name) {
      name = "temporary" + StringUtil.getRandomNumbers(10)
    }

    alias = new IdSlug().withSlugId(name).withAlias(this)

    return this
  }

  static IdOrg fromSlug(String slugId) {
    IdSlug slug = (IdSlug) new IdSlug().withId(slugId)
    boolean load = slug.load()
    if (!load) {
      return null
    }

    // verify that the slug is for an org
    AIdBase org = slug.alias
    return org instanceof IdOrg ? (IdOrg) org : null
  }

  IdUserOrg hasMember(IdUser user) {
    // check to see if user is a member of the org
    IdUserOrg userorg = (IdUserOrg) new IdUserOrg().withMember(user).withGroup(this)
    boolean load = userorg.load()
    return load ? userorg : null
  }

  IdOrgTeam hasTeam(IdTeam team) {
    // check to see if user is a member of the org
    IdOrgTeam orgteam = (IdOrgTeam) new IdOrgTeam().withMember(this).withGroup(team)
    boolean load = orgteam.load()
    return load ? orgteam : null
  }
}
