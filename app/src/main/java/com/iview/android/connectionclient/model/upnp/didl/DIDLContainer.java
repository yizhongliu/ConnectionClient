/**
 * Copyright (C) 2013 Aurélien Chabot <aurelien@chabot.fr>
 * 
 * This file is part of DroidUPNP.
 * 
 * DroidUPNP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DroidUPNP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DroidUPNP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.iview.android.connectionclient.model.upnp.didl;

import com.iview.android.connectionclient.R;

import org.cybergarage.upnp.std.av.server.object.container.ContainerNode;

public class DIDLContainer extends DIDLObject implements IDIDLContainer {

	public DIDLContainer(ContainerNode item)
	{
		super(item);
	}

	public String getCount()
	{
		return "" + getChildCount();
	}

	@Override
	public int getIcon()
	{
		return R.drawable.ic_action_collection;
	}

	@Override
	public int getChildCount()
	{
		if (item == null || !(item instanceof ContainerNode))
			return 0;

		Integer i = ((ContainerNode) item).getChildCount();

		if (i == null)
			return 0;

		return i;
	}
}
