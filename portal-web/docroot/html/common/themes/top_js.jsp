<%
/**
 * Copyright (c) 2000-2006 Liferay, LLC. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
%>

<c:choose>
	<c:when test="<%= GetterUtil.getBoolean(PropsUtil.get(PropsUtil.JAVASCRIPT_FAST_LOAD)) %>">
		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/everything.js"></script>
	</c:when>
	<c:otherwise>
		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/sniffer.js"></script>
		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/menu.js"></script>
		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/rollovers.js"></script>
		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/util.js"></script>
		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/validation.js"></script>
		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/portal.js"></script>
		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/tabs.js"></script>

		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/ajax/prototype.js"></script>
		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/ajax/ajaxtags.js"></script>
		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/ajax/ajax.js"></script>

		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/calendar/calendar_stripped.js"></script>
		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/calendar/calendar-setup_stripped.js"></script>

		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/colorpicker/colorpicker.js"></script>

		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/dragdrop/coordinates.js"></script>
		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/dragdrop/drag.js"></script>
		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/dragdrop/dragdrop.js"></script>

		<script type="text/javascript" src="<%= themeDisplay.getPathJavaScript() %>/portlet/layout_configuration.js"></script>
	</c:otherwise>
</c:choose>