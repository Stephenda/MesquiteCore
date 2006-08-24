/* Mesquite source code.  Copyright 1997-2002 W. Maddison & D. Maddison. Version 0.992.  September 2002.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.diverse.CategCharSpecExt;import mesquite.lib.*;import mesquite.categ.lib.*;import mesquite.diverse.lib.*;/** ======================================================================== */public class CategCharSpecExt extends TreeCharSimulate {	RandomBetween rng;	double rateCharStateChange = 0.05; //01	double spnForState0 = 0.1;	double spnForState1 = 0.5;	double extForState0 = 0.05;	double extForState1 = 0.05;	double increment = 0.001; //	double increment = 0.0001;	double prior1AtRoot = 0.5;	//IF BOOLEAN SET THEN increment spnForState0 up by 0.01 or some such each new tree	double spnPerIncrement0 = spnForState0*increment;	double spnPerIncrement1 = spnForState1*increment;	double extPerIncrement0 = extForState0*increment;	double extPerIncrement1 = extForState1*increment;	double rateChangePerIncrement = rateCharStateChange*increment;	double logSpnForState0 = Math.log(spnPerIncrement0);  	double logSpnForState1 = Math.log(spnPerIncrement1);	double logExtForState0 = Math.log(extPerIncrement0);  	double logExtForState1 = Math.log(extPerIncrement1);	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		rng= new RandomBetween(1);		if (!commandRec.scripting())			rateCharStateChange = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Evolution of Speciation Controlling Character", rateCharStateChange);		if (!MesquiteDouble.isCombinable(rateCharStateChange))			return false;//		spn		MesquiteDouble r0 = new MesquiteDouble(spnForState0);		MesquiteDouble r1 = new MesquiteDouble(spnForState1);		if (!commandRec.scripting())			if (!QueryDialogs.queryTwoDoubles(containerOfModule(),  "Speciation rates",  "Rate of speciation with character state 0",  r0,  "Rate of speciation with character state 1",  r1))				return false;		spnForState0 = r0.getValue();		if (!MesquiteDouble.isCombinable(spnForState0))			return false;		spnForState1 = r1.getValue();		if (!MesquiteDouble.isCombinable(spnForState1))			return false;		spnPerIncrement0 = spnForState0*increment;		spnPerIncrement1 = spnForState1*increment;		logSpnForState0 = Math.log(spnPerIncrement0);		logSpnForState1 = Math.log(spnPerIncrement1);//		extinction		r0.setValue(extForState0);		r1.setValue(extForState1);		if (!commandRec.scripting())			if (!QueryDialogs.queryTwoDoubles(containerOfModule(),  "Extinction rates",  "Rate of extinction with character state 0",  r0,  "Rate of extinction with character state 1",  r1))				return false;		extForState0 = r0.getValue();		if (!MesquiteDouble.isCombinable(extForState0))			return false;		extForState1 = r1.getValue();		if (!MesquiteDouble.isCombinable(extForState1))			return false;		extPerIncrement0 = extForState0*increment;		extPerIncrement1 = extForState1*increment;		logExtForState0 = Math.log(extPerIncrement0);		logExtForState1 = Math.log(extPerIncrement1);		rateChangePerIncrement = rateCharStateChange*increment;		addMenuItem("Rate of Evolution of Speciation Controlling Character...", makeCommand("setRate",  this));		addMenuItem("Rate of Speciation if state 0...", makeCommand("setSRate0",  this));		addMenuItem("Rate of Speciation if state 1...", makeCommand("setSRate1",  this));		addMenuItem("Rate of Extinction if state 0...", makeCommand("setERate0",  this));		addMenuItem("Rate of Extinction if state 1...", makeCommand("setERate1",  this));		addMenuItem("Prior on state at root...", makeCommand("setPrior",  this));		return true;	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return false;  	}	/*.................................................................................................................*/	public boolean isSubstantive(){		return true;	}	/*.................................................................................................................*/	public Snapshot getSnapshot(MesquiteFile file) {		Snapshot temp = new Snapshot();		temp.addLine("setRate " + rateCharStateChange);		temp.addLine("setSRate0 " + spnForState0);		temp.addLine("setSRate1 " + spnForState1);		temp.addLine("setERate0 " + extForState0);		temp.addLine("setERate1 " + extForState1);		temp.addLine("setPrior " + prior1AtRoot);		return temp;	}	MesquiteInteger pos = new MesquiteInteger(0);	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(this.getClass(), "Sets the rate of change of the categorical character", "[number]", commandName, "setRate")) {			pos.setValue(0);			double s = MesquiteDouble.fromString(arguments, pos);			if (!MesquiteDouble.isCombinable(s))				s = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Evolution of Speciation Controlling Character", rateCharStateChange);			if (MesquiteDouble.isCombinable(s)) {				rateCharStateChange = s;				rateChangePerIncrement = s * increment;				if (!commandRec.scripting())					parametersChanged(null, commandRec);			}		}		else if (checker.compare(this.getClass(), "Sets the rate of speciation if 0", "[number]", commandName, "setSRate0")) {			pos.setValue(0);			double s = MesquiteDouble.fromString(arguments, pos);			if (!MesquiteDouble.isCombinable(s))				s = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Speciation if 0", spnForState0);			if (MesquiteDouble.isCombinable(s)) {				spnForState0 = s;				spnPerIncrement0 = spnForState0*increment;				logSpnForState0 = Math.log(spnPerIncrement0);				if (!commandRec.scripting())					parametersChanged(null, commandRec);			}		}		else if (checker.compare(this.getClass(), "Sets the rate of speciation if 1", "[number]", commandName, "setSRate1")) {			pos.setValue(0);			double s = MesquiteDouble.fromString(arguments, pos);			if (!MesquiteDouble.isCombinable(s))				s = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Speciation if 1", spnForState1);			if (MesquiteDouble.isCombinable(s)) {				spnForState1 = s;				spnPerIncrement1 = spnForState1*increment;				logSpnForState1 = Math.log(spnPerIncrement1);				if (!commandRec.scripting())					parametersChanged(null, commandRec);			}		}		else if (checker.compare(this.getClass(), "Sets the rate of extinction if 0", "[number]", commandName, "setERate0")) {			pos.setValue(0);			double s = MesquiteDouble.fromString(arguments, pos);			if (!MesquiteDouble.isCombinable(s))				s = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Extinction if 0", extForState0);			if (MesquiteDouble.isCombinable(s)) {				extForState0 = s;				extPerIncrement0 = extForState0*increment;				logExtForState0 = Math.log(extPerIncrement0);				if (!commandRec.scripting())					parametersChanged(null, commandRec);			}		}		else if (checker.compare(this.getClass(), "Sets the rate of extinction if 1", "[number]", commandName, "setERate1")) {			pos.setValue(0);			double s = MesquiteDouble.fromString(arguments, pos);			if (!MesquiteDouble.isCombinable(s))				s = MesquiteDouble.queryDouble(containerOfModule(), "Rate", "Rate of Extinction if 1", extForState1);			if (MesquiteDouble.isCombinable(s)) {				extForState1 = s;				extPerIncrement1 = extForState1*increment;				logExtForState1 = Math.log(extPerIncrement1);				if (!commandRec.scripting())					parametersChanged(null, commandRec);			}		}		else if (checker.compare(this.getClass(), "Sets the prior probability of state 1 at root", "[number]", commandName, "setPrior")) {			pos.setValue(0);			double s = MesquiteDouble.fromString(arguments, pos);			if (!MesquiteDouble.isCombinable(s))				s = MesquiteDouble.queryDouble(containerOfModule(), "Root state", "Probability of state 1 at root", prior1AtRoot);			if (MesquiteDouble.isCombinable(s)) {				prior1AtRoot = s;				if (!commandRec.scripting())					parametersChanged(null, commandRec);			}		}		else return super.doCommand(commandName, arguments, commandRec, checker);		return null;	}	private double getSRate(long state){		if (state == 1) //state 0			return logSpnForState0;		else			return logSpnForState1;	}	private double getERate(long state){		if (state == 1) //state 0			return logExtForState0;		else			return logExtForState1;	}		long countSpeciations = 0;	long countExtinctions = 0;	/*.................................................................................................................*/	private boolean goExtinctIfUnlucky(Taxa taxa, MesquiteTree tree, CategoricalHistory states, MesquiteInteger countOfSpecies, CommandRecord commandRec){		for (int it = 0; it<taxa.getNumTaxa(); it++){			int node = tree.nodeOfTaxonNumber(it);			if (tree.nodeExists(node)){				long statesAtNode = states.getState(node);				double rate = getERate(statesAtNode);				double probability = Math.exp(-Math.abs(rate));				if (rng.nextDouble()<probability) {					tree.deleteClade(node, false);					countExtinctions++;					countOfSpecies.decrement();		  			commandRec.tick("Went extinct at node " + node + " ; total number of species " + countOfSpecies + "; total speciations: " + countSpeciations  + "; total extinctions: " + countExtinctions );					if (countOfSpecies.getValue() == 0 || tree.numberOfTerminalsInClade(tree.getRoot()) == 0)						return true;				}			}		}		return false;	}	/*.................................................................................................................*/	private void speciateIfLucky(MesquiteTree tree, int node, CategoricalHistory states, MesquiteInteger countOfSpecies, int numTaxa, CommandRecord commandRec){		if (tree.nodeIsTerminal(node) && MesquiteDouble.isCombinable(tree.getBranchLength(node))) {  //length will be uncombinable if this was just a daughter species			if (countOfSpecies.getValue()<numTaxa) {				int taxon = tree.taxonNumberOfNode(node);				long statesAtNode = states.getState(node);				double rate = getSRate(statesAtNode);  //negative absolute value of rate				double probability = Math.exp(-Math.abs(rate));				if (rng.nextDouble()<probability) {					tree.splitTerminal(taxon, -1, false);					countSpeciations++;					states.setState(tree.firstDaughterOfNode(node), statesAtNode);					states.setState(tree.lastDaughterOfNode(node), statesAtNode);					countOfSpecies.increment();		  			commandRec.tick("Speciated at node " + node + " ; total number of species " + countOfSpecies + "; total speciations: " + countSpeciations  + "; total extinctions: " + countExtinctions );				}			}		}		else			for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)) {				if (countOfSpecies.getValue()<numTaxa)					speciateIfLucky(tree, d, states, countOfSpecies, numTaxa, commandRec);			}	}	private void flipState(CategoricalHistory states, int index){		if (states.getState(index) == 1L)			states.setState(index, 2L); //state 0 to 1		else			states.setState(index, 1L);//state 1 to 0	}	int numFlips;	/*.................................................................................................................*/	private void evolveRates(MesquiteTree tree, int node, CategoricalHistory states){		if (tree.nodeIsTerminal(node)) {			if (rng.nextDouble()< rateChangePerIncrement) {				flipState(states, node);				numFlips++;			}		}		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)) {			evolveRates(tree, d, states);		}	}	/*.................................................................................................................*/	private void addLengthToAllTerminals(MesquiteTree tree, int node, double increment){		if (tree.nodeIsTerminal(node)) {			double current = tree.getBranchLength(node, MesquiteDouble.unassigned);			if (MesquiteDouble.isCombinable(current))				tree.setBranchLength(node, current + increment, false);			else				tree.setBranchLength(node, increment, false);  	 				}		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d)) {			addLengthToAllTerminals(tree, d, increment);		}	}	/*.................................................................................................................*/	public String getDataType(){		return "Standard Categorical Data";	}	/*.................................................................................................................*/	public  void doSimulation(Taxa taxa, int replicateNumber, ObjectContainer treeContainer, ObjectContainer characterHistoryContainer, MesquiteLong seed, CommandRecord commandRec){		//save random seed used to make tree under tree.seed for use in recovering later		rng.setSeed(seed.getValue());		MesquiteTree tree = null;		CategoricalHistory charHistory = null;		Object t = treeContainer.getObject();		if (t == null || !(t instanceof MesquiteTree))			tree = new MesquiteTree(taxa);		else			tree = (MesquiteTree)t;		Object c = characterHistoryContainer.getObject();		if (c == null || !(c instanceof CategoricalHistory))			charHistory = new CategoricalHistory(taxa);		else  			charHistory = (CategoricalHistory)c;		charHistory = (CategoricalHistory)charHistory.adjustSize(tree);		int numTaxa = taxa.getNumTaxa();		int attempts = 0;		boolean done = false;		boolean wentExtinct = false;		int patience = 100; //TODO: make this user settable		while (attempts < patience && !done){			countSpeciations = 0;			countExtinctions = 0;			wentExtinct = false;			long state = 1L; //state 0			if (rng.nextDouble()<prior1AtRoot)				state = 2L; //state 1			for (int i=0; i<tree.getNumNodeSpaces(); i++){  //state 0				charHistory.setState(i, state);			}			tree.setToDefaultBush(2, false);			tree.setAllBranchLengths(0, false);			MesquiteInteger countOfSpecies = new MesquiteInteger(2);			long generations = 0;			//logln("sim using  logSpnForState0 " + logSpnForState0 + " logSpnForState1 " + logSpnForState1 + " rateChangePerIncrement " + rateChangePerIncrement);			addLengthToAllTerminals(tree, tree.getRoot(), increment);			numFlips =0;			while (countOfSpecies.getValue()<numTaxa && !wentExtinct){				generations++;				evolveRates(tree, tree.getRoot(), charHistory);				if (goExtinctIfUnlucky(taxa, tree, charHistory, countOfSpecies, commandRec))					wentExtinct = true;				else {					speciateIfLucky(tree, tree.getRoot(), charHistory, countOfSpecies, numTaxa, commandRec);					addLengthToAllTerminals(tree, tree.getRoot(), increment);				}			}			if (!wentExtinct)				done = true;			attempts++;		}		if (!done){			tree.setToDefaultBush(2, false);			tree.setAllBranchLengths(0, false);			charHistory.deassignStates();			MesquiteMessage.warnUser("Attempt to simulate speciation/extinction failed because clade went entirely extinct " + patience + " times");		}		else 			MesquiteMessage.println("Tree and character " + (replicateNumber +1) + " successfully evolved.");		//	MesquiteMessage.println("number of character changes: " + numFlips);		treeContainer.setObject(tree);		characterHistoryContainer.setObject(charHistory);		seed.setValue(rng.nextLong());  //see for next time	}	public void initialize(Taxa taxa, CommandRecord commandRec){	}	/*.................................................................................................................*/	/*.................................................................................................................*/	public String getName() {		return "Evolving Speciation/Extinction Character";	}	/*.................................................................................................................*/	public String getVersion() {		return null;	}	/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Generates tree by a speciation/extinction model in which a character controlling rates of speciation/extinction ." ;	}	/*.................................................................................................................*/	public String getParameters() {		return "Rate of evolution of character controlling speciation/extinction: " + rateCharStateChange;	}	/*.................................................................................................................*/}