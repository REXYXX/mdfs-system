package info.nemoworks.mdfs.client.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    @Value(value = "${server.port}")
    private Integer location;

    public String getLocation() {
        return location.toString();
    }
}
