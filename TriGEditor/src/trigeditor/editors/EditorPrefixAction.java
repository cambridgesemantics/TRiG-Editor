package trigeditor.editors;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * This class is responsible for adding the refactoring action to the toolbar for the TriGEditor.
 * 
 * @author Natasha
 *
 */
public class EditorPrefixAction implements IEditorActionDelegate{

	/**
	 * Creates a TriGEditor object in order to refactor prefixes when user performs the action
	 * by clicking the button in the editor's toolbar.
	 */
	@Override
	public void run(IAction arg0) {
		try {
			TriGEditor t = new TriGEditor();
			t.doRefactoring();
			//t.firePropertyChange(IEditorPart.PROP_DIRTY);
		} catch (CoreException | IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		
	}

	@Override
	public void setActiveEditor(IAction arg0, IEditorPart arg1) {
		
	}

}
