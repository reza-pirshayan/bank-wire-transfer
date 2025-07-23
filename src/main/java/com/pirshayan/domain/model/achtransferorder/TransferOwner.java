package com.pirshayan.domain.model.achtransferorder;

import java.util.Objects;

import com.pirshayan.domain.model.Validator;
import com.pirshayan.domain.model.exception.InvalidDomainObjectException;

/**
 * Represents the owner of the ACH transfer order.
 * 
 * This is a value object holding identity and name information.
 * It enforces domain constraints via validation and defines equality based on content.
 */
class TransferOwner {

    /**
     * Unique identifier for the transfer owner.
     * For example, it might refer to a user ID, company ID, or external system reference.
     */
    private final String id;

    /**
     * The full name of the transfer owner.
     */
    private final String name;

    /**
     * Constructs a validated and immutable {@code TransferOwner} instance.
     *
     * @param id    Unique identifier (not validated here).
     * @param name  Full name (validated).
     * @throws InvalidDomainObjectException if validation fails.
     */
    TransferOwner(String id, String name) throws InvalidDomainObjectException {
        this.id = id;
        this.name = Validator.validateTransferOwnerName(name);
    }

    /**
     * Returns the identifier of the transfer owner.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of the transfer owner.
     */
    public String getName() {
        return name;
    }

    /**
     * Two {@code TransferOwner} objects are equal if their IDs and names match.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        TransferOwner other = (TransferOwner) obj;
        return Objects.equals(name, other.name) && Objects.equals(id, other.id);
    }
}
