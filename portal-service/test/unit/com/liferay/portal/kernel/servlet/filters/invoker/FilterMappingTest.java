/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.kernel.servlet.filters.invoker;

import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.StringPool;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Mika Koivisto
 */
@RunWith(PowerMockRunner.class)
public class FilterMappingTest extends PowerMockito {

	@Before
	public void setUp() {
		_dispatchers = new ArrayList<String>();

		for (Dispatcher dispatcher : Dispatcher.values()) {
			_dispatchers.add(dispatcher.name());
		}

		_filter = mock(Filter.class);

		_filterConfig = mock(FilterConfig.class);

		when(
			_filterConfig.getInitParameter("url-regex-pattern")
		).thenReturn(
			StringPool.BLANK
		);

		when(
			_filterConfig.getInitParameter("url-regex-ignore-pattern")
		).thenReturn(
			StringPool.BLANK
		);
	}

        /*
         * Reading 
         * <a href='http://cdivilly.wordpress.com/2011/04/22/java-servlets-uri-parameters/'>this article</a>
         * points to information that URI's may contain path parameters for
         * each path segment and further that filter mapping URI patterns
         * should match the path without the parameters.
         * 
         * From <a href='http://tools.ietf.org/html/rfc2396'>RFC2396</a> (URI):
         * <pre>
         *   The path component contains data, specific to the authority (or the
         *   scheme if there is no authority component), identifying the resource
         *   within the scope of that scheme and authority.
         *
         *      path          = [ abs_path | opaque_part ]
         *
         *      path_segments = segment *( "/" segment )
         *      segment       = *pchar *( ";" param )
         *      param         = *pchar
         *
         *      pchar         = unreserved | escaped |
         *                      ":" | "@" | "&" | "=" | "+" | "$" | ","
         *
         *   The path may consist of a sequence of path segments separated by a
         *   single slash "/" character.  Within a path segment, the characters
         *   "/", ";", "=", and "?" are reserved.  Each path segment may include a
         *   sequence of parameters, indicated by the semicolon ";" character.
         *   The parameters are not significant to the parsing of relative
         *   references.
         * </pre>
         * From the <a href='http://jcp.org/aboutJava/communityprocess/final/jsr315/index.html'>servlet spec</a>:
         * <pre>
         *   The path used for mapping to a servlet is the request URL from 
         *   the request object minus the context path and the path parameters.
         * </pre>
         */
	@Test
        public void testIsMatchWithUriPathParameters() {
                List<String> urlPatterns = new ArrayList<String>();

                urlPatterns.add("/c/portal/login");

                FilterMapping filterMapping = new FilterMapping(
                        _filter, _filterConfig, urlPatterns, _dispatchers);

                String uri = "/c;foo=bar,d,you=win/portal;a=b,true=false/login;jsessionid=ACD311312312323BF.worker1";

                MockHttpServletRequest mockHttpServletRequest =
                        new MockHttpServletRequest(HttpMethods.GET, uri);

                Assert.assertEquals(
                        true,
                        filterMapping.isMatch(
                                mockHttpServletRequest, Dispatcher.REQUEST, uri));
        }

	@Test
	public void testIsMatchWithJSessionId() {
		List<String> urlPatterns = new ArrayList<String>();

		urlPatterns.add("/c/portal/login");

		FilterMapping filterMapping = new FilterMapping(
			_filter, _filterConfig, urlPatterns, _dispatchers);

		String uri = "/c/portal/login;jsessionid=ACD311312312323BF.worker1";

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(HttpMethods.GET, uri);

		Assert.assertEquals(
			true,
			filterMapping.isMatch(
				mockHttpServletRequest, Dispatcher.REQUEST, uri));
	}

	@Test
	public void testIsMatchWithoutJSessionId() {
		List<String> urlPatterns = new ArrayList<String>();

		urlPatterns.add("/c/portal/login");

		FilterMapping filterMapping = new FilterMapping(
			_filter, _filterConfig, urlPatterns, _dispatchers);

		String uri = "/c/portal/login";

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(HttpMethods.GET, uri);

		Assert.assertEquals(
			true,
			filterMapping.isMatch(
				mockHttpServletRequest, Dispatcher.REQUEST, uri));
	}

	private List<String> _dispatchers;
	private Filter _filter;
	private FilterConfig _filterConfig;

}