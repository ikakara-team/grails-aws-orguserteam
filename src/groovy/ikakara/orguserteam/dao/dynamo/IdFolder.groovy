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

import groovy.transform.ToString
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
@ToString(includePackage=false, includeNames=true, ignoreNulls=true, includeSuperProperties=true)
@Validateable(nullable = true)
@SimpleMarshaller(includes = ["id", "type", "aliasId", "owner", "userList", "privacy", "imageUrl", "name", "description", "createdDate", "updatedDate"])
@Slf4j("LOG")
@CompileStatic
class IdFolder extends AIdBase implements TIdGroup {

  public static final String ID_TYPE = "Folder"
  public static final String ID_PREFIX = "!"

  public static final Integer PRIVACY_PUBLIC = 0
  public static final Integer PRIVACY_ORG = 1
  public static final Integer PRIVACY_PRIVATE = 10

  @DynamoDBAttribute(attributeName = "Privacy")
  Number privacy = PRIVACY_PRIVATE
  @DynamoDBAttribute(attributeName = "ImageUrl")
  String imageUrl
  @DynamoDBAttribute(attributeName = "Description")
  String description

  boolean isPrivacyPrivate() {
    return privacy.intValue() == PRIVACY_PRIVATE
  }

  boolean isPrivacyPublic() {
    return privacy.intValue() == PRIVACY_PUBLIC
  }

  boolean isPrivacyOrg() {
    return privacy.intValue() == PRIVACY_ORG
  }

  boolean isOrgVisible() {
    return privacy.intValue() < PRIVACY_PRIVATE
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
    marshalOwnerIn(item)

    if (item.isPresent("Privacy")) {
      privacy = item.getNumber("Privacy")
    }
    if (item.isPresent("ImageUrl")) {
      imageUrl = item.getString("ImageUrl")
    }
    if (item.isPresent("Description")) {
      description = item.getString("Description")
    }
    //}
  }

  @Override
  Item marshalItemOUT(List removeAttributeNull) {
    Item outItem = super.marshalItemOUT(removeAttributeNull) ?: new Item()

    outItem = marshalOwnerOUT(outItem, removeAttributeNull)

    if (privacy != null) {
      outItem = outItem.withNumber("Privacy", privacy)
    } else if (removeAttributeNull != null) {
      removeAttributeNull.add("Privacy")
    }
    if (imageUrl) {
      outItem = outItem.withString("ImageUrl", imageUrl)
    } else if (removeAttributeNull != null) {
      removeAttributeNull.add("ImageUrl")
    }
    if (description) {
      outItem = outItem.withString("Description", description)
    } else if (removeAttributeNull != null) {
      removeAttributeNull.add("Description")
    }

    return outItem
  }

  @Override
  void initParameters(Map params) {
    super.initParameters(params)
    //if (params) {

    try {
      privacy = (Integer) params.privacy
      imageUrl = (String) params.image_url
      description = (String) params.description
    } catch (e) {
      LOG.error("initParameters", e)
    }

    //}
  }

  IdFolder() {
  }

  IdFolder(Map params) {
    initParameters(params)
  }

  IdFolder initId() {
    id = "1" + StringUtil.getRandomNumbers(9)
    return this
  }

  Integer toInteger() {
    try {
      return Integer.parseInt(id)
    } catch (e) {
      LOG.error(e.message, e)
      return -1
    }
  }

  IdFolder slugify(String str) {
    if (str) {
      name = str
    }

    if (!name) {
      name = "untitled" + StringUtil.getRandomNumbers(10)
    }

    alias = new IdSlug().withSlugId(name).withAlias(this)

    return this
  }

  static IdFolder fromSlug(String slugId) {
    IdSlug slug = (IdSlug) new IdSlug().withId(slugId)
    boolean load = slug.load()
    if (!load) {
      return null
    }

    // verify that the slug is for a folder
    AIdBase folder = slug.alias
    return (folder instanceof IdFolder) ? (IdFolder) folder : null
  }

  IdUserFolder hasMember(IdUser user) {
    // check to see if user is a member of the folder
    IdUserFolder userfolder = (IdUserFolder) new IdUserFolder().withMember(user).withGroup(this)
    boolean load = userfolder.load()
    return load ? userfolder : null
  }
}
