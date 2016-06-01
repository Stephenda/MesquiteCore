/* Mesquite source code.  Copyright 1997 and onward, W. Maddison and D. Maddison. 


Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
 */
package mesquite.basic.MergeTaxa;

import java.util.*;
import java.awt.*;

import mesquite.lib.characters.*;
import mesquite.categ.lib.*;
import mesquite.lib.*;
import mesquite.lib.duties.*;
import mesquite.lib.table.*;


/* ======================================================================== */
public class MergeTaxa extends TaxonUtility {
//	int maxNameLength=30;
	boolean keepEntireName=true;
	static int USEFIRSTNAME = 0;
	static int KEEPENTIRE = 1;
	static int KEEPPARTIAL = 2;
	int keepMode=USEFIRSTNAME;
	int startLengthToKeep = 10;
	int endLengthToKeep = 4;
	boolean preferencesSet = false;
	boolean setMultiplestatesToUncertainty = false;
	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName){
		loadPreferences();
		return true;
	}
	/*.................................................................................................................*/
	public void processSingleXMLPreference (String tag, String content) {
		if ("startLengthToKeep".equalsIgnoreCase(tag)) {
			startLengthToKeep = MesquiteInteger.fromString(content);
		} else  if ("endLengthToKeep".equalsIgnoreCase(tag)) {
			endLengthToKeep = MesquiteInteger.fromString(content);
		} else  if ("keepEntireName".equalsIgnoreCase(tag)) {   // retain this for older installations before keepMode was added
			keepEntireName = MesquiteBoolean.fromOffOnString(content);
			if (keepEntireName)
				keepMode=KEEPENTIRE;
			else
				keepMode = KEEPPARTIAL;
		} else  if ("keepMode".equalsIgnoreCase(tag)) {
			keepMode = MesquiteInteger.fromString(content);
		} else  if ("setMultiplestatesToUncertainty".equalsIgnoreCase(tag)) {
			setMultiplestatesToUncertainty = MesquiteBoolean.fromOffOnString(content);
		}  

		preferencesSet = true;
	}
/*.................................................................................................................*/
public String preparePreferencesForXML () {
		StringBuffer buffer = new StringBuffer(200);
		StringUtil.appendXMLTag(buffer, 2, "startLengthToKeep", startLengthToKeep);  
		StringUtil.appendXMLTag(buffer, 2, "endLengthToKeep", endLengthToKeep);  
		StringUtil.appendXMLTag(buffer, 2, "keepMode", keepMode);  
		StringUtil.appendXMLTag(buffer, 2, "setMultiplestatesToUncertainty", setMultiplestatesToUncertainty);  

		preferencesSet = true;
		return buffer.toString();
	}
	/*.................................................................................................................*/
	public boolean queryOptions() {
		MesquiteInteger buttonPressed = new MesquiteInteger(1);
		ExtensibleDialog queryDialog = new ExtensibleDialog(containerOfModule(), "Merge Taxa",buttonPressed);  //MesquiteTrunk.mesquiteTrunk.containerOfModule()
		queryDialog.addLabel("Merge Taxa");
		
		RadioButtons choices = queryDialog.addRadioButtons (new String[]{"Use first taxon's name", "Merge taxon names, retaining full length", "Merge taxon names, retaining partial names:"}, keepMode);

		IntegerField startLengthToKeepField = queryDialog.addIntegerField("Number of characters from start of each name to retain:", startLengthToKeep, 6, 0, 200);
		IntegerField endLengthToKeepField = queryDialog.addIntegerField("Number of characters from end of each name to retain:", endLengthToKeep, 6, 0, 200);
		
		Checkbox setMultipleStatesUncertaintyBox = queryDialog.addCheckBox("Set merged cells with multiple states to uncertainty rather than polymorphism", setMultiplestatesToUncertainty);

		queryDialog.completeAndShowDialog(true);
		if (buttonPressed.getValue()==0)  {
			keepMode = choices.getValue();
			startLengthToKeep = startLengthToKeepField.getValue();
			endLengthToKeep = endLengthToKeepField.getValue();
			setMultiplestatesToUncertainty = setMultipleStatesUncertaintyBox.getState();
			storePreferences();
		}
		queryDialog.dispose();
		return (buttonPressed.getValue()==0);
	}
	/*.................................................................................................................*/
	/** if returns true, then requests to remain on even after operateOnTaxa is called.  Default is false*/
	public boolean pleaseLeaveMeOn(){
		return false;
	}
	/*.................................................................................................................*/
	/** Called to operate on the taxa in the block.  Returns true if taxa altered*/
	public  boolean operateOnTaxa(Taxa taxa){
		int numSelected = taxa.numberSelected();
		if (numSelected<2){
			discreetAlert( "You need to select at least two taxa before merging them.");
			return false;
		}
		int numMatrices = getProject().getNumberCharMatrices(taxa);
		boolean nonCategFound = false;
		for (int iM = 0; iM < numMatrices; iM++){
			CharacterData data = getProject().getCharacterMatrix(taxa, iM);
			if (!(data instanceof CategoricalData)){
				nonCategFound = true;
			}
		}
		if (nonCategFound)
			discreetAlert( "Some character matrices are neither categorical nor molecular (e.g. are continuous, meristic). For these matrices, if more than one of the merged taxa have states in some characters, only the first taxon's states will be kept.");

		if (!MesquiteThread.isScripting()){
			boolean OK = AlertDialog.query(containerOfModule(), "Merge?", "Are you sure you want to merge the selected taxa?  You will not be able to undo this.  " 
					+ "Their character states in their character matrices will be merged.  " 
					+ "Other associated information like footnotes, attachments, and so forth WILL NOT be merged and will be lost from all but the first taxon.");
			if (!OK)
				return false;
			if (!queryOptions())
				return false;
		}
		boolean[] selected = new boolean[taxa.getNumTaxa()];

		for (int it = 0; it<taxa.getNumTaxa(); it++) {
			selected[it] = taxa.getSelected(it);
		}
		int firstSelected = taxa.firstSelected();
		String originalTaxonName = taxa.getTaxonName(firstSelected);
		
	//now let's merge the taxon names
		StringBuffer sb = new StringBuffer();
		int count=0;
			for (int it = 0; it<taxa.getNumTaxa(); it++) {
				if (selected[it]) {
					String s = taxa.getTaxonName(it);
					if (keepMode==KEEPPARTIAL && startLengthToKeep>0 && endLengthToKeep>0) {
						if (s.length()>startLengthToKeep+endLengthToKeep) {
							sb.append(s.substring(0,startLengthToKeep)+s.substring(s.length()-endLengthToKeep));
						}
					} else {
						sb.append(s);
						if (keepMode==USEFIRSTNAME) {
							break;
						}
					}
					count++;
					if (count<numSelected)
						sb.append("+");
				}
			}
	
/*
 * 		if (sb.length()> maxNameLength && !keepEntireName) {
			int indivLength = maxNameLength / numSelected-1;
			count=0;
			sb.setLength(0);
			if (indivLength<=2) {  // then the pieces are too small; let's just do it based upon the first two
				indivLength = (maxNameLength-2)/2;
				for (int it = 0; it<taxa.getNumTaxa(); it++) {
					if (selected[it]) {
						String partName = taxa.getTaxonName(it);
						if (partName.length()>indivLength)
							sb.append(partName.substring(0, indivLength+1) + "+");
						else
							sb.append(partName + "+");
						count++;
						if (count>=2)
							break;
					}
				}
			}
			else
				for (int it = 0; it<taxa.getNumTaxa(); it++) {
					if (selected[it]) {
						String partName = taxa.getTaxonName(it);
						if (partName.length()>indivLength)
							sb.append(partName.substring(0, indivLength+1));
						else
							sb.append(partName);
						count++;
						if (count<numSelected)
							sb.append("+");
					}
				}
		}
		*/
	//	selected[firstSelected] = false;
		String report = "";
		for (int iM = 0; iM < numMatrices; iM++){
			CharacterData data = getProject().getCharacterMatrix(taxa, iM);
			boolean[] ma = data.mergeTaxa(firstSelected, selected, setMultiplestatesToUncertainty);
			if (ma!= null){
				if (data instanceof CategoricalData)
					report += "For matrix " + data.getName() + ", the following taxa when merged to taxon \"" + originalTaxonName + "\" required merging of character states:\n";
				else
					report += "For matrix " + data.getName() + ", states of the following taxa may have been discarded when merging with taxon \"" + originalTaxonName + "\":\n";
					
				for (int it = 0; it< ma.length; it++){
					if (ma[it])
						report += "  " + taxa.getTaxonName(it) + "\n";
				}
			}
		}
		
		taxa.setTaxonName(firstSelected, sb.toString());
		
		for (int it =  taxa.getNumTaxa() -1; it> firstSelected; it--){
			if (selected[it]) {
				taxa.deleteTaxa(it, 1, false);
			}
		}

		if (!StringUtil.blank(report))
			alert(report);
		taxa.notifyListeners(this, new Notification(PARTS_DELETED));
		for (int iM = 0; iM < numMatrices; iM++){
			CharacterData data = getProject().getCharacterMatrix(taxa, iM);
			data.notifyListeners(this, new Notification(PARTS_DELETED));
		}
		return true;
	}
	/*.................................................................................................................*/
	public String getName() {
		return "Merge Taxa";
	}
	/*.................................................................................................................*/
	public String getNameForMenuItem() {
		return "Merge Taxa...";
	}

	/*.................................................................................................................*/
	public String getExplanation() {
		return "Merges selected taxa and their character states.";
	}
	/*.................................................................................................................*/
	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer
	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.
	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/
	public int getVersionOfFirstRelease(){
		return 110;  
	}
	/*.................................................................................................................*/
	public boolean isPrerelease(){
		return false;
	}

}





