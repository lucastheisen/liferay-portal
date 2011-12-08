<%--
/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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
--%>

<%@ include file="/html/portlet/sites_admin/init.jsp" %>

<%
Group group = (Group)request.getAttribute("site.group");

LayoutSet privateLayoutSet = LayoutSetLocalServiceUtil.getLayoutSet(group.getGroupId(), true);
LayoutSet publicLayoutSet = LayoutSetLocalServiceUtil.getLayoutSet(group.getGroupId(), false);

LayoutSetPrototype privateLayoutSetPrototype = null;

boolean privateLayoutSetPrototypeInheritance = false;

LayoutSetPrototype publicLayoutSetPrototype = null;

boolean publicLayoutSetPrototypeInheritance = false;

if (Validator.isNotNull(privateLayoutSet.getLayoutSetPrototypeUuid())) {
	privateLayoutSetPrototype = LayoutSetPrototypeLocalServiceUtil.getLayoutSetPrototypeByUuid(privateLayoutSet.getLayoutSetPrototypeUuid());

	privateLayoutSetPrototypeInheritance = privateLayoutSet.getLayoutSetPrototypeLinkEnabled();
}

if (Validator.isNotNull(publicLayoutSet.getLayoutSetPrototypeUuid())) {
	publicLayoutSetPrototype = LayoutSetPrototypeLocalServiceUtil.getLayoutSetPrototypeByUuid(publicLayoutSet.getLayoutSetPrototypeUuid());

	publicLayoutSetPrototypeInheritance = publicLayoutSet.getLayoutSetPrototypeLinkEnabled();
}
%>

<liferay-ui:error-marker key="errorSection" value="siteTempate" />

<c:if test="<%= (publicLayoutSetPrototype == null) && (privateLayoutSetPrototype == null) %>">
	<div class="portlet-msg-info">
		<liferay-ui:message key="there-are-no-templates-used-on-this-site" />
	</div>
</c:if>

<aui:fieldset>
	<c:if test="<%= publicLayoutSetPrototype != null %>">
		<aui:fieldset label="public-site-template">
			<c:choose>
				<c:when test="<%= publicLayoutSetPrototypeInheritance %>">

					<%
					boolean layoutsUpdateable = GetterUtil.getBoolean(publicLayoutSetPrototype.getSettingsProperty("layoutsUpdateable"), true);
					%>

					<liferay-ui:message arguments="<%= new Object[] {publicLayoutSetPrototype.getName(locale)} %>" key="these-pages-are-linked-to-template-x" />

					<aui:field-wrapper label="site-template-settings">
						<aui:input disabled="<%= true %>" name="active" value="<%= publicLayoutSetPrototype.isActive() %>" type="checkbox" />
						<aui:input disabled="<%= true %>" name="allow-modifications" type="checkbox" value="<%= layoutsUpdateable %>" />
					</aui:field-wrapper>
				</c:when>
				<c:otherwise>
					<liferay-ui:message arguments="<%= new Object[] {publicLayoutSetPrototype.getName(locale)} %>" key="this-site-was-cloned-from-template-x" />
				</c:otherwise>
			</c:choose>
		</aui:fieldset>
	</c:if>

	<c:if test="<%= privateLayoutSetPrototype != null %>">
		<aui:fieldset label="private-site-template">
			<c:choose>
				<c:when test="<%= privateLayoutSetPrototypeInheritance %>">

					<%
					boolean layoutsUpdateable = GetterUtil.getBoolean(privateLayoutSetPrototype.getSettingsProperty("layoutsUpdateable"), true);
					%>

					<liferay-ui:message arguments="<%= new Object[] {privateLayoutSetPrototype.getName(locale)} %>" key="these-pages-are-linked-to-template-x" />

					<aui:field-wrapper label="site-template-settings">
						<aui:input disabled="<%= true %>" name="active" type="checkbox" value="<%= privateLayoutSetPrototype.isActive() %>" />
						<aui:input disabled="<%= true %>" name="template-allows-modification" type="checkbox" value="<%= layoutsUpdateable %>" />
					</aui:field-wrapper>
				</c:when>
				<c:otherwise>
					<liferay-ui:message arguments="<%= new Object[] {privateLayoutSetPrototype.getName(locale)} %>" key="this-site-was-cloned-from-template-x" />
				</c:otherwise>
			</c:choose>
		</aui:fieldset>
	</c:if>
</aui:fieldset>