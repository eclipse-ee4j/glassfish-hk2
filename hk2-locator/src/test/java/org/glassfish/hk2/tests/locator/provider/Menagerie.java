/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.provider;

import javax.inject.Inject;
import javax.inject.Named;

import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceHandle;
import org.junit.Assert;

/**
 * @author jwells
 *
 */
public class Menagerie {
    @Inject
    private @Eagles IterableProvider<Character> allEagles;
    
    @Inject
    private IterableProvider<FootballCharacter> allNFLPlayers;
    
    @Inject
    private @Named(ProviderTest.QUEEQUEG) IterableProvider<BookCharacter> queequegProvider;
    
    @Inject
    private IterableProvider<BookCharacter> allBookCharacters;
    
    @Inject
    private IterableProvider<Character> allCharacters;
    
    public void validateAllEagles() {
        Assert.assertTrue(allEagles.getSize() == 1);
        Assert.assertEquals(ProviderTest.SHADY, allEagles.get().getName());
        Assert.assertEquals(ProviderTest.EAGLES, ((FootballCharacter) allEagles.getHandle().getService()).getTeam());
        
        IterableProvider<FootballCharacter> eagles = allNFLPlayers.qualifiedWith(new EaglesImpl());
        
        Assert.assertTrue(eagles.getSize() == 1);
        Assert.assertEquals(ProviderTest.SHADY, eagles.get().getName());
        Assert.assertEquals(ProviderTest.EAGLES, eagles.getHandle().getService().getTeam());
    }
    
    public void validateAllGiants() {
        Assert.assertTrue(allNFLPlayers.getSize() == 2);
        IterableProvider<FootballCharacter> giants = allNFLPlayers.qualifiedWith(new GiantsImpl());
        
        Assert.assertTrue(giants.getSize() == 1);
        Assert.assertEquals(ProviderTest.ELI, giants.get().getName());
        Assert.assertEquals(ProviderTest.GIANTS, giants.getHandle().getService().getTeam());
    }
    
    public void validateQueequeg() {
        boolean found = false;
        for (BookCharacter character : queequegProvider) {
            Assert.assertFalse(found);
            found = true;
            
            Assert.assertEquals(ProviderTest.QUEEQUEG, character.getName());
            Assert.assertEquals(ProviderTest.MOBY_DICK, character.getBook());
        }
        Assert.assertTrue(found);
        
        found = false;
        for (ServiceHandle<BookCharacter> character : queequegProvider.handleIterator()) {
            Assert.assertFalse(found);
            found = true;
            
            Assert.assertEquals(ProviderTest.QUEEQUEG, character.getService().getName());
            Assert.assertEquals(ProviderTest.MOBY_DICK, character.getService().getBook());
        }
    }
    
    public void validateBookCharacters() {
        Assert.assertEquals(2, allBookCharacters.getSize());
        
        Assert.assertEquals(2, allCharacters.ofType(BookCharacter.class).getSize());
    }
    
    public void validateAllCharacters() {
        Assert.assertEquals(4, allCharacters.getSize());
        
        boolean foundQueequeg = false;
        boolean foundIshmael = false;
        boolean foundShady = false;
        boolean foundEli = false;
        for (Character character : allCharacters) {
            if (ProviderTest.QUEEQUEG.equals(character.getName())) foundQueequeg = true;
            if (ProviderTest.ISHMAEL.equals(character.getName())) foundIshmael = true;
            if (ProviderTest.SHADY.equals(character.getName())) foundShady = true;
            if (ProviderTest.ELI.equals(character.getName())) foundEli = true;
        }
        
        Assert.assertTrue(foundQueequeg);
        Assert.assertTrue(foundIshmael);
        Assert.assertTrue(foundShady);
        Assert.assertTrue(foundEli);
        
        foundQueequeg = false;
        foundIshmael = false;
        foundShady = false;
        foundEli = false;
        for (ServiceHandle<Character> character : allCharacters.handleIterator()) {
            if (ProviderTest.QUEEQUEG.equals(character.getService().getName())) foundQueequeg = true;
            if (ProviderTest.ISHMAEL.equals(character.getService().getName())) foundIshmael = true;
            if (ProviderTest.SHADY.equals(character.getService().getName())) foundShady = true;
            if (ProviderTest.ELI.equals(character.getService().getName())) foundEli = true;
        }
        
        Assert.assertTrue(foundQueequeg);
        Assert.assertTrue(foundIshmael);
        Assert.assertTrue(foundShady);
        Assert.assertTrue(foundEli);
        
        Assert.assertEquals(ProviderTest.SHADY,
                allCharacters.named(ProviderTest.SHADY).get().getName());
        Assert.assertEquals(ProviderTest.ELI,
                allCharacters.named(ProviderTest.ELI).get().getName());
        Assert.assertEquals(ProviderTest.QUEEQUEG,
                allCharacters.named(ProviderTest.QUEEQUEG).get().getName());
        Assert.assertEquals(ProviderTest.ISHMAEL,
                allCharacters.named(ProviderTest.ISHMAEL).get().getName());
        
        Assert.assertEquals(ProviderTest.SHADY,
                allCharacters.named(ProviderTest.SHADY).getHandle().getService().getName());
        Assert.assertEquals(ProviderTest.ELI,
                allCharacters.named(ProviderTest.ELI).getHandle().getService().getName());
        Assert.assertEquals(ProviderTest.QUEEQUEG,
                allCharacters.named(ProviderTest.QUEEQUEG).getHandle().getService().getName());
        Assert.assertEquals(ProviderTest.ISHMAEL,
                allCharacters.named(ProviderTest.ISHMAEL).getHandle().getService().getName());
    }
    
    private static class EaglesImpl extends AnnotationLiteral<Eagles> implements Eagles {
        private static final long serialVersionUID = 5656136871421657809L;
        
    }
}
