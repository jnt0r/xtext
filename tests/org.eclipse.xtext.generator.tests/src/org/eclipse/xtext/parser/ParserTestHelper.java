/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.parser;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class ParserTestHelper {

	private final IResourceFactory factory;
	private final IParser parser;

	public ParserTestHelper(IResourceFactory factory, IParser parser) {
		this.factory = factory;
		this.parser = parser;
	}
	
	public XtextResource getResourceFromStream(InputStream in) throws IOException {
		XtextResourceSet rs = new XtextResourceSet();
		rs.setClasspathURIContext(getClass());
		URI uri = URI.createURI("mytestmodel." + factory.getModelFileExtensions()[0]);
		XtextResource resource = createResource(uri);
		rs.getResources().add(resource);
		resource.load(in, null);

		for (Diagnostic d : resource.getErrors())
			System.out.println("Resource Error: " + d);

		for (Diagnostic d : resource.getWarnings())
			System.out.println("Resource Warning: " + d);

		return resource;
	}

	public XtextResource createResource(URI uri) {
		XtextResource result = (XtextResource) factory.createResource(uri);
		result.setParser(parser);
		return result;
	}
}
