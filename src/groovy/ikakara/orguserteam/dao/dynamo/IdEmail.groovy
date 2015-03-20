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

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import grails.validation.Validateable

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.document.Item


import ikakara.awsinstance.util.StringUtil
import ikakara.simplemarshaller.annotation.SimpleMarshaller

/**
 *
 * @author Allen
 */
@Validateable(nullable = true)
@Slf4j("LOG")
@CompileStatic
public class IdEmail extends AIdBase {

  static public final String ID_TYPE = "Email"
  static public final String ID_PREFIX = '$'

  @DynamoDBAttribute(attributeName = "Status")
  Number status

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
    if (item.isPresent("Status")) {
      status = item.getNumber("Status")
    }
    //}
  }

  @Override
  public Item marshalItemOUT(boolean removeAttributeNull) {
    Item outItem = super.marshalItemOUT(removeAttributeNull)
    if (outItem == null) {
      outItem = new Item()
    }

    if (status != null) {
      outItem = outItem.withNumber("Status", status)
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("Status")
    }

    return outItem
  }

  @Override
  public void initParameters(Map params) {
    super.initParameters(params)
    //if (params != null && !params.isEmpty()) {

    try {
      status = (Integer) params.status
    } catch (Exception e) {

    }

    //}
  }

  public IdEmail() {
  }

   IdEmail(Map params) {
    initParameters(params)
  }

}
