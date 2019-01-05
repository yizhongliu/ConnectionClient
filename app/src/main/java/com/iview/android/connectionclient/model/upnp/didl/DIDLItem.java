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

import android.util.Log;

import com.iview.android.connectionclient.R;

import org.cybergarage.upnp.std.av.server.object.ContentNode;
import org.cybergarage.upnp.std.av.server.object.item.ItemNode;

import java.util.List;

public class DIDLItem extends DIDLObject implements IDIDLItem {

	private static final String TAG = "DIDLItem";

	public DIDLItem(ContentNode item)
	{
		super(item);
	}

	@Override
	public int getIcon()
	{
		return R.drawable.ic_file;
	}

	@Override
	public String getURI()
	{

		if (item != null)
		{
            String res = ((ItemNode)item).getResource();
            String protocolInfo = ((ItemNode)item).getProtocolInfo();
			Log.e(TAG, "Item : res:" + res + ",protocolInfo:" + protocolInfo);
			return item.getValue();
		} else {
			Log.e(TAG, "item is null");
		}
		return null;
	}
}
