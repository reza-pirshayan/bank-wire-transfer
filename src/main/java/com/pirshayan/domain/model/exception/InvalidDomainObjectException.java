package com.pirshayan.domain.model.exception;

public class InvalidDomainObjectException extends GeneralException {
    private static final long serialVersionUID = 1L;
	private final String attributeName;
    private final String className;

    public InvalidDomainObjectException(String message, String className, String attributeName) {
        super("100001", message);
        this.className = className;
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getClassName() {
    	
        return className;
    }

    @Override
    public String getErrorDetails() {
        return String.format("validation for %s.%s failed",getAttributeName(), getClassName());
    }
}
