/*
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.gate.controllers

import com.netflix.spinnaker.gate.services.DataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.AntPathMatcher
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.HandlerMapping

import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/v1/data")
class DataController {
  @Autowired
  DataService dataService

  @RequestMapping(value = "/static/{id}", method = RequestMethod.GET)
  @ResponseBody byte[] getStaticData(@PathVariable("id") String id,
                                     @RequestParam Map<String, String> filters) {
    def outputStream = new ByteArrayOutputStream()
    dataService.getStaticData(id, filters, outputStream)
    return outputStream.toByteArray()
  }

  @RequestMapping(value = "/adhoc/{groupId}/{bucketId}/**")
  @ResponseBody byte[] getAdhocData(@PathVariable("groupId") String groupId,
                                    @PathVariable("bucketId") String bucketId,
                                    HttpServletRequest httpServletRequest) {
    String pattern = (String) httpServletRequest.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE)
    String objectId = new AntPathMatcher().extractPathWithinPattern(pattern, httpServletRequest.getServletPath())

    def outputStream = new ByteArrayOutputStream()
    dataService.getAdhocData(groupId, bucketId, objectId, outputStream)
    return outputStream.toByteArray()
  }
}
