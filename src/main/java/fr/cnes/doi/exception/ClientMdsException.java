/*
 * Copyright (C) 2017-2018 Centre National d'Etudes Spatiales (CNES).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package fr.cnes.doi.exception;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

/**
 * Exception for Client Cross Cite.
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
public class ClientMdsException extends Exception {

    private static final long serialVersionUID = -5061913391706889102L;

    /**
     * Detail message.
     */
    private final String detailMessage;
    
    /**
     * HTTP status.
     */
    private final Status status;
    
    /**
     * Constructs a new exception with HTTP status as its detail message.
     * @param status HTTP status
     */
    public ClientMdsException(final Status status) {
        super();
        this.detailMessage = computeDetailMessage(status);
        this.status = status;
    }
    
    /**
     * Constructs a new exception with the specified HTTP status and detail message.
     * @param status HTTP message
     * @param message message
     */
    public ClientMdsException(final Status status, final String message) {
        super(message);
        this.detailMessage = computeDetailMessage(status);
        this.status = status;
    }    
    
    /**
     * Constructs a new exception with the specified detail cause.
     * @param status HTTP status
     * @param cause cause
     */
    public ClientMdsException(final Status status,final Throwable cause) {
        super(cause);
        this.detailMessage = computeDetailMessage(status);
        this.status = status;
    }    
    
    /**
     * Constructs a new exception with the specified detail HTTP status, message
     * and cause.
     * @param status HTTP status
     * @param message message
     * @param cause cause
     */
    public ClientMdsException(final Status status, final String message, final Throwable cause) {
        super(message, cause);
        this.detailMessage = computeDetailMessage(status);
        this.status = status;
    }
    
    /**
     * Constructs a new exception with the specified detail HTTP status, response
     * and cause.
     * @param status HTTP status
     * @param message message
     * @param responseEntity Representation of the response
     * @param cause cause
     */
    public ClientMdsException(final Status status, final String message, 
            final Representation responseEntity, final Throwable cause) {
       super(message, cause);
       this.status = status;
       String txt;
        try {
            txt = responseEntity.getText();
        } catch (IOException ex) {
            txt = computeDetailMessage(status);
        }
        this.detailMessage = txt;
    }
    
    /**
     * Returns the detail message according to the status.
     * @param status HTTP status
     * @return the detail message
     */
    private String computeDetailMessage(final Status status) {
        final String result;
        switch(status.getCode()) {
            case 201:
            case 200:
                result = "Operation successful";
                break;
            case 204:
                result = "no DOIs founds";
                break;
            case 400:
                result = "invalid XML, wrong prefix or request body must be exactly two lines: "
                        + "DOI and URL; wrong domain, wrong prefix";
                break;
            case 401:
                result = "no login";
                break;
            case 403:
                result = "login problem, quota exceeded or dataset belongs to another party ";
                break;
            case 404:
                result = "DOI does not exist in our database";
                break;
            case 410:
                result = "the requested dataset was marked inactive (using DELETE method)";
                break;
            case 412:
                result = "metadata must be uploaded first";
                break;
            default:
                result = "Internal error";
                break;
        }
        return result;
    } 
    
    /**
     * Returns the status.
     * @return the status
     */
    public Status getStatus() {
        return this.status;
    }
    
    /**
     * Returns detail message;
     * @return the detail message
     */
    public String getDetailMessage() {
        return this.detailMessage;
    }    
}
