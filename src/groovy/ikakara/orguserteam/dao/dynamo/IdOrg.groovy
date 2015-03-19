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

import java.util.Map
import java.util.Set
import java.util.List
import java.util.ArrayList

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import grails.validation.Validateable

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.document.Item

import ikakara.simplemarshaller.annotation.SimpleMarshaller
import ikakara.awsinstance.util.StringUtil

/**
 *
 * @author Allen
 */
@Validateable(nullable = true)
@SimpleMarshaller(includes = ["id", "type", "aliasId", "teamList", "userList", "visibility", "name", "imageUrl", "webUrl", "description", "createdDate", "updatedDate"])
@Slf4j("LOG")
@CompileStatic
public class IdOrg extends AIdBase {

  static public final String ID_TYPE = "Org"
  static public final String ID_PREFIX = "@"

  static public final Integer VISIBILITY_PUBLIC = 0
  static public final Integer VISIBILITY_PRIVATE = 10

  Number visibility = VISIBILITY_PRIVATE
  String name
  String image_url
  String web_url
  String description

  // transient
  List<IdTeam> teamList = new ArrayList<>()
  List<IdUser> userList = new ArrayList<>()

  @DynamoDBIgnore
  public List<IdTeam> getTeamList() {
    return teamList
  }

  public void teamListAdd(List<IdTeam> list) {
    teamList.addAll(list)
  }

  public void teamListAdd(IdTeam team) {
    teamList.add(team)
  }

  @DynamoDBIgnore
  public List<IdUser> getUserList() {
    return userList
  }

  public void userListAdd(List<IdUser> list) {
    userList.addAll(list)
  }

  public void userListAdd(IdUser user) {
    userList.add(user)
  }

  public boolean isVisibilityPrivate() {
    return visibility.intValue() == VISIBILITY_PRIVATE
  }

  public boolean isVisibilityPublic() {
    return visibility.intValue() == VISIBILITY_PUBLIC
  }

  @Override
  @DynamoDBIgnore
  public String getTypePrefix() {
    return ID_PREFIX
  }

  @Override
  @DynamoDBAttribute(attributeName = "IdType")
  public String getType() {
    return ID_TYPE
  }

  @Override
  public void marshalAttributesIN(Item item) {
    super.marshalAttributesIN(item)
    //if (map != null && !map.isEmpty()) {
    if (item.isPresent("Visibility")) {
      visibility = item.getNumber("Visibility")
    }
    if (item.isPresent("Name")) {
      name = item.getString("Name")
    }
    if (item.isPresent("ImageUrl")) {
      image_url = item.getString("ImageUrl")
    }
    if (item.isPresent("WebUrl")) {
      web_url = item.getString("WebUrl")
    }
    if (item.isPresent("Description")) {
      description = item.getString("Description")
    }
    //}
  }

  @Override
  public Item marshalItemOUT(boolean bRemoveAttributeNull) {
    Item outItem = super.marshalItemOUT(bRemoveAttributeNull)
    if (outItem == null) {
      outItem = new Item()
    }
    if (visibility != null) {
      outItem = outItem.withNumber("Visibility", visibility)
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("Visibility")
    }
    if (name != null) {
      outItem = outItem.withString("Name", name)
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("Name")
    }
    if (image_url != null && !"".equals(image_url)) {
      outItem = outItem.withString("ImageUrl", image_url)
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("ImageUrl")
    }
    if (web_url != null && !"".equals(web_url)) {
      outItem = outItem.withString("WebUrl", web_url)
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("WebUrl")
    }
    if (description != null && !"".equals(description)) {
      outItem = outItem.withString("Description", description)
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("Description")
    }

    return outItem
  }

  @Override
  public void initParameters(Map params) {
    super.initParameters(params)
    //if (params != null && !params.isEmpty()) {

    try {
      visibility = (Integer) params.get("visibility")
      name = (String) params.get("name")
      image_url = (String) params.get("image_url")
      web_url = (String) params.get("web_url")
      description = (String) params.get("description")
    } catch (Exception e) {

    }

    //}
  }

  public IdOrg() {
    super()
  }

  public IdOrg(Map params) {
    super()
    this.initParameters(params)
  }

  public IdOrg initId() {
    id = StringUtil.getRandomChars(6)
    return this
  }

  public IdOrg slugify(String str) {
    if (str != null && !"".equals(str)) {
      name = str
    }

    if (name == null || "".equals(name)) {
      name = "temporary" + StringUtil.getRandomNumbers(10)
    }

    alias = new IdSlug().withSlugId(name).withAlias(this)

    return this
  }

  static public IdOrg fromSlug(String slugId) {
    IdSlug slug = (IdSlug) new IdSlug().withId(slugId)
    boolean bload = slug.load()
    if (!bload) {
      return null
    }

    // verify that the slug is for an org
    AIdBase org = slug.getAlias()
    if (org instanceof IdOrg) {
      return (IdOrg) org
    }

    return null
  }

  public IdUserOrg hasMember(IdUser user) {
    // check to see if user is a member of the org
    IdUserOrg userorg = (IdUserOrg) new IdUserOrg().withMember(user).withGroup(this)
    boolean bload = userorg.load()
    if (!bload) {
      return null
    }

    return userorg
  }

  public IdOrgTeam hasTeam(IdTeam team) {
    // check to see if user is a member of the org
    IdOrgTeam orgteam = (IdOrgTeam) new IdOrgTeam().withMember(this).withGroup(team)
    boolean bload = orgteam.load()
    if (!bload) {
      return null
    }

    return orgteam
  }

  @DynamoDBAttribute(attributeName = "Visibility")
  public Number getVisibility() {
    return visibility
  }

  public void setVisibility(Number d) {
    visibility = d
  }

  @DynamoDBAttribute(attributeName = "Name")
  public String getName() {
    return name
  }

  public void setName(String d) {
    name = d
  }

  @DynamoDBAttribute(attributeName = "ImageUrl")
  public String getImageUrl() {
    return image_url
  }

  public void setImageUrl(String d) {
    image_url = d
  }

  @DynamoDBAttribute(attributeName = "WebUrl")
  public String getWebUrl() {
    return web_url
  }

  public void setWebUrl(String d) {
    web_url = d
  }

  @DynamoDBAttribute(attributeName = "Description")
  public String getDescription() {
    return description
  }

  public void setDescription(String d) {
    description = d
  }

}
