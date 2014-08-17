package trigeditor.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.openrdf.query.algebra.Regex;

//TODO: characters can be anything to be part of word-- handle this?

public class TriGScanner extends RuleBasedScanner {
	
	private final TriGWhitespaceDetector white = new TriGWhitespaceDetector();
	private final String alphanumRegex = "^[a-zA-Z0-9_]*$";
	private final String hexRegex = "^[a-fA-F0-9]$";
	private final String iriRegex = "^[a-zA-Z0-9?:#$!@&*/\\.\\-_%!',`]$";
	private final String nameRegex = "^[a-zA-Z0-9_\\-]$";
	private final Map<IToken, String> tokens = new HashMap<IToken, String>();

	public TriGScanner(ColorManager manager) {
		
		WordRule w = new WordRule(new IWordDetector() {

			
			@Override
			public boolean isWordPart(char c) {
				return (Character.isJavaIdentifierPart(c));
			}

			@Override
			public boolean isWordStart(char c) {
				return (Character.isJavaIdentifierStart(c) || c == '@' || c == ':');
			}
			
		});
		
		IToken commentToken =
			new Token(
				new TextAttribute(
					manager.getColor(ITriGColorConstants.TRIG_COMMENT)));
		
		IToken URIToken =
				new Token(
					new TextAttribute(
						manager.getColor(ITriGColorConstants.URI_REF)));
		
		IToken URI2Token =
				new Token("");
		
		IToken prefixToken =
				new Token(
					new TextAttribute(
						manager.getColor(ITriGColorConstants.PREFIX_BASE_TAG), manager.getColor(new RGB(255,255,255)), SWT.BOLD));
		
		IToken blankNodeToken =
				new Token(
						new TextAttribute(
								manager.getColor(ITriGColorConstants.BLANK_NODE)));
		
		IToken literalToken =
				new Token(
						new TextAttribute(
								manager.getColor(ITriGColorConstants.LITERAL)));
		
		IToken literalBooleanToken =
				new Token(
						new TextAttribute(
								manager.getColor(ITriGColorConstants.LITERAL), manager.getColor(new RGB(255,255,255)), SWT.BOLD));
		
		IToken nameToken =
				new Token(
						new TextAttribute(
								manager.getColor(ITriGColorConstants.IRI_NAME)));
		
		IToken defaultToken =
				new Token(
						new TextAttribute(
								manager.getColor(ITriGColorConstants.DEFAULT)));
		
		IToken tag = new Token("");

		IRule[] rules = new IRule[14];
		
		//URI REFS
		rules[0] = new MultiLineRule("<", ">", URIToken);
		//tokens.put(URIToken, "URI");
		rules[13] = new TagRule(tag);
		//COMMENTS
		
		rules[1] = new EndOfLineRule("#", commentToken);
			
		//PREFIX/BASE TAGS
		w.addWord("@prefix", prefixToken);
		w.addWord("@base", prefixToken);
		w.addWord(":", defaultToken);
		rules[2] = w;
		
		
		//BLANK NODES
		rules[3] = new WordPatternRule(new IWordDetector(){

			@Override
			public boolean isWordPart(char c) {
				String charStr = "" + c;
				return (charStr.matches(iriRegex) || c == '.');
			}

			@Override
			public boolean isWordStart(char c) {
				return (Character.isJavaIdentifierStart(c) || c == ':' || c == '_');
			}
			
		},"_:", "", blankNodeToken);		
		rules[4] = new WordPatternRule(new IWordDetector(){

			@Override
			public boolean isWordPart(char c) {
				String regex = "[ ]+";
				String schar = "" + c;
				return schar.matches(regex) || c == ']';
			}

			@Override
			public boolean isWordStart(char c) {
				return c == '[';
			}
			
		}, "[", "]", blankNodeToken);
		
		//STRINGLITERALS --SINGLE AND MULTI LINE
		rules[5] = new SingleLineRule("\"", "\"", literalToken);
		rules[6] = new MultiLineRule("\"\"\"", "\"\"\"", literalToken);
		rules[7] = new SingleLineRule("'", "'", literalToken);
		rules[8] = new MultiLineRule("'''", "'''", literalToken);
		w.addWord("true", literalBooleanToken);
		w.addWord("false", literalBooleanToken);
		rules[9] = new WordPatternRule(new IWordDetector(){
			
			@Override
			public boolean isWordPart(char c) {
				String schar = "" + c;
				return schar.matches(alphanumRegex) || c == '-';
			}

			@Override
			public boolean isWordStart(char c) {
				return c == '@';
			}
			
		}, "@", "", literalToken);
		
		rules[10] = new WordPatternRule(new IWordDetector(){
			
			@Override
			public boolean isWordPart(char c) {
				String schar = "" + c;
				return schar.matches(iriRegex) || c == '^' || c == '<' || c == '>';
			}

			@Override
			public boolean isWordStart(char c) {
				return c == '^';
			}
			
		}, "^^", "", literalToken);
		
		rules[11] = new WordPatternRule(new IWordDetector(){

			@Override
			public boolean isWordPart(char c) {
				String schar = "" + c;
				return schar.matches(nameRegex);
			}

			@Override
			public boolean isWordStart(char c) {
				return c == ':';
			}
			
		}, ":", "", nameToken); 
		
		//WHITESPACE
		rules[12] = new WhitespaceRule(new TriGWhitespaceDetector());

		setRules(rules);
	}
}
