package io.pdfdata.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;

/**
 * Base class for {@link Entity entities} that refer to a binary {@link Resource} via a {@code
 * resource} property in the API response.
 * @publicapi
 */
public abstract class ResourcefulEntity extends Entity {
    private String resourceID;

    @JsonIgnore
    private Resource resource;

    /**
     * Returns the resource ID conveyed by the entity's {@code resource} property in a
     * successfully-completed {@link Proc} response.
     */
    @JsonProperty("resource")
    public String getResourceID() {
        return resourceID;
    }

    /**
     * Returns this entity's {@link Resource}, which provides metadata related to the binary
     * resource data itself (as distinct from this entity's data), as well as
     * {@link Resource#get() a convenience method for easily retrieving said data}.
     */
    public Resource getResource () {
        return resource;
    }

    /**
     * @nodoc
     */
    public void registerResource (Operation.Result parentResult) throws IOException {
        if (resource == null) {
            resource = (Resource) parentResult.getResources().get(resourceID);

            if (resource == null) {
                throw new IOException("No resource available for result entity with resourceID " + resourceID);
            }
        }
    }
}
