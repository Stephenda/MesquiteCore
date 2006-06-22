/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.charts.TreesScattergram;/*~~  */import java.awt.*;import java.util.*;import mesquite.lib.*;import mesquite.lib.duties.*;/* ======================================================================== *//**=== Class TreesScattergram.  ===*/public class TreesScattergram extends FileAssistantCS  {	public NumberForTree numberTaskX, numberTaskY;	private TreeSource treeSourceTask;	ItemsBiplotter chartWindowTask;	public Taxa taxa;	boolean separateAxes=false;	MesquiteString numberTaskXName, numberTaskYName;	MesquiteString treeSourceName;	ChartWindow cWindow;	MesquiteCommand ntxC, ntyC, ntC, tstC;	MesquiteMenuSpec mX;	MesquiteMenuSpec mY;	static int numMade = 0;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		if (getProject().getNumberTaxas()==0)			return sorry(commandRec, getName() + " couldn't start because no blocks of taxa available.");		chartWindowTask = (ItemsBiplotter)hireEmployee(commandRec, ItemsBiplotter.class, null);		if (chartWindowTask == null)			return sorry(commandRec, getName() + " couldn't start because no charting module obtained.");		makeMenu("Scattergram");				//Source of trees %%%%%%%%		treeSourceTask= (TreeSource)hireEmployee(commandRec, TreeSource.class, "Source of trees (Trees scattergram)");		if (treeSourceTask == null)			return sorry(commandRec, getName() + " couldn't start because no source of trees obtained.");		tstC =makeCommand("setTreeSource",  this);		treeSourceTask.setHiringCommand(tstC);		treeSourceName = new MesquiteString();		treeSourceName.setValue(treeSourceTask.getName());		if (numModulesAvailable(TreeSource.class)>1) {			MesquiteSubmenuSpec mss = addSubmenu(null, "Tree Source", tstC, TreeSource.class);			mss.setSelected(treeSourceName);		}						//values etc.  %%%%%%%%		ntC =makeCommand("setValues",  this);		ntxC =makeCommand("setValuesX",  this);		ntyC =makeCommand("setValuesY",  this); 			 		cWindow = chartWindowTask.makeChartWindow(this, commandRec);		setModuleWindow( cWindow);		cWindow.setChartTitle("Trees Scattergram " + (++numMade));		cWindow.resetTitle();	 	taxa = getProject().chooseTaxa(containerOfModule(), "For which block of taxa do you want to show a trees scattergram?", commandRec); // as default;	 	if (taxa==null)	 		return sorry(commandRec, getName() + " couldn't start because block of taxa not obtained.");	 	taxa.addListener(this); 		numberTaskXName = new MesquiteString(); 		numberTaskYName = new MesquiteString();				/* ---------------------------*/		if (!commandRec.scripting()){			String expl = "(For instance, the X and Y axes might show the same calculations but for different characters, or they may show two entirely different calculations.)";			separateAxes = (numModulesAvailable(NumberForTreeIncr.class)==0) || !AlertDialog.query(containerOfModule(), "Axes", "Choose same or different calculations for the two axes? " + expl, "Same", "Different");			initMenus();			if (!separateAxes){				numberTaskX = (NumberForTreeIncr)hireEmployee(commandRec, NumberForTreeIncr.class, "Values for axes");				if (numberTaskX == null)					return sorry(commandRec, getName() + " couldn't start because no calculating module obtained.");		 		numberTaskXName.setValue(numberTaskX.getName());		 		numberTaskX.setHiringCommand(ntC);				numberTaskY = numberTaskX;			}			else {				numberTaskX = (NumberForTree)hireEmployee(commandRec, NumberForTree.class, "Values for X axis");				if (numberTaskX == null)					return sorry(commandRec, getName() + " couldn't start because no calculating module obtained for X axis.");				numberTaskY = (NumberForTree)hireEmployee(commandRec, NumberForTree.class, "Values for Y axis");				if (numberTaskY == null)					return sorry(commandRec, getName() + " couldn't start because no calculating module obtained for Y axis");		 		numberTaskXName.setValue(numberTaskX.getName());		 		numberTaskYName.setValue(numberTaskY.getName());		 		numberTaskX.setHiringCommand(ntxC);		 		numberTaskY.setHiringCommand(ntyC);				numberTaskX.setMenuToUse(mX);				numberTaskY.setMenuToUse(mY);			}			sendParametersToCharter();			if (!commandRec.scripting()){				chartWindowTask.doCounts(commandRec);				cWindow.setChartVisible();				cWindow.setVisible(true);			}		} 		resetContainingMenuBar();		resetAllWindowsMenus();		/* ---------------------------*/				return true;	}	private void initMenus(){			if (!separateAxes){				if (numModulesAvailable(NumberForTreeIncr.class)>1) {					MesquiteSubmenuSpec mss = addSubmenu(null, "Values", ntC, NumberForTreeIncr.class);		 			mss.setSelected(numberTaskXName);				}			}			else {				mX = addAuxiliaryMenu("X.Axis");				mY = addAuxiliaryMenu("Y.Axis");				if (numModulesAvailable(NumberForTree.class)>1) {					MesquiteSubmenuSpec mss = addSubmenu(null, "Values on X", ntxC, NumberForTree.class);		 			mss.setSelected(numberTaskXName);					mss = addSubmenu(null, "Values on Y", ntyC, NumberForTree.class);		 			mss.setSelected(numberTaskYName);				}			}	} 	public void employeeQuit(MesquiteModule m){ 		if (m== chartWindowTask) 			iQuit(); 	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}	/*.................................................................................................................*/	public boolean isSubstantive(){		return false;	}	/*.................................................................................................................*/	public void endJob(){			if (taxa!=null)				taxa.removeListener(this);			super.endJob();	}	/*.................................................................................................................*/	/** passes which object is being disposed (from MesquiteListener interface)*/	public void disposing(Object obj){		if (obj instanceof Taxa && (Taxa)obj == taxa) {			iQuit();		}	}	void sendParametersToCharter(){		chartWindowTask.setTaxa(taxa);		chartWindowTask.setNumberTaskX(numberTaskX);		chartWindowTask.setNumberTaskY(numberTaskY);		chartWindowTask.setItemsSource(treeSourceTask);	}	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {    	 	Snapshot temp = new Snapshot(); 	 	temp.addLine("setTreeSource ", treeSourceTask);  	 	temp.addLine("setTaxa " + getProject().getTaxaReferenceExternal(taxa));    	 	if (separateAxes){   	 		temp.addLine("axesDifferent");    	 		temp.addLine("setValuesX ", numberTaskX);    	 		temp.addLine("setValuesY ", numberTaskY);    	 	}   	 	else {   	 		temp.addLine("axesSame");    	 		temp.addLine("setValues ", numberTaskX);    	 	}		temp.addLine("getCharter", chartWindowTask);		temp.addLine("setChartVisible");    	 	temp.addLine("doCounts");    	 	temp.addLine("showWindow");  	 	return temp;  	 }  	 MesquiteInteger pos = new MesquiteInteger(0);	/*.................................................................................................................*/  	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {  		 if (checker.compare(this.getClass(), "Sets the taxa block to use", "[block reference, number, or name]", commandName, "setTaxa")) {  			 Taxa t = getProject().getTaxa(checker.getFile(), parser.getFirstToken(arguments));  			 if (t!=null){  				 if (taxa!=null)  					 taxa.removeListener(this);  				 taxa = t;  				 if (taxa!=null)  					 taxa.addListener(this);  				 parametersChanged(null, commandRec);  				 resetContainingMenuBar();  				 return taxa;  			 }  		 }  		 /*else if (checker.compare(this.getClass(), "Suspends calculations", null, commandName, "suspendCalculations")){  		  chartWindowTask.incrementSuspension(commandRec);  		  }  		  else if (checker.compare(this.getClass(), "Resumes calculations", null, commandName, "resumeCalculations")){  		  chartWindowTask.decrementSuspension(commandRec);  		  }  		  */  		 else if (checker.compare(this.getClass(), "Sets the two axes as using different calculators", null, commandName, "axesDifferent")){  			 separateAxes=true;  			 initMenus();  		 }  		 else if (checker.compare(this.getClass(), "Sets the two axes as using the same calculator", null, commandName, "axesSame")){  			 separateAxes=false;  			 initMenus();  		 }  		 else if (checker.compare(this.getClass(), "Sets the module to calculate the number for trees for both axes", "[name of module]", commandName, "setValues")) {  			 NumberForTreeIncr temp =  (NumberForTreeIncr)replaceEmployee(commandRec, NumberForTreeIncr.class, arguments, "Values for axes", numberTaskX);  			 if (temp!=null) {  				 numberTaskX = temp;  				 numberTaskX.setHiringCommand(ntC);  				 numberTaskY = numberTaskX;  				 numberTaskXName.setValue(numberTaskX.getName());  				 if (cWindow!=null){  					 if (!commandRec.scripting()) {  						 sendParametersToCharter();  						 chartWindowTask.doCounts(commandRec);  					 }  				 }  				 return numberTaskX;  			 }  		 }  		 else if (checker.compare(this.getClass(), "Sets the module to calculate the number for trees for the X axis", "[name of module]", commandName, "setValuesX")) {  			 NumberForTree temp =  (NumberForTree)replaceEmployee(commandRec, NumberForTree.class, arguments, "Values for X axis", numberTaskX);  			 if (temp!=null) {  				 numberTaskX = temp;  				 numberTaskX.setHiringCommand(ntxC);  				 numberTaskXName.setValue(numberTaskX.getName());  				 if (separateAxes)  					 numberTaskX.setMenuToUse(mX);  				 if (cWindow!=null){  					 if (!commandRec.scripting()) {  						 sendParametersToCharter();  						 chartWindowTask.doCounts(commandRec);  					 }  				 }  				 return numberTaskX;  			 }  		 }  		 else if (checker.compare(this.getClass(), "Sets the module to calculate the number for trees for the Y axis", "[name of module]", commandName, "setValuesY")) {  			 NumberForTree temp =  (NumberForTree)replaceEmployee(commandRec, NumberForTree.class, arguments, "Values for Y axis", numberTaskY);  			 if (temp!=null) {  				 numberTaskY = temp;  				 numberTaskY.setHiringCommand(ntyC);  				 numberTaskYName.setValue(numberTaskY.getName());  				 if (separateAxes)  					 numberTaskY.setMenuToUse(mY);  				 if (cWindow!=null){  					 if (!commandRec.scripting()) {  						 sendParametersToCharter();  						 chartWindowTask.doCounts(commandRec);  					 }  				 }  				 return numberTaskY;  			 }  		 }  		 else if (checker.compare(this.getClass(),  "Requests that chart calculations be redone", null, commandName, "doCounts")){  			 sendParametersToCharter();  			 if (cWindow!=null) {  				 chartWindowTask.doCounts(commandRec);  			 }  		 }  		 else if (checker.compare(this.getClass(), "Returns the chart drawing module", null, commandName, "getCharter")) {  			 return chartWindowTask;  		 }  		 else if (checker.compare(this.getClass(), "Sets the chart as visible", null, commandName, "setChartVisible")) {  			 if (cWindow!=null)  				 cWindow.setChartVisible();  		 }  		 else if (checker.compare(this.getClass(), "Sets the module supplying trees", "[name of module]", commandName, "setTreeSource")) {  			 TreeSource temp =   (TreeSource)replaceEmployee(commandRec, TreeSource.class, arguments, "Source of trees for chart", treeSourceTask);  			 if (temp!=null) {  				 treeSourceTask = temp;  				 treeSourceTask.setHiringCommand(tstC);  				 treeSourceName.setValue(treeSourceTask.getName());  				 if (cWindow!=null){  					 if (!commandRec.scripting()) {  						 sendParametersToCharter();  						 chartWindowTask.doCounts(commandRec);  					 }  				 }  				 return treeSourceTask;  			 }  		 }  		 else  			 return super.doCommand(commandName, arguments, commandRec, checker);  		 return null;  	 }  	 	/*.................................................................................................................*/	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) {		if (!commandRec.scripting()) {			sendParametersToCharter();			if (Notification.getCode(notification) == MesquiteListener.PARTS_ADDED && source == treeSourceTask) {				int[] notifParam = notification.getParameters();				if (notifParam!=null && notifParam.length==2)					// we now have just some trees added.  The first tree added as notifParam[0]+1, and notifParam[1] trees are added.					chartWindowTask.doCounts(commandRec, notifParam[0]+1, notifParam[0]+notifParam[1], false);				else					chartWindowTask.doCounts(commandRec);			} 			else if (Notification.getCode(notification) != MesquiteListener.SELECTION_CHANGED)				chartWindowTask.doCounts(commandRec);		}	}	/*.................................................................................................................*/    	 public String getName() {		return "Trees Scattergram";   	 }	/*.................................................................................................................*/    	 public String getNameForMenuItem() {		return "Trees";   	 }	/*.................................................................................................................*/  	 public String getExplanation() {		return "Makes a chart comparing two values for each of many trees.";   	 }   	 }