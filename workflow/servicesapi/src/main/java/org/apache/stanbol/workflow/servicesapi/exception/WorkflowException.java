package org.apache.stanbol.workflow.servicesapi.exception;

import org.apache.stanbol.enhancer.servicesapi.EnhancementException;
import org.apache.stanbol.workflow.servicesapi.WorkflowJobManager;

/**
 * <p>BaseException thrown by {@link WorkflowJobManager} implementations when encountering problems 
 * while executing a route</p>
 * 
 * @author Antonio David Perez Morales <adperezmorales@gmail.com>
 *
 */
public class WorkflowException extends EnhancementException {

    private static final long serialVersionUID = 1L;

    public WorkflowException(String message) {
        super(message);
    }
    public WorkflowException(String message, Throwable cause) {
        super(message,cause);
    }
}
