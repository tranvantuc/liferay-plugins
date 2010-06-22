/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.opensocial.portlet;

import com.liferay.opensocial.model.Gadget;
import com.liferay.opensocial.service.GadgetLocalServiceUtil;
import com.liferay.opensocial.shindig.util.ShindigUtil;
import com.liferay.opensocial.util.WebKeys;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.expando.NoSuchColumnException;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * <a href="GadgetPortlet.java.html"><b><i>View Source</i></b></a>
 *
 * @author Michael Young
 */
public class GadgetPortlet extends MVCPortlet {

	public static final String PORTLET_NAME_PREFIX = "OPENSOCIAL_";

	public void init(PortletConfig portletConfig) throws PortletException {
		super.init(portletConfig);

		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)portletConfig;

		Portlet portlet = liferayPortletConfig.getPortlet();

		try {
			checkExpando(portlet);
		}
		catch (Exception e) {
			throw new PortletException(e);
		}
	}

	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			Gadget gadget = getGadget();

			renderRequest.setAttribute(WebKeys.GADGET, gadget);

			super.render(renderRequest, renderResponse);
		}
		catch (IOException ioe) {
			throw ioe;
		}
		catch (PortletException pe) {
			throw pe;
		}
		catch (Exception e) {
			throw new PortletException(e);
		}
	}

	protected void checkExpando(Portlet portlet) throws Exception {
		long companyId = portlet.getCompanyId();

		if (companyId == 0) {
			return;
		}

		ExpandoTable expandoTable = ExpandoTableLocalServiceUtil.getTable(
			companyId, User.class.getName(), ShindigUtil.getTableOpenSocial());

		String namespace = PortalUtil.getPortletNamespace(
			portlet.getPortletId());

		String columnName = ShindigUtil.getColumnUserPrefs(namespace);

		try {
			ExpandoColumnLocalServiceUtil.getColumn(
				expandoTable.getTableId(), columnName);
		}
		catch (NoSuchColumnException nsce) {
			ExpandoColumnLocalServiceUtil.addColumn(
				expandoTable.getTableId(), columnName,
				ExpandoColumnConstants.STRING);
		}
	}

	protected Gadget getGadget() throws Exception {
		String portletName = getPortletConfig().getPortletName();

		int pos = portletName.indexOf(
			StringPool.UNDERLINE, PORTLET_NAME_PREFIX.length());

		long gadgetId = GetterUtil.getLong(portletName.substring(pos + 1));

		Gadget gadget = GadgetLocalServiceUtil.getGadget(gadgetId);

		return gadget;
	}

}