/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cnes.doi.resource.mds;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Jean-Christophe Malapert (jean-christophe.malapert@cnes.fr)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({fr.cnes.doi.resource.mds.DoisResourceTest.class, fr.cnes.doi.resource.mds.MetadataResourceTest.class, fr.cnes.doi.resource.mds.DoiResourceTest.class, fr.cnes.doi.resource.mds.MediaResourceTest.class, fr.cnes.doi.resource.mds.MetadatasResourceTest.class})
public class MdsSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}