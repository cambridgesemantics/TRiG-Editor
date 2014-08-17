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

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;

public class BasicTriGEditorActionContributor extends BasicTextEditorActionContributor {

	public void setActiveEditor(IEditorPart part){
		super.setActiveEditor(part);
	
		if(!(part instanceof ITextEditor)){
			return;
		}
		IActionBars actionBars = getActionBars();
		if(actionBars == null){
			return;
		}

	}
}
