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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;


import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 */
public class FilterMapping {

	public FilterMapping(
		Filter filter, FilterConfig filterConfig, List<String> urlPatterns,
		List<String> dispatchers) {

		_filter = filter;
		_urlPatterns = urlPatterns;

		initFilterConfig(filterConfig);
		initDispatchers(dispatchers);
	}

	public Filter getFilter() {
		return _filter;
	}
	
        /**
         * Strips URI path parameters from each path segment of the supplied
         * URI.
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
         * 
         * @param uri The URI to strip path parameters from.
         * @return The stripped URI.
         * @author Lucas Theisen
         */
	String stripUriPathParameters( String uri ) {
            char[] uriChars = uri.toCharArray();
            int insertPosition = 0;
            boolean ignore = false;
            for ( char c : uriChars ) {
                if ( c == ';' ) {
                    ignore = true;
                }
                else if ( c == '/' ) {
                    ignore = false;
                    uriChars[insertPosition++] = c;
                }
                else if ( c == '?' ) {
                    ignore = false;
                    uriChars[insertPosition++] = c;
                }
                else if ( !ignore ) {
                    uriChars[insertPosition++] = c;
                }
            }
            return new String( uriChars, 0, insertPosition );
        }

	public boolean isMatch(
		HttpServletRequest request, Dispatcher dispatcher, String uri) {

		if (!isMatchDispatcher(dispatcher)) {
			return false;
		}

		if (uri == null) {
			return false;
		}

		uri = stripUriPathParameters( uri );

		boolean matchURLPattern = false;

		for (String urlPattern : _urlPatterns) {
			if (isMatchURLPattern(uri, urlPattern)) {
				matchURLPattern = true;

				break;
			}
		}

		if (_log.isDebugEnabled()) {
			if (matchURLPattern) {
				_log.debug(
					_filter.getClass() + " has a pattern match with " + uri);
			}
			else {
				_log.debug(
					_filter.getClass() +
						" does not have a pattern match with " + uri);
			}
		}

		if (!matchURLPattern) {
			return false;
		}

		if (isMatchURLRegexPattern(request, uri)) {
			return true;
		}

		return false;
	}

	public boolean isMatchURLRegexPattern(
		HttpServletRequest request, String uri) {

		String url = uri;

		String queryString = request.getQueryString();

		if (Validator.isNotNull(queryString)) {
			url = url.concat(StringPool.QUESTION).concat(queryString);
		}

		boolean matchURLRegexPattern = true;

		if (_urlRegexPattern != null) {
			Matcher matcher = _urlRegexPattern.matcher(url);

			matchURLRegexPattern = matcher.find();
		}

		if (matchURLRegexPattern && (_urlRegexIgnorePattern != null)) {
			Matcher matcher = _urlRegexIgnorePattern.matcher(url);

			matchURLRegexPattern = !matcher.find();
		}

		if (_log.isDebugEnabled()) {
			if (matchURLRegexPattern) {
				_log.debug(
					_filter.getClass() + " has a regex match with " + url);
			}
			else {
				_log.debug(
					_filter.getClass() + " does not have a regex match with " +
						url);
			}
		}

		return matchURLRegexPattern;
	}

	public void setFilter(Filter filter) {
		_filter = filter;
	}

	protected void initDispatchers(List<String> dispatchers) {
		for (String dispatcher : dispatchers) {
			if (dispatcher.equals("ERROR")) {
				_dispatcherError = true;
			}
			else if (dispatcher.equals("FORWARD")) {
				_dispatcherForward = true;
			}
			else if (dispatcher.equals("INCLUDE")) {
				_dispatcherInclude = true;
			}
			else if (dispatcher.equals("REQUEST")) {
				_dispatcherRequest = true;
			}
			else {
				throw new IllegalArgumentException(
					"Invalid dispatcher " + dispatcher);
			}
		}

		if (!_dispatcherError && !_dispatcherForward && !_dispatcherInclude &&
			!_dispatcherRequest) {

			_dispatcherRequest = true;
		}
	}

	protected void initFilterConfig(FilterConfig filterConfig) {
		String urlRegexPattern = GetterUtil.getString(
			filterConfig.getInitParameter("url-regex-pattern"));

		if (Validator.isNotNull(urlRegexPattern)) {
			_urlRegexPattern = Pattern.compile(urlRegexPattern);
		}

		String urlRegexIgnorePattern = GetterUtil.getString(
			filterConfig.getInitParameter("url-regex-ignore-pattern"));

		if (Validator.isNotNull(urlRegexIgnorePattern)) {
			_urlRegexIgnorePattern = Pattern.compile(urlRegexIgnorePattern);
		}
	}

	protected boolean isMatchDispatcher(Dispatcher dispatcher) {
		if (((dispatcher == Dispatcher.ERROR) && _dispatcherError) ||
			((dispatcher == Dispatcher.FORWARD) && _dispatcherForward) ||
			((dispatcher == Dispatcher.INCLUDE) && _dispatcherInclude) ||
			((dispatcher == Dispatcher.REQUEST) && _dispatcherRequest)) {

			return true;
		}
		else {
			return false;
		}
	}

	protected boolean isMatchURLPattern(String uri, String urlPattern) {
		if (urlPattern.equals(uri)) {
			return true;
		}

		if (urlPattern.equals(_SLASH_STAR)) {
			return true;
		}

		if (urlPattern.endsWith(_SLASH_STAR)) {
			if (urlPattern.regionMatches(0, uri, 0, urlPattern.length() - 2)) {
				if (uri.length() == (urlPattern.length() - 2)) {
					return true;
				}
				else if (CharPool.SLASH ==
							uri.charAt(urlPattern.length() - 2)) {

					return true;
				}
			}
		}
		else if (urlPattern.startsWith(_STAR_PERIOD)) {
			int slashPos = uri.lastIndexOf(CharPool.SLASH);
			int periodPos = uri.lastIndexOf(CharPool.PERIOD);

			if ((slashPos >= 0) && (periodPos > slashPos) &&
				(periodPos != (uri.length() - 1)) &&
				((uri.length() - periodPos) == (urlPattern.length() - 1))) {

				if (urlPattern.regionMatches(
						2, uri, periodPos + 1, urlPattern.length() - 2)) {

					return true;
				}
			}
		}

		return false;
	}

	private static final String _SLASH_STAR = "/*";

	private static final String _STAR_PERIOD = "*.";

	private static Log _log = LogFactoryUtil.getLog(FilterMapping.class);

	private boolean _dispatcherError;
	private boolean _dispatcherForward;
	private boolean _dispatcherInclude;
	private boolean _dispatcherRequest;
	private Filter _filter;
	private List<String> _urlPatterns;
	private Pattern _urlRegexIgnorePattern;
	private Pattern _urlRegexPattern;

}