/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.hk2.xml.test.readonly;

import java.net.URL;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ReadOnlyTest {
    private final static String LIBRARY1_FILE = "library1.xml";
    
    private final static String LIBRARY_NAME = "Sesame Street Library";
    
    private final static String GEEK_SUBLIME_ISBN = "978-1-55597-685-9";
    private final static String GEEK_SUBLIME_NAME = "Geek Sublime";
    
    private final static String ESL_ISBN = "1-592-40087-6";
    private final static String ESL_NAME = "Eats, Shoots and Leaves";
    
    private final static String ALIEN_NAME = "Alien";
    private final static String HANNA_NAME = "Hannah and her Sisters";
    private final static String WOODS_NAME = "Into the Woods";
    
    private final static String ADDRESS_LINE_1 = "123 Sesame Street";
    private final static String ADDRESS_LINE_2 = "CO Grover";
    private final static String TOWN = "New York";
    private final static String STATE = "NY";
    private final static int ZIP = 10128;
    
    private final static String SCIENTIFIC_AMERICAN = "Scientific American";
    private final static String GAME_INFORMER = "Game Informer";
    
    /**
     * Tests that we can add a read-only bean
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testReadOnlyBeans() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(LIBRARY1_FILE);
        
        XmlRootHandle<LibraryBean> rootHandle = xmlService.unmarshal(url.toURI(), LibraryBean.class);
        LibraryBean library = rootHandle.getRoot();
        
        Assert.assertEquals(LIBRARY_NAME, library.getName());
        
        {
            BookBean geekSublimeBook = library.getBooks().get(0);
            Assert.assertEquals(GEEK_SUBLIME_ISBN, geekSublimeBook.getISBN());
            Assert.assertEquals(GEEK_SUBLIME_NAME, geekSublimeBook.getName());
        }
        
        {
            BookBean eslBook = library.getBooks().get(1);
            Assert.assertEquals(ESL_ISBN, eslBook.getISBN());
            Assert.assertEquals(ESL_NAME, eslBook.getName());
        }
        
        Assert.assertEquals(ALIEN_NAME, library.getMovies().get(0).getName());
        Assert.assertEquals(HANNA_NAME, library.getMovies().get(1).getName());
        Assert.assertEquals(WOODS_NAME, library.getMovies().get(2).getName());
        
        Assert.assertEquals(SCIENTIFIC_AMERICAN, library.getMagazines()[0].getName());
        Assert.assertEquals(GAME_INFORMER, library.getMagazines()[1].getName());
        Assert.assertEquals(2, library.getMagazines().length);
        
        AddressBean address = library.getAddress();
        Assert.assertNotNull(address);
        
        Assert.assertEquals(ADDRESS_LINE_1, address.getStreetAddress1());
        Assert.assertEquals(ADDRESS_LINE_2, address.getStreetAddress2());
        Assert.assertEquals(TOWN, address.getTown());
        Assert.assertEquals(STATE, address.getState());
        Assert.assertEquals(ZIP, address.getZipCode());
    }

}
