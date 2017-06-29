package org.openhab.binding.supla.internal.supla.api;

import org.openhab.binding.supla.internal.api.ChannelManager;
import org.openhab.binding.supla.internal.mappers.Mapper;
import org.openhab.binding.supla.internal.server.http.HttpExecutor;
import org.openhab.binding.supla.internal.server.http.Request;
import org.openhab.binding.supla.internal.server.http.Response;
import org.openhab.binding.supla.internal.supla.entities.SuplaChannel;
import org.openhab.binding.supla.internal.supla.entities.SuplaChannelStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class SuplaChannelManager implements ChannelManager {
    private final Logger logger = LoggerFactory.getLogger(SuplaChannelManager.class);

    private final HttpExecutor httpExecutor;
    private final Mapper mapper;

    public SuplaChannelManager(HttpExecutor httpExecutor, Mapper mapper) {
        this.httpExecutor = checkNotNull(httpExecutor);
        this.mapper = checkNotNull(mapper);
    }

    @Override
    public void turnOn(SuplaChannel channel) {
        logger.warn("turnOn({}) not implemented!", channel);
    }

    @Override
    public void turnOff(SuplaChannel channel) {
        logger.warn("turnOff({}) not implemented!", channel);
    }

    @Override
    public Optional<SuplaChannelStatus> obtainChannelStatus(SuplaChannel channel) {
        final Response response = httpExecutor.get(new Request("/channels/" + channel.getId()));
        if (response.success()) {
            return Optional.of(mapper.to(SuplaChannelStatus.class, response.getResponse()));
        } else {
            return Optional.empty();
        }
    }
}
