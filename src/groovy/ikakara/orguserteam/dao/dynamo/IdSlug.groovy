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

/**
 * @author Allen
 */
@ToString(includePackage=false, ignoreNulls=true)
@Validateable(nullable = true)
@Slf4j("LOG")
@CompileStatic
class IdSlug extends AIdBase {

  public static final String ID_TYPE = "Slug"
  public static final String ID_PREFIX = "#"

  public static final int SLUG_MINLENGTH = 8

  @DynamoDBAttribute(attributeName = "Status")
  Number status

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
    if (item.isPresent("Status")) {
      status = item.getNumber("Status")
    }
    //}
  }

  @Override
  Item marshalItemOUT(boolean removeAttributeNull) {
    Item outItem = super.marshalItemOUT(removeAttributeNull) ?: new Item()

    if (status != null) {
      outItem = outItem.withNumber("Status", status)
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("Status")
    }

    return outItem
  }

  @Override
  void initParameters(Map params) {
    super.initParameters(params)
    //if (params) {

    try {
      status = (Integer) params.status
    } catch (ignored) {
    }

    //}
  }

  IdSlug() {
  }

  IdSlug(Map params) {
    initParameters(params)
  }

  IdSlug withSlugId(String str) {
    // check for min length
    if (str.length() < SLUG_MINLENGTH) {
      int idiff = SLUG_MINLENGTH - str.length()
      str += StringUtil.getRandomNumbers(idiff)
    }

    try {
      id = StringUtil.slugify(str)
    } catch (IOException ioe) {
      LOG.error("withSlugId: $ioe.message")
    }

    return this
  }
}
