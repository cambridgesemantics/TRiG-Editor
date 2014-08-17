package trigeditor.editors;

import org.eclipse.swt.graphics.RGB;

/**
 * Represents the colors used for syntax highlighting in the TriGEditor.
 * 
 * @author Natasha
 *
 */
public interface ITriGColorConstants {
	RGB TRIG_COMMENT = new RGB(0, 175, 0);
	RGB URI_REF = new RGB(0, 0, 255);
	RGB PREFIX_BASE_TAG = new RGB(139, 0, 0);
	RGB BLANK_NODE = new RGB(200, 0, 0);
	RGB IRI_NAME = new RGB(0,0,100);
	RGB LITERAL = new RGB(100,0,100);
	
	RGB DEFAULT = new RGB(0, 0, 0);
}
