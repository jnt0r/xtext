/**
 * Copyright (c) 2013, 2016 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.resource.uriHell;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.resource.uriHell.AbstractURIHandlerWithEcoreTest;
import org.eclipse.xtext.resource.uriHell.UnexpectedResourcesException;
import org.junit.Test;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
@SuppressWarnings("all")
public class FilesInModelAndSourceMavenDifferentProjectsTest extends AbstractURIHandlerWithEcoreTest {
  @Override
  public URI getResourceURI() {
    return URI.createURI("platform:/resource/projectName/model/First.ecore");
  }
  
  @Override
  public URI getPackagedResourceURI() {
    return URI.createURI("platform:/resource/projectName/model/First.ecore");
  }
  
  @Override
  public URI getReferencedURI() {
    return URI.createURI("platform:/resource/other/src/main/org/package/Second.ecore");
  }
  
  @Override
  public URI getPackagedReferencedURI() {
    return URI.createURI("platform:/resource/other/org/package/Second.ecore");
  }
  
  @Test(expected = UnexpectedResourcesException.class)
  @Override
  public void testLoadResourceWithPackagedURIs() {
    super.testLoadResourceWithPackagedURIs();
  }
}
