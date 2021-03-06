/*
 * Copyright (C) 2018 Centre National d'Etudes Spatiales (CNES).
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
package fr.cnes.doi.resource.admin;

import fr.cnes.doi.InitServerForTest;
import fr.cnes.doi.settings.Consts;
import fr.cnes.doi.settings.DoiSettings;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

/**
 * Tests services from the Administration application.
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
public class ServicesTest {
    
    private static Client cl;

    public ServicesTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        InitServerForTest.init();
        cl = new Client(new Context(), Protocol.HTTPS);
        Series<Parameter> parameters = cl.getContext().getParameters();
        parameters.add("truststorePath", "jks/doiServerKey.jks");
        parameters.add("truststorePassword", DoiSettings.getInstance().getSecret(Consts.SERVER_HTTPS_TRUST_STORE_PASSWD));
        parameters.add("truststoreType", "JKS");
    }

    @AfterClass
    public static void tearDownClass() {
        InitServerForTest.close();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getStatus service through a HTTPS server, of class AdministrationApplication.
     */
    @Test
    public void getStatus()  {
        System.out.println("getStatus service through a HTTPS server");
        String port = DoiSettings.getInstance().getString(Consts.SERVER_HTTPS_PORT);
        ClientResource client = new ClientResource("https://localhost:"+port+"/status");        
        client.setNext(cl);    
        Status status;
        try {
            Representation rep = client.get();
            status = client.getStatus();
        } catch(ResourceException ex) {
            status = ex.getStatus();
        }
        client.release();
        assertEquals("Test of status service",Status.SUCCESS_OK.getCode(), status.getCode());
    }    
    
    /**
     * Test of createToken method, of class TokenResource.
     */
    @Test
    @Ignore
    public void getStats()  {
        System.out.println("getStats service through a HTTPS server");
        String port = DoiSettings.getInstance().getString(Consts.SERVER_HTTPS_PORT);
        ClientResource client = new ClientResource("https://localhost:"+port+"/stats");        
        client.setNext(cl);    
        Status status;
        try {
            Representation rep = client.get();
            status = client.getStatus();
        } catch(ResourceException ex) {
            status = ex.getStatus();
        }
        client.release();
        assertEquals("Test of stats service",Status.SUCCESS_OK.getCode(), status.getCode());
    }      
    
}
