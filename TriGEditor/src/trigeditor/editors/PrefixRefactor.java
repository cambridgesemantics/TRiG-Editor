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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

/**
 * The PrefixRefactor Object handles prefix refactoring for TriG files by the TriGEditor.
 * 
 * Prefix refactoring requires parsing the file for URIs and prefixes. URIs that appear
 * frequently and haven't been matched to a prefix yet are refactored. After generating
 * a prefix name, the prefix name and matching URI are added to the list of prefixes, and
 * all occurrences of the URI are replaced by the prefix name.
 * 
 * @author Natasha
 *
 */
public class PrefixRefactor {
	
	//Data structures to keep track of the URIs and/or matched prefixes in a file
	private Map<String, Integer> urisFrequency = new HashMap<String, Integer>();
	private Map<String, List<String>> urisPrefixes = new HashMap<String, List<String>>();
	
	/**
	 * Empty constructor of a PrefixRefactor object
	 */
	public PrefixRefactor(){
		
	}

	/**
	 * Helper function to refactor prefixes.
	 * 
	 * Creates the String prefix name from a URI, passed in as a parameter. The prefix name
	 * produced is no longer than 8 alpha-characters. If the URI has capital letters, those 
	 * characters are used to form the prefix name. Otherwise, the characters before the last '#'
	 * or after the last '/' are used to form the prefix name. The prefix name formed is lowercase.
	 * If a prefix name cannot be formed, "error" is returned.
	 * 
	 * Based off of the .net function that creates a prefix from a given URI.
	 * 
	 * @param namespace String representing the URI whose prefix name will be created
	 * @return prefix name representing the URI
	 */
	private String prefixFromNamespace(String namespace){
		String prefix = "";
		final String alphanumRegex = "^[a-zA-Z_]*$";
		final String alphaRegex = "^[a-zA-Z]*$";
		
		int start = -1;
		int end = -1;
		
		for(int i = namespace.length() - 1; i > 0; i--){
			char c = namespace.charAt(i);
			String cstr = "" + c;
			if(c == '/' | c == '#' | c == ':'){
				if(end != -1 && start != -1){
					break;
				}
				end = -1;
				start = -1;
			}
			
			if(end == -1){
				if(cstr.matches(alphanumRegex)){
					end = i;
				}
			}
			else{
				if(cstr.matches(alphaRegex) || start != -1){
					start = i;
				}
				else{
					start = -1;
				}
			}
		}
		
		//guarantees that the prefix name will only contain letters
		if(end != -1 && start != -1){
			for(int i = start; i < end + 1; i++){
				String charStr = "" + namespace.charAt(i);
				if(charStr.matches(alphaRegex)){
					prefix = prefix.concat("" + namespace.charAt(i));
				}
			}

			//guarantees the prefix name isn't longer than 8 characters
			if(prefix.length() > 8){
				StringBuilder sb = new StringBuilder();
				sb.append(prefix.charAt(0));
				for(int j = 1; j < prefix.length(); j++){
					if(Character.isUpperCase(prefix.charAt(j))){
						sb.append(prefix.charAt(j));
					}
				}
				if(sb.length() > 1){
					prefix = sb.toString();
				}
				else{
					prefix = prefix.substring(0, 8);
				}
			}
			
			//guarantees unique prefix names are used for different URIs
			if(urisPrefixes.containsValue(prefix)){
				prefix = prefix.concat("Two");
			}
			return prefix.toLowerCase();
		}
		
		return "error";
	}
	
	/**
	 * Helper function to refactor prefixes.
	 * 
	 * Creates a map that maps URIs parsed to the number of times each URI appears. Parsing is based
	 * off of how URIs can appear in a TriG file. This finds all URIs that appear in high frequency
	 * in a file (more than 4 times).
	 * @param s String representing the contents being parsed to find URIs. In most cases, the 
	 * string representation of a TriG file.
	 * @return map containing all the URIs found and the number of times each appears, only if they
	 * appear more than 4 times.
	 * @throws IOException
	 */
	private Map<String, Integer> findURI(String s) throws IOException{
		boolean isURI = false;
		StringBuilder uri = new StringBuilder();
		String newURI = "";
		List<String> uriList = new ArrayList<String>();
		Map<String, Integer> uriFreq = new HashMap<String, Integer>();
		char[] chars = s.toCharArray();
		
		char c = chars[0];
		
		for(int i = 1; i < chars.length; i++){
			if(c == '<'){
				isURI = true;
				//don't append < character
				c = chars[i];
			}
			else if(isURI && c != '>'){
				//creates URI with text up to first instance of #
				if(c == '#'){
					uri.append((char)c);
					newURI = uri.toString();
					if(!uriFreq.containsKey(newURI)){
						uriFreq.put(newURI, 1);
					}
					else{
						uriFreq.put(newURI, uriFreq.get(newURI) + 1);
					}
					uriList.add(newURI);
					uri.setLength(0);
					isURI = false;
				}
				else{
					uri.append((char)c);
				}
				c = chars[i];
			}
			if(isURI && c == '>'){								
				isURI = false;
				newURI = uri.toString();
				if(!uriFreq.containsKey(newURI)){
					uriFreq.put(newURI, 1);
				}
				else{
					uriFreq.put(newURI, uriFreq.get(newURI) + 1);
				}
				uriList.add(newURI);
				uri.setLength(0);
				c = chars[i];
			}
			c = chars[i];
		}
		
		//holds URIs that appear at least 5 times.
		Map<String, Integer> uriHighFreq = new HashMap<String, Integer>();
		for(String str : uriFreq.keySet()){
			if(uriFreq.get(str) > 4){
				uriHighFreq.put(str, uriFreq.get(str));
			}
		}
		
		for(String uriName : uriHighFreq.keySet()){
			if(prefixMatchURI(s).containsKey(uriName)){
				uriHighFreq.remove(uriName);
			}
		}
		urisFrequency = uriHighFreq;
		return uriHighFreq;
	}
	
	
	/**
	 * Helper function to refactor prefixes.
	 * 
	 * Creates a map of all existing URIs to their matching prefix names in a string. This is used
	 * to ensure that a prefix name isn't generated for a URI that has already been matched.
	 * In addition, it ensures that the same prefix name isn't mapped to unique URIs. A URI
	 * can be matched to multiple prefix names (although not recommended).
	 * @param s String being parsed for prefixes and matching URIs. This String is generally the
	 * string representation of a file.
	 * @return a map that maps all URIs to the list of prefix names that match each URI.
	 */
	private Map<String, List<String>> prefixMatchURI(String s){
		Map<String, List<String>> uriMatchings = new HashMap<String, List<String>>();
		StringBuilder uri = new StringBuilder();
		String uriMatching = "";
		StringBuilder prefix = new StringBuilder();
		String prefixID = "";
		char[] chars = s.toCharArray();
		boolean isPrefix = false;
		boolean isPrefixID = false;
		
		int i = 0;
		while(i < chars.length - 1){
			if(i < chars.length - 8){
				if(chars[i] == '@' && chars[i+1] == 'p' && chars[i+2] == 'r' && chars[i+3] == 'e' &&
						chars[i+4] == 'f' && chars[i+5] == 'i' && chars[i+6] == 'x'){
					isPrefix = true;
					isPrefixID = true;
					i += 7;
				}
				else if(isPrefix){
					if(isPrefixID && chars[i] != ':'){
						while(i < chars.length - 1 && chars[i] != ':'){
							if(chars[i] != ' '){
								prefix.append(chars[i]);
							}
							i++;
						}
						prefixID = prefix.toString();
						
						isPrefixID = false;
						i++;
					}
					else{
						while(i < chars.length - 1 && chars[i] != '<'){
							i++;
						}
						if(chars[i] == '<'){
							i++;
							while(i < chars.length - 1 && (chars[i] != '#' && chars[i] != '>')){
								uri.append(chars[i]);
								i++;
							}
							if(chars[i] == '#' || chars[i] == '>'){
								uriMatching = uri.toString();
								if(uriMatchings.get(uriMatching) == null){
									uriMatchings.put(uriMatching, new ArrayList<String>());
								}
								uriMatchings.get(uriMatching).add(0, prefixID);
								uriMatchings.put("URI: " + uriMatching, uriMatchings.get(uriMatching));
								isPrefix = false;
								uriMatching = "";
								prefixID = "";
								uri.setLength(0);
								prefix.setLength(0);
							}
						}
					}
				}
				else{
					i++;
				}
			}
			else{
				i++;
			}			
			
		}

		urisPrefixes = uriMatchings;
		return uriMatchings;
	}
	
	/**
	 * Generates prefixes for URIs that appear in a string at least 5 times, given that they do
	 * not already have a prefix name. This map holds the new URIs and the respective prefix
	 * name generated for them. These will be added to the string during the refactoring.
	 * 
	 * @param s String from which the prefixes are generated. This String is generally the
	 * String representation of a file.
	 * @return map containing the URIs matching prefix names to be added to the file through
	 * the refactoring.
	 * @throws IOException
	 */
	private Map<String, String> generatePrefixes(String s) throws IOException{
		Map<String, String> prefixMatchings = new HashMap<String, String>();
		findURI(s);
		prefixMatchURI(s);
		for(String str : urisFrequency.keySet()){
			prefixMatchings.put(str, prefixFromNamespace(str));
		}
		
		return prefixMatchings;
	}
	
	/**
	 * Function that refactors prefixes. For URIs that haven't been matched to a prefix yet
	 * and appear in the file at least 5 times, this function adds the prefix name and URI 
	 * to the top of the list of prefixes in the file. In addition, all occurrences of the URI
	 * are replaced by the prefix name. If there is additional text in the URI, the text is 
	 * separated by ':' from the prefix name.
	 * 
	 * Additionally, the same URI should not appear multiple times in a triple statement.
	 * 
	 * @param input String representation of the file whose prefixes are being refactored.
	 * @param fileName String representation of the path of the file
	 * @throws IOException
	 */
	public void refactorPrefixes(String input, String filePath) throws IOException{
		File f = new File(filePath);
		
		//prevent concurrency issues: using different data structures to avoid access/changing at same time
		List<String> lines = FileUtils.readLines(f);
		List<String> tempLines = new ArrayList<String>();
		List<String> tempLinesRename = new ArrayList<String>();
		
		Map<String, String> tempPrefixes = new HashMap<String, String>();
		Map<String, String> tempURIRename = new HashMap<String, String>();
		tempPrefixes = generatePrefixes(input);
		tempURIRename = generatePrefixes(input);
		
		//Add prefix name to list of prefixes
		int index = 1;		
		for(String s : lines){
			tempLines.add(s);
			if(s.contains("@prefix") && !tempPrefixes.isEmpty()){
				for(String URI : tempPrefixes.keySet()){
					tempLines.add(index - 1, "@prefix " + tempPrefixes.get(URI) + "	:	" + "<" + URI + "> .");
					tempPrefixes.remove(URI);
				}
			}
			
			index++;
		}
		//case where there are no prefixes listed in the file
		if(!tempPrefixes.isEmpty()){
			for(String URI : tempPrefixes.keySet()){
				tempLines.add(0, "@prefix " + tempPrefixes.get(URI) + "	:	" + "<" + URI + "> .");
				tempPrefixes.remove(URI);
			}
		}
		
		
		//Replace URIs with prefix name in document
		int index2 = 0;
		for(String s : tempLines){
			tempLinesRename.add(s);
			for(String uri : tempURIRename.keySet()){
				String strAtLine = tempLinesRename.get(index2);
				if(strAtLine.contains(uri) && !strAtLine.contains("@prefix")){					
					int start = strAtLine.indexOf(uri) + uri.length();
					String strAtLineNew = "";
					if(start > 0){
						int i = start;
						String temp = strAtLine;
						if(strAtLine.charAt(start) == '#' || strAtLine.charAt(start) == '/'){
							temp = strAtLine.substring(0, start) + ":" + strAtLine.substring(start + 1);
						}
						while(temp.charAt(i) != '>'){
							i++;
						}
						
						if(i != temp.length() - 1){
							strAtLineNew = temp.substring(0, i) + temp.substring(i+1);
						}
						else{
							strAtLineNew = temp.substring(0, i);
						}
					}
					
					CharSequence target = "<" + uri;
					CharSequence literal = tempURIRename.get(uri);
					tempLinesRename.set(index2, strAtLineNew.replace(target, literal));
					
				}				
			}
			index2++;
		}
		
		
		FileUtils.writeLines(f, tempLinesRename);
	}

}
