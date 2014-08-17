/***********************************************************************
 * Copyright (c) 2014 Cambridge Semantics Incorporated.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Cambridge Semantics Incorporated - initial API and implementation
 ***********************************************************************/

package trigeditor.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

public class TriGTextFileDocumentProvider extends TextFileDocumentProvider  {
	
	protected FileInfo createFileInfo(Object element) throws CoreException{
		FileInfo info = super.createFileInfo(element);
		if(info == null){
			info = createEmptyFileInfo();
		}
		IDocument document = info.fTextFileBuffer.getDocument();
		if(document != null){
			IDocumentPartitioner partitioner =
					new FastPartitioner(
						new XMLPartitionScanner(),
						new String[] {
							XMLPartitionScanner.XML_TAG,
							XMLPartitionScanner.XML_COMMENT });
				partitioner.connect(document);
				document.setDocumentPartitioner(partitioner);
		}
		else{
			createEmptyFileInfo();
		}
		
		return info;
	}

}
