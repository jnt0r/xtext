/**
 * Copyright (c) 2016, 2020 itemis AG (http://www.itemis.eu) and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.xtext.ui.tests.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.XtextSourceViewer;
import org.eclipse.xtext.ui.refactoring.ui.SyncUtil;
import org.eclipse.xtext.ui.testing.AbstractEditorTest;
import org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Jan Koehnlein - Initial contribution and API
 */
public class DirtyStateEditorSupportIntegrationTest extends AbstractEditorTest {
	@Inject
	private SyncUtil syncUtil;

	private XtextEditor editor;

	private List<IResourceDescription.Event> events;

	private Display myDisplay;

	private StyledText styledText;

	@Before
	public void setUpEditor() throws Exception {
		IResourceServiceProvider rsp = IResourceServiceProvider.Registry.INSTANCE
				.getResourceServiceProvider(URI.createURI("dummy.testlanguage"));
		rsp.get(Injector.class).injectMembers(this);
		IFile file = IResourcesSetupUtil.createFile("test/foo.testlanguage", "stuff foo");
		editor = openEditor(file);
		syncUtil.yieldToQueuedDisplayJobs(new NullProgressMonitor());
		editor.getSite().getPage().activate(editor);
		syncUtil.yieldToQueuedDisplayJobs(new NullProgressMonitor());
		events = new ArrayList<>();
		editor.getDirtyStateEditorSupport().getDirtyStateManager().addListener(e -> events.add(e));
		myDisplay = editor.getSite().getShell().getDisplay();
		myDisplay.getShells()[0].forceActive();
		syncUtil.yieldToQueuedDisplayJobs(new NullProgressMonitor());
		styledText = editor.getInternalSourceViewer().getTextWidget();
		styledText.setCaretOffset(9);
		styledText.setFocus();
		syncUtil.waitForReconciler(editor);
		syncUtil.yieldToQueuedDisplayJobs(new NullProgressMonitor());
		Thread.sleep(20);
		Assert.assertTrue(events.isEmpty());
	}

	/**
	 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=488380
	 */
	@Test
	public void testSaveAndEdit() throws Exception {
		pushKey('a', 0);
		Assert.assertEquals(1, events.size());
		Assert.assertEquals("fooa", getFirstExportedObjectInLastEventDelta().getQualifiedName().getLastSegment());
		editor.doSave(new NullProgressMonitor());
		syncUtil.yieldToQueuedDisplayJobs(new NullProgressMonitor());
		Assert.assertEquals(2, events.size());
		Assert.assertNull(Iterables.getLast(events).getDeltas().get(0).getNew());
		pushKey((char) 0, SWT.BS);
		Assert.assertEquals(3, events.size());
		Assert.assertEquals("foo", getFirstExportedObjectInLastEventDelta()
				.getQualifiedName().getLastSegment());
	}

	/**
	 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=354123
	 */
	@Test
	public void testUndoRedo() throws Exception {
		pushKey('a', 0);
		Assert.assertEquals(1, events.size());
		Assert.assertEquals("fooa", getFirstExportedObjectInLastEventDelta()
				.getQualifiedName().getLastSegment());
		IUndoManager undoManager = ((XtextSourceViewer) editor.getInternalSourceViewer()).getUndoManager();
		undoManager.undo();
		syncUtil.yieldToQueuedDisplayJobs(new NullProgressMonitor());
		syncUtil.waitForReconciler(editor);
		Assert.assertEquals(2, events.size());
		Assert.assertNull(Iterables.getLast(events).getDeltas().get(0).getNew());
		undoManager.redo();
		syncUtil.yieldToQueuedDisplayJobs(new NullProgressMonitor());
		syncUtil.waitForReconciler(editor);
		Assert.assertEquals(3, events.size());
		Assert.assertEquals("fooa", getFirstExportedObjectInLastEventDelta()
				.getQualifiedName().getLastSegment());
		Iterables.getLast(events);
	}

	private IEObjectDescription getFirstExportedObjectInLastEventDelta() {
		return Iterables.getFirst(Iterables.getLast(events).getDeltas().get(0).getNew().getExportedObjects(), null);
	}

	protected void pushKey(char c, int k) throws Exception {
		String textBefore = editor.getDocument().get();
		Event event = new Event();
		event.type = SWT.KeyDown;
		event.character = c;
		event.keyCode = k;
		myDisplay.post(event);
		syncUtil.yieldToQueuedDisplayJobs(new NullProgressMonitor());
		Event event2 = new Event();
		event2.type = SWT.KeyUp;
		event2.character = c;
		event2.keyCode = k;
		myDisplay.post(event2);
		int maxTries = 10;
		while (maxTries-- > 0) {
			if (!Objects.equal(editor.getDocument().get(), textBefore)) {
				syncUtil.waitForReconciler(editor);
				return;
			}
			Thread.sleep(10);
			syncUtil.yieldToQueuedDisplayJobs(new NullProgressMonitor());
		}
		Assert.fail("Document didn't change on keystroke");
	}

	@Override
	protected String getEditorId() {
		return "org.eclipse.xtext.ui.tests.TestLanguage";
	}
}
