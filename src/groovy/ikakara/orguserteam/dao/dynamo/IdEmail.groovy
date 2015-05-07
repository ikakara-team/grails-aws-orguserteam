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

import groovy.transform.ToString
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import grails.validation.Validateable

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.document.Item

import ikakara.awsinstance.util.CalendarUtil
import ikakara.awsinstance.util.StringUtil
import ikakara.simplemarshaller.annotation.SimpleMarshaller

/**
 *
 * @author Allen
 */
@ToString(includePackage=false, includeNames=true, ignoreNulls=true, includeSuperProperties=true)
@Validateable(nullable = true)
@SimpleMarshaller(includes = ["id", "type", "aliasId", "createdDate", "updatedDate"])
@Slf4j("LOG")
@CompileStatic
class IdEmail extends AIdBase {

  static public final String ID_TYPE = "Email"
  static public final String ID_PREFIX = '$'

  @DynamoDBAttribute(attributeName = "EmailSentCount")
  Number emailSentCount;
  @DynamoDBAttribute(attributeName = "EmailSentLast")
  String emailSentLast;
  @DynamoDBAttribute(attributeName = "EmailSentLastError")
  String emailSentLastError;

  // transient
  protected Date lastSentDate = null;
  protected Date lastSentErrorDate = null;

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
    //if (map != null && !map.isEmpty()) {
    if (item.isPresent("EmailSentCount")) {
      emailSentCount = item.getNumber("EmailSentCount");
    }
    if (item.isPresent("EmailSentLast")) {
      emailSentLast = item.getString("EmailSentLast");
    }
    if (item.isPresent("EmailSentLastError")) {
      emailSentLastError = item.getString("EmailSentLastError");
    }
    //}
  }

  @Override
  Item marshalItemOUT(List removeAttributeNull) {
    Item outItem = super.marshalItemOUT(removeAttributeNull) ?: new Item()
    if (emailSentCount != null) {
      outItem = outItem.withNumber("EmailSentCount", emailSentCount);
    } else if (removeAttributeNull != null) {
      removeAttributeNull.add("EmailSentCount");
    }
    if (emailSentLast) {
      outItem = outItem.withString("EmailSentLast", emailSentLast);
    } else if (removeAttributeNull != null) {
      removeAttributeNull.add("EmailSentLast");
    }
    if (emailSentLastError) {
      outItem = outItem.withString("EmailSentLastError", emailSentLastError);
    } else if (removeAttributeNull != null) {
      removeAttributeNull.add("EmailSentLastError");
    }
    return outItem
  }

  @Override
  void initParameters(Map params) {
    super.initParameters(params)
    //if (params != null && !params.isEmpty()) {

    try {
      emailSentCount = (Integer) params.emailSentCount
      emailSentLast = params.emailSentLast
      emailSentLastError = params.emailSentLastError
    } catch (Exception e) {

    }

    //}
  }

  IdEmail() {
  }

  IdEmail(Map params) {
    initParameters(params)
  }

  @DynamoDBIgnore
  public Date getLastSentDate() {
    if (!lastSentDate) {
      if (emailSentLast) {
        lastSentDate = CalendarUtil.getDateFromString_CONCISE_MS(emailSentLast);
      } else {
        setLastSentDate(new Date());
      }
    }
    return lastSentDate;
  }

  public void setLastSentDate(Date d) {
    lastSentDate = d;
    emailSentLast = CalendarUtil.getStringFromDate_CONCISE_MS(d);
  }

  /////////////
  @DynamoDBIgnore
  public Date getLastSentErrorDate() {
    if (!lastSentErrorDate) {
      if (emailSentLastError) {
        lastSentErrorDate = CalendarUtil.getDateFromString_CONCISE_MS(emailSentLastError);
      } else {
        setLastSentErrorDate(new Date());
      }
    }
    return lastSentErrorDate;
  }

  public void setLastSentErrorDate(Date d) {
    lastSentErrorDate = d;
    emailSentLastError = CalendarUtil.getStringFromDate_CONCISE_MS(d);
  }

}
