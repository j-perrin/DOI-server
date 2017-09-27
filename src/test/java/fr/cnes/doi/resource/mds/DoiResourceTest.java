/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnes.doi.resource.mds;

import fr.cnes.doi.InitServerForTest;
import fr.cnes.doi.settings.Consts;
import fr.cnes.doi.settings.DoiSettings;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
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
 *
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
public class DoiResourceTest {
    
    public static final String DOI = "10.5072/828606/8c3e91ad45ca855b477126bc073ae44b";
    private static Client cl;
    
    public DoiResourceTest() {
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
     * Test of getDoi method through a HTTP server, of class DoiResource.
     * A Status.SUCCESS_OK is expected and the response <i>https://cfosat.cnes.fr/</i>
     * @throws java.io.IOException - if OutOfMemoryErrors
     */
    @Test
    public void testGetDoiHttp() throws IOException {
        System.out.println("getDoi http");
        String port = DoiSettings.getInstance().getString(Consts.SERVER_HTTP_PORT);
        ClientResource client = new ClientResource("http://localhost:"+port+"/mds/dois/"+DOI);
        Representation rep = client.get();
        Status status = client.getStatus();
        assertEquals(status.getCode(), Status.SUCCESS_OK.getCode());
        assertEquals("Test the landing page is the right one","https://cfosat.cnes.fr/", rep.getText());
    }
    
    /**
     * Test of getDoi method through a HTTP server, of class DoiResource.
     * A Status.SUCCESS_OK is expected and the response <i>https://cfosat.cnes.fr/</i>
     * @throws java.io.IOException - if OutOfMemoryErrors
     */
    @Test
    public void testGetDoiHttps() throws IOException {
        System.out.println("getDoi https");
        String port = DoiSettings.getInstance().getString(Consts.SERVER_HTTPS_PORT);
        ClientResource client = new ClientResource("https://localhost:"+port+"/mds/dois/"+DOI);
        client.setNext(cl);
        Representation rep = client.get();
        Status status = client.getStatus();
        assertEquals(status.getCode(), Status.SUCCESS_OK.getCode());
        assertEquals("Test the landing page is the right one","https://cfosat.cnes.fr/", rep.getText());
    }    
    
    /**
     * Test of getDoi method when the DOI is not found through a HTTPS server, of class DoiResource.
     * A Status.CLIENT_ERROR_NOT_FOUND is expected because the 
     * DOI does not exist.
     * @throws java.io.IOException - if OutOfMemoryErrors
     */
    @Test
    public void testGetDoiNotFoundHttps() throws IOException {        
        System.out.println("getDoi not found https");      
        String port = DoiSettings.getInstance().getString(Consts.SERVER_HTTPS_PORT);
        ClientResource client = new ClientResource("https://localhost:"+port+"/mds/dois/10.5072/828606");
        client.setNext(cl);
        int code;
        try {
            Representation rep = client.get();
            code = client.getStatus().getCode();
        } catch (ResourceException ex) {
            code = ex.getStatus().getCode();
        } 
        client.release();
        assertEquals(code, Status.CLIENT_ERROR_NOT_FOUND.getCode());
    }        
    
//    /**
//     * Test of getDoi method when the DOI prefix is not the right one through a HTTPS server, of class DoiResource.
//     * @throws java.io.IOException
//     */
//    @Test
//    @Ignore
//    public void testGetDoiNotAllowedHttps() throws IOException {        
//        System.out.println("getDoi not allowed https");      
//        String port = DoiSettings.getInstance().getString(Consts.SERVER_HTTPS_PORT);
//        ClientResource client = new ClientResource("https://localhost:"+port+"/mds/dois/10.4072/828606");
//        client.setNext(cl);
//        int code;
//        try {
//            Representation rep = client.get();
//            code = client.getStatus().getCode();
//        } catch (ResourceException ex) {   
//            code = ex.getStatus().getCode();
//        }
//        
//        assertEquals(code, Status.CLIENT_ERROR_BAD_REQUEST.getCode());
//    }     
    
}