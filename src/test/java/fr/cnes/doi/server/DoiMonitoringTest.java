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
package fr.cnes.doi.server;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.restlet.data.Method;

import fr.cnes.doi.services.DoiMonitoring;

/**
 *
 * @author malapert
 */
public class DoiMonitoringTest {
    
    private DoiMonitoring instance;
    
    public DoiMonitoringTest() {
        instance = new DoiMonitoring();
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of register method, of class DoiMonitoring.
     */
    @Test
    public void testRegister() {
        System.out.println("register");
        Method name = Method.GET;
        String path = "/test";
        String description = "myTest";        
        instance.register(name, path, description);
        assertTrue(true);
    }

    /**
     * Test of addMeasurement method, of class DoiMonitoring.
     */
    @Test
    public void testAddMeasurement() {
        System.out.println("addMeasurement");
        Method name = Method.GET;
        String path = "/test";
        float duration = 10.0F;
        instance.addMeasurement(name, path, duration);
        assertTrue(true);
    }

    /**
     * Test of isRegistered method, of class DoiMonitoring.
     */
    @Test
    public void testIsRegistered() {
        System.out.println("isRegistered");
        Method name = Method.POST;
        String path = "/registered";
        instance.register(name, path, "my description");
        boolean expResult = true;
        boolean result = instance.isRegistered(name, path);
        assertEquals(expResult, result);
    }

    /**
     * Test of getCurrentMean method, of class DoiMonitoring.
     */
    @Test
    public void testGetCurrentMean() {
        System.out.println("getCurrentMean");
        Method name = Method.POST;
        String path = "/mean";
        float expResult = 9.0F;
        instance.register(name, path, "test");
        instance.addMeasurement(name, path, 10);
        instance.addMeasurement(name, path, 8);
        float result = instance.getCurrentAverage(name, path);
        assertEquals(expResult, result, 0.001);
    }

    /**
     * Test of getDescription method, of class DoiMonitoring.
     */
    @Test
    public void testGetDescription() {
        System.out.println("getDescription");
        Method name = Method.HEAD;
        String path = "/test";
        instance.register(name, path, "description");
        String expResult = "description";
        String result = instance.getDescription(name, path);
        assertEquals(expResult, result);
    }
    
}
