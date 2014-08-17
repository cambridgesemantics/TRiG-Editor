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
