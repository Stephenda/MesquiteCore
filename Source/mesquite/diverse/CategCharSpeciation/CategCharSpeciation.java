/* Mesquite source code.  Copyright 1997-2002 W. Maddison & D. Maddison. Version 0.992.  September 2002.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.diverse.CategCharSpeciation;import mesquite.lib.*;import mesquite.categ.lib.*;import mesquite.diverse.lib.*;/** ======================================================================== */public class CategCharSpeciation extends TreeCharSimulate {	RandomBetween rng;	double rateCharStateChange = 0.05; //01	double rateForState0 = 0.1;	double rateForState1 = 0.5;	double increment = 0.001; //	double increment = 0.0001;	double prior1AtRoot = 0.5;		//IF BOOLEAN SET THEN increment rateForState0 up by 0.01 or some such each new tree		double ratePerIncrement0 = rateForState0*increment;	double ratePerIncrement1 = rateForState1*increment;	double rateChangePerIncrement = rateCharStateChange*increment;	double logRateForState0 = Math.log(ratePerIncrement0);  	double logRateForState1 = Math.log(ratePerIncrement1);	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) { 		rng= new RandomBetween(1); 		if (!commandRec.scripting()) 			rateCharStateChange = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Evolution of Speciation Controlling Character", rateCharStateChange); 		if (!MesquiteDouble.isCombinable(rateCharStateChange)) 			return false;   		MesquiteDouble r0 = new MesquiteDouble(rateForState0);   		MesquiteDouble r1 = new MesquiteDouble(rateForState1); 		if (!commandRec.scripting())   		if (!QueryDialogs.queryTwoDoubles(containerOfModule(),  "Speciation rates",  "Rate of speciation with character state 0",  r0,  "Rate of speciation with character state 1",  r1))   			return false;   		rateForState0 = r0.getValue(); 		if (!MesquiteDouble.isCombinable(rateForState0)) 			return false;    		rateForState1 = r1.getValue(); 		if (!MesquiteDouble.isCombinable(rateForState1)) 			return false;		ratePerIncrement0 = rateForState0*increment;		ratePerIncrement1 = rateForState1*increment;		logRateForState0 = Math.log(ratePerIncrement0);		logRateForState1 = Math.log(ratePerIncrement1);		rateChangePerIncrement = rateCharStateChange*increment;				  		addMenuItem("Rate of Evolution of Speciation Controlling Character...", makeCommand("setRate",  this));  		addMenuItem("Rate of Speciation if state 0...", makeCommand("setRate0",  this));  		addMenuItem("Rate of Speciation if state 1...", makeCommand("setRate1",  this));  		addMenuItem("Prior on state at root...", makeCommand("setPrior",  this));		return true;  	 }  	 	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return false;     	}	/*.................................................................................................................*/  	 public boolean isPrerelease(){  	 	return true;  	 }	/*.................................................................................................................*/   	 public boolean isSubstantive(){   	 	return true;   	 }	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();  	 	temp.addLine("setRate " + rateCharStateChange);  	 	temp.addLine("setRate0 " + rateForState0);  	 	temp.addLine("setRate1 " + rateForState1);  	 	temp.addLine("setPrior " + prior1AtRoot);  	 	return temp;  	 }  	 MesquiteInteger pos = new MesquiteInteger(0);	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Sets the rate of change of the categorical character", "[number]", commandName, "setRate")) {    	 		pos.setValue(0); 			double s = MesquiteDouble.fromString(arguments, pos);	 		if (!MesquiteDouble.isCombinable(s)) 				s = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Evolution of Speciation Controlling Character", rateCharStateChange);	 		if (MesquiteDouble.isCombinable(s)) {	 			rateCharStateChange = s;	 			rateChangePerIncrement = s * increment;	 			if (!commandRec.scripting())	 				parametersChanged(null, commandRec);	 		}    		}    	 	else if (checker.compare(this.getClass(), "Sets the rate of speciation if 0", "[number]", commandName, "setRate0")) {    	 		pos.setValue(0); 			double s = MesquiteDouble.fromString(arguments, pos);	 		if (!MesquiteDouble.isCombinable(s)) 				s = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Speciation if 0", rateForState0);	 		if (MesquiteDouble.isCombinable(s)) {	 			rateForState0 = s;				ratePerIncrement0 = rateForState0*increment;				logRateForState0 = Math.log(ratePerIncrement0);	 			if (!commandRec.scripting())	 				parametersChanged(null, commandRec);	 		}    		}    	 	else if (checker.compare(this.getClass(), "Sets the rate of speciation if 1", "[number]", commandName, "setRate1")) {    	 		pos.setValue(0); 			double s = MesquiteDouble.fromString(arguments, pos);	 		if (!MesquiteDouble.isCombinable(s)) 				s = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Speciation if 1", rateForState1);	 		if (MesquiteDouble.isCombinable(s)) {	 			rateForState1 = s;				ratePerIncrement1 = rateForState1*increment;				logRateForState1 = Math.log(ratePerIncrement1);	 			if (!commandRec.scripting())	 				parametersChanged(null, commandRec);	 		}    		}    	 	else if (checker.compare(this.getClass(), "Sets the prior probability of state 1 at root", "[number]", commandName, "setPrior")) {    	 		pos.setValue(0); 			double s = MesquiteDouble.fromString(arguments, pos);	 		if (!MesquiteDouble.isCombinable(s)) 				s = MesquiteDouble.queryDouble(containerOfModule(), "Root state", "Probability of state 1 at root", prior1AtRoot);	 		if (MesquiteDouble.isCombinable(s)) {	 			prior1AtRoot = s;	 			if (!commandRec.scripting())	 				parametersChanged(null, commandRec);	 		}    		}   	 	else return super.doCommand(commandName, arguments, commandRec, checker);		return null;   	 }   	    	 private double getRate(long state){   	 	if (state == 1) //state 0   	 		return logRateForState0;   	 	else   	 		return logRateForState1;   	 }	/*.................................................................................................................*/  	 private void speciateIfLucky(MesquiteTree tree, int node, CategoricalHistory states, MesquiteInteger countOfSpecies, int numTaxa){  	 	if (tree.nodeIsTerminal(node) && MesquiteDouble.isCombinable(tree.getBranchLength(node))) {  //length will be uncombinable if this was just a daughter species  	 		if (countOfSpecies.getValue()<numTaxa) {  	 			int taxon = tree.taxonNumberOfNode(node);  	 			long statesAtNode = states.getState(node);  	 			double rate = getRate(statesAtNode);  //negative absolute value of rate  	 			double probability = Math.exp(-Math.abs(rate));	  	 		if (rng.nextDouble()<probability) {	  	 			tree.splitTerminal(taxon, countOfSpecies.getValue(), false);	  	 			states.setState(tree.firstDaughterOfNode(node), statesAtNode);	  	 			states.setState(tree.lastDaughterOfNode(node), statesAtNode);	  	 			countOfSpecies.increment();	  	 		}  	 		}		}		else			for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)) {				if (countOfSpecies.getValue()<numTaxa)					speciateIfLucky(tree, d, states, countOfSpecies, numTaxa);			}  	 }  	   	 private void flipState(CategoricalHistory states, int index){  	 	 if (states.getState(index) == 1L)  	 	 	states.setState(index, 2L); //state 0 to 1  	 	 else  	 		states.setState(index, 1L);//state 1 to 0 	 }  	 int numFlips;	/*.................................................................................................................*/  	 private void evolveRates(MesquiteTree tree, int node, CategoricalHistory states){  	 	if (tree.nodeIsTerminal(node)) {  	 		if (rng.nextDouble()< rateChangePerIncrement) {  	 			flipState(states, node);  	 			numFlips++;  	 		}		}		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)) {			evolveRates(tree, d, states);		}  	 }	/*.................................................................................................................*/  	 private void addLengthToAllTerminals(MesquiteTree tree, int node, double increment){  	 	if (tree.nodeIsTerminal(node)) {  	 		double current = tree.getBranchLength(node, MesquiteDouble.unassigned);  	 		if (MesquiteDouble.isCombinable(current))  	 			tree.setBranchLength(node, current + increment, false);  	 		else  	 			tree.setBranchLength(node, increment, false);  	 				}		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)) {			addLengthToAllTerminals(tree, d, increment);		}  	 }	/*.................................................................................................................*/   	public String getDataType(){   		return "Standard Categorical Data";   	}	/*.................................................................................................................*/  	public  void doSimulation(Taxa taxa, int replicateNumber, ObjectContainer treeContainer, ObjectContainer characterHistoryContainer, MesquiteLong seed, CommandRecord commandRec){   	//save random seed used to make tree under tree.seed for use in recovering later  			rng.setSeed(seed.getValue());  			MesquiteTree tree = null;  			CategoricalHistory charHistory = null;  			   			Object t = treeContainer.getObject();   			if (t == null || !(t instanceof MesquiteTree))   				tree = new MesquiteTree(taxa);   			else   				tree = (MesquiteTree)t;   			Object c = characterHistoryContainer.getObject();   			if (c == null || !(c instanceof CategoricalHistory))   				charHistory = new CategoricalHistory(taxa);   			else     				charHistory = (CategoricalHistory)c;   			   			charHistory = (CategoricalHistory)charHistory.adjustSize(tree);  			 						int numTaxa = taxa.getNumTaxa();  			long state = 1L; //state 0  			if (rng.nextDouble()<prior1AtRoot)  				state = 2L; //state 1  			for (int i=0; i<tree.getNumNodeSpaces(); i++){  //state 0  				charHistory.setState(i, state);			} 						tree.setToDefaultBush(2, false);			tree.setAllBranchLengths(0, false);			MesquiteInteger countOfSpecies = new MesquiteInteger(2);			long generations = 0;			//logln("sim using  logRateForState0 " + logRateForState0 + " logRateForState1 " + logRateForState1 + " rateChangePerIncrement " + rateChangePerIncrement);			addLengthToAllTerminals(tree, tree.getRoot(), increment);			numFlips =0;			while (countOfSpecies.getValue()<numTaxa){				generations++;				evolveRates(tree, tree.getRoot(), charHistory);				speciateIfLucky(tree, tree.getRoot(), charHistory, countOfSpecies, numTaxa);				addLengthToAllTerminals(tree, tree.getRoot(), increment);			}		//	MesquiteMessage.println("number of character changes: " + numFlips);			treeContainer.setObject(tree);			characterHistoryContainer.setObject(charHistory);						seed.setValue(rng.nextLong());  //see for next time   	}      	public void initialize(Taxa taxa, CommandRecord commandRec){   	}	/*.................................................................................................................*/	/*.................................................................................................................*/    	 public String getName() {		return "Evolving Speciation Rate (Categorical Character)";   	 }	/*.................................................................................................................*/  	 public String getVersion() {		return null;   	 }   	 	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Generates tree by a speciation model in which a character controlling speciation ." ;   	 }	/*.................................................................................................................*/   	public String getParameters() {		return "Rate of evolution of character controlling speciation: " + rateCharStateChange;   	}	/*.................................................................................................................*/}