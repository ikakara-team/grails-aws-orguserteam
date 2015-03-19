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

/**
 *
 * @author Allen
 */
@Validateable(nullable = true)
@Slf4j("LOG")
@CompileStatic
public class IdUserOrg extends AMemberGroupBase {

  static public final String ID_TYPE = "UserOrg"
  static public final String ROLE_ADMIN = "admin"

  @Override
  @DynamoDBAttribute(attributeName = "IdType")
  public String getType() {
    return ID_TYPE
  }

  public IdUserOrg() {
    super()
  }

  public IdUserOrg(Map params) {
    super()
    initParameters(params)
  }

  public boolean isAdmin() {
    return ROLE_ADMIN.equals(member_role)
  }

}
