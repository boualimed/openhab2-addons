/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.mihome.handler;

import static org.openhab.binding.mihome.XiaomiGatewayBindingConstants.*;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

/**
 * Handles the Xiaomi aqara wall switch with two buttons
 *
 * @author Dieter Schmidt - Initial contribution
 */
public class XiaomiAqaraActorSwitch2Handler extends XiaomiActorBaseHandler {

    private final Logger logger = LoggerFactory.getLogger(XiaomiAqaraActorSwitch2Handler.class);

    private static final String CHANNEL_0 = "channel_0";
    private static final String CHANNEL_1 = "channel_1";
    private static final String ON = "on";

    public XiaomiAqaraActorSwitch2Handler(Thing thing) {
        super(thing);
    }

    @Override
    void execute(ChannelUID channelUID, Command command) {
        String status = command.toString().toLowerCase();
        switch (channelUID.getId()) {
            case CHANNEL_SWITCH_CH0:
                getXiaomiBridgeHandler().writeToDevice(getItemId(), new String[] { CHANNEL_0 },
                        new Object[] { status });
                return;
            case CHANNEL_SWITCH_CH1:
                getXiaomiBridgeHandler().writeToDevice(getItemId(), new String[] { CHANNEL_1 },
                        new Object[] { status });
                return;
        }
        // Only gets here, if no condition was met
        logger.error("Can't handle command {} on channel {}", command, channelUID);
    }

    @Override
    void parseReport(JsonObject data) {
        parseDefault(data);
    }

    @Override
    void parseHeartbeat(JsonObject data) {
        parseDefault(data);
    }

    @Override
    void parseReadAck(JsonObject data) {
        parseDefault(data);
    }

    @Override
    void parseWriteAck(JsonObject data) {
        parseDefault(data);
    }

    @Override
    void parseDefault(JsonObject data) {
        if (data.has(CHANNEL_0)) {
            boolean isOn = ON.equals(data.get(CHANNEL_0).getAsString().toLowerCase());
            updateState(CHANNEL_SWITCH_CH0, isOn ? OnOffType.ON : OnOffType.OFF);
        } else if (data.has(CHANNEL_1)) {
            boolean isOn = ON.equals(data.get(CHANNEL_1).getAsString().toLowerCase());
            updateState(CHANNEL_SWITCH_CH1, isOn ? OnOffType.ON : OnOffType.OFF);
        }
    }

}
