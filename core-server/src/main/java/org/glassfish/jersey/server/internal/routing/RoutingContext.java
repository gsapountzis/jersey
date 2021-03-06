/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.server.internal.routing;

import java.util.List;
import java.util.regex.MatchResult;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

import org.glassfish.jersey.model.internal.RankedProvider;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.glassfish.jersey.uri.UriTemplate;

/**
 * Jersey request matching and routing context.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 * @author Martin Matula (martin.matula at oracle.com)
 */
public interface RoutingContext extends ResourceInfo {

    /**
     * Push the result of the successful request URI routing pattern match.
     *
     * @param matchResult successful request URI routing pattern
     *                    {@link java.util.regex.MatchResult match result}.
     */
    public void pushMatchResult(MatchResult matchResult);

    /**
     * Push the resource that matched the request URI.
     *
     * @param resource instance of the resource that matched the request URI.
     */
    public void pushMatchedResource(Object resource);

    /**
     * Peek the last resource object that successfully matched the request URI.
     *
     * @return last resource matched as previously set by {@link #pushMatchedResource}
     */
    public Object peekMatchedResource();

    /**
     * Peek at the last successful request URI routing pattern
     * {@link java.util.regex.MatchResult match result}.
     *
     * @return last successful request URI routing pattern match result.
     */
    public MatchResult peekMatchResult();

    /**
     * Push matched request URI routing pattern {@link org.glassfish.jersey.uri.UriTemplate URI template}.
     *
     * @param template URI template of the matched request URI routing pattern.
     */
    public void pushTemplate(UriTemplate template);

    /**
     * Get the final matching group of the last successful request URI routing
     * pattern {@link java.util.regex.MatchResult match result}. Also known as right-hand path.
     * <p>
     * May be empty but is never {@code null}.
     * </p>
     *
     * @return final matching group of the last successful request URI routing
     *         pattern match result.
     */
    public String getFinalMatchingGroup();

    /**
     * Get a read-only list of {@link java.util.regex.MatchResult match results} for matched
     * request URI routing patterns. Entries are ordered in reverse request URI
     * matching order, with the root request URI routing pattern match result
     * last.
     *
     * @return a read-only reverse list of request URI routing pattern match
     *         results.
     */
    public List<MatchResult> getMatchedResults();

    /**
     * Add currently matched left-hand side part of request path to the list of
     * matched paths returned by {@link javax.ws.rs.core.UriInfo#getMatchedURIs()}.
     * <p/>
     * Left-hand side request path is the request path excluding the suffix
     * part of the path matched by the {@link #getFinalMatchingGroup() final
     * matching group} of the last successful request URI routing pattern.
     */
    public void pushLeftHandPath();

    /**
     * Set the matched request to response inflector.
     *
     * This method can be used in a non-terminal stage to set the inflector that
     * can be retrieved and processed by a subsequent stage.
     *
     * @param inflector matched request to response inflector.
     */
    public void setInflector(Inflector<ContainerRequest, ContainerResponse> inflector);

    /**
     * Get the matched request to response data inflector if present, or {@code null}
     * otherwise.
     *
     * @return matched request to response inflector, or {@code null} if not available.
     */
    public Inflector<ContainerRequest, ContainerResponse> getInflector();

    /**
     * Get all bound request filters applicable to this request.
     * This is populated once the right resource method is matched.
     *
     * @return All bound (dynamically or by name) request filters applicable to the matched inflector (or an empty
     * collection if no inflector matched yet).
     */
    public Iterable<RankedProvider<ContainerRequestFilter>> getBoundRequestFilters();

    /**
     * Get all bound response filters applicable to this request.
     * This is populated once the right resource method is matched.
     *
     * @return All bound (dynamically or by name) response filters applicable to the matched inflector (or an empty
     * collection if no inflector matched yet).
     */
    public Iterable<RankedProvider<ContainerResponseFilter>> getBoundResponseFilters();

    /**
     * Get all reader interceptors applicable to this request.
     * This is populated once the right resource method is matched.
     *
     * @return All reader interceptors applicable to the matched inflector (or an empty
     * collection if no inflector matched yet).
     */
    public Iterable<RankedProvider<ReaderInterceptor>> getBoundReaderInterceptors();

    /**
     * Get all writer interceptors applicable to this request.
     * This is populated once the right resource method is matched.
     *
     * @return All writer interceptors applicable to the matched inflector (or an empty
     * collection if no inflector matched yet).
     */
    public Iterable<RankedProvider<WriterInterceptor>> getBoundWriterInterceptors();

    /**
     * Set the matched {@link ResourceMethod resource method}. This method needs to be called only if the method was
     * matched. This method should be called only for setting the final resource method and not for setting sub resource
     * locators invoked during matching.
     *
     * @param resourceMethod Resource method that was matched.
     */
    public void setMatchedResourceMethod(ResourceMethod resourceMethod);

    /**
     * Set the matched {@link Resource resource or child resource}. This method needs to be called only if the resource was
     * matched. This method should be called only setting for final resource which contains matched resource method
     * and not for setting resources which contains sub resource locators invoked during matching.
     *
     * @param resource Resource that was matched.
     */
    public void setMatchedResource(Resource resource);

    /**
     * Push a matched {@link RuntimeResource runtime resource} that was visited during matching phase. This method must
     * be called for any matched runtime resource.
     *
     * @param runtimeResource Runtime resource that was matched during matching.
     */
    public void pushMatchedRuntimeResource(RuntimeResource runtimeResource);
}
