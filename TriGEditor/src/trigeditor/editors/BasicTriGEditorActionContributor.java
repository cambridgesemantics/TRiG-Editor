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
