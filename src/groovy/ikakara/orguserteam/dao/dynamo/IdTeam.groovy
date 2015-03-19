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
import java.util.ArrayList
import java.util.List

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
@SimpleMarshaller(includes = ["id", "type", "aliasId", "owner", "userList", "privacy", "imageUrl", "name", "description", "createdDate", "updatedDate"])
@Slf4j("LOG")
@CompileStatic
public class IdTeam extends AIdBase {

  static public final String ID_TYPE = "Team"
  static public final String ID_PREFIX = "!"

  static public final Integer PRIVACY_PUBLIC = 0
  static public final Integer PRIVACY_ORG = 1
  static public final Integer PRIVACY_PRIVATE = 10

  Number privacy = PRIVACY_PRIVATE
  String image_url
  String name
  String description

  // transient
  AIdBase owner
  List<IdUser> userList = new ArrayList<>()

  @DynamoDBIgnore
  public AIdBase getOwner() {
    if (owner == null) {
      // check to see if team is part of the org
      List<AMemberGroupBase> list = new IdOrgTeam().withGroup(this).queryByGroup()
      if (list != null) {
        for (AMemberGroupBase id : list) {
          // there should only be 1 owner; what should we do if there's more than 1???
          owner = id.getMember()
          break
        }
      }
    }

    return owner
  }

  public void setOwner(AIdBase own) {
    owner = own
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

  public boolean isPrivacyPrivate() {
    return privacy.intValue() == PRIVACY_PRIVATE
  }

  public boolean isPrivacyPublic() {
    return privacy.intValue() == PRIVACY_PUBLIC
  }

  public boolean isPrivacyOrg() {
    return privacy.intValue() == PRIVACY_ORG
  }

  public boolean isOrgVisible() {
    return privacy.intValue() < PRIVACY_PRIVATE
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
    if (item.isPresent("Privacy")) {
      privacy = item.getNumber("Privacy")
    }
    if (item.isPresent("ImageUrl")) {
      image_url = item.getString("ImageUrl")
    }
    if (item.isPresent("Name")) {
      name = item.getString("Name")
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

    if (privacy != null) {
      outItem = outItem.withNumber("Privacy", privacy)
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("Privacy")
    }
    if (image_url != null && !"".equals(image_url)) {
      outItem = outItem.withString("ImageUrl", image_url)
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("ImageUrl")
    }
    if (name != null && !"".equals(name)) {
      outItem = outItem.withString("Name", name)
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("Name")
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
      privacy = (Integer) params.get("privacy")
      image_url = (String) params.get("image_url")
      name = (String) params.get("name")
      description = (String) params.get("description")
    } catch (Exception e) {
      LOG.error("initParameters", e)
    }

    //}
  }

  public IdTeam() {
    super()
  }

  public IdTeam(Map params) {
    super()
    initParameters(params)
  }

  public IdTeam initId() {
    id = "1" + StringUtil.getRandomNumbers(9)
    return this
  }

  public Integer toInteger() {
    Integer iId = -1
    try {
      iId = Integer.parseInt(id)
    } catch (Exception e) {
      LOG.error(e.getMessage())
    }
    return iId
  }

  public IdTeam slugify(String str) {
    if (str != null && !"".equals(str)) {
      name = str
    }

    if (name == null || "".equals(name)) {
      name = "untitled" + StringUtil.getRandomNumbers(10)
    }

    alias = new IdSlug().withSlugId(name).withAlias(this)

    return this
  }

  static public IdTeam fromSlug(String slugId) {
    IdSlug slug = (IdSlug) new IdSlug().withId(slugId)
    boolean bload = slug.load()
    if (!bload) {
      return null
    }

    // verify that the slug is for an app
    AIdBase app = slug.getAlias()
    if (app instanceof IdTeam) {
      return (IdTeam) app
    }

    return null
  }

  public IdUserTeam hasMember(IdUser user) {
    // check to see if user is a member of the team
    IdUserTeam userapp = (IdUserTeam) new IdUserTeam().withMember(user).withGroup(this)
    boolean bload = userapp.load()
    if (!bload) {
      return null
    }

    return userapp
  }

  @DynamoDBAttribute(attributeName = "Privacy")
  public Number getPrivacy() {
    return privacy
  }

  public void setPrivacy(Number d) {
    privacy = d
  }

  @DynamoDBAttribute(attributeName = "ImageUrl")
  public String getImageUrl() {
    return image_url
  }

  public void setImageUrl(String d) {
    image_url = d
  }

  @DynamoDBAttribute(attributeName = "Name")
  public String getName() {
    return name
  }

  public void setName(String d) {
    name = d
  }

  @DynamoDBAttribute(attributeName = "Description")
  public String getDescription() {
    return description
  }

  public void setDescription(String d) {
    description = d
  }

}
