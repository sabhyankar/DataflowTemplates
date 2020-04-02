/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.teleport.cdc.dlq;

import org.apache.beam.sdk.transforms.SimpleFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO when this is a flex template it could incorporate outputs with configurable dest?
/**
 * The DeadLetterQueueSanitizer is an abstract handler to clean and prepare pipeline failures
 * to be stored in a GCS Dead Letter Queue.
 *
 * Extending the DeadLetterQueueSanitizer requires only to
 * implement getJsonMessage() and getErrorMessageJson().
 *
 * NOTE: The input to a Sanitizer is flexible but the output must be a String
 * unless your override formatMessage()
 */
public class DeadLetterQueueSanitizer<InputT, OutputT> extends SimpleFunction<InputT, OutputT> {

  private static final  Logger LOG = LoggerFactory.getLogger(DeadLetterQueueSanitizer.class);

  // public DeadLetterQueueSanitizer() {}

  @Override
  public OutputT apply(InputT input) {
    // Extract details required for DLQ Storage
    String rawJson = getJsonMessage(input);
    String errorMessageJson = getErrorMessageJson(input);

    return formatMessage(rawJson, errorMessageJson);
  }

  // NOTE: Override these 2 functions to extend this class
  public String getJsonMessage(InputT input) {
    return "";
  }

  public String getErrorMessageJson(InputT input) {
    return "";
  }

  // NOTE: Only override formatMessage if required or you desire a non-String output
  public OutputT formatMessage(String rawJson, String errorMessageJson) {
    String dlqRow = String.format(
        "{\"error_message\":%s,\"message\":%s}",
        rawJson, errorMessageJson);

    return (OutputT) dlqRow;
  }

}
