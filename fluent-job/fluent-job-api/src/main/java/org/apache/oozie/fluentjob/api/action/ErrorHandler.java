/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.oozie.fluentjob.api.action;

import com.google.common.base.Preconditions;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;

/**
 * A class encapsulating an action so that it can be used as an error handler in a workflow.
 *
 * In an Oozie workflow definition (XML), every action has an "ok-transition" that is taken if the action completed
 * successfully and an "error-transition" that is taken if the action failed. In this API, the dependency relations
 * specified will be translated into "ok-transitions".
 *
 * If you would like to provide some error handling in case of action failure, you should add an {@link ErrorHandler}
 * to the {@link Node} representing the action. The error handler action will be added as the "error-transition" of
 * the original action in the generated Oozie workflow XML. Both the "ok-transition" and the "error-transition" of the
 * error handler action itself will lead to an autogenerated kill node.
 */
@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ErrorHandler {
    private final Node handlerNode;

    /**
     * Creates a new {@link ErrorHandler}. The provided builder is used to build the underlying error handler action.
     * The builder should be in a state where no parents are specified, otherwise an exception is thrown.
     * @param builder The builder that is used to build the underlying error handler node.
     * @return A new {@link ErrorHandler}.
     *
     * @throws IllegalStateException if the provided builder has parents registered.
     */
    public static ErrorHandler buildAsErrorHandler(final Builder<? extends Node> builder) {
        final Node handlerNode = builder.build();
        return new ErrorHandler(handlerNode);
    }

    private ErrorHandler(final Node handlerNode) {
        final boolean hasParents = !handlerNode.getAllParents().isEmpty();
        final boolean hasChildren = !handlerNode.getAllChildren().isEmpty();
        Preconditions.checkState(!hasParents && !hasChildren, "Error handler nodes cannot have parents or children.");

        this.handlerNode = handlerNode;
    }

    /**
     * Returns the name of the error handler action.
     * @return The name of the error handler action.
     */
    public String getName() {
        return handlerNode.getName();
    }

    /**
     * Returns the error handler action node.
     * @return The error handler action node.
     */
    public Node getHandlerNode() {
        return handlerNode;
    }
}
