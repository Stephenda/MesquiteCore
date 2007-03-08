/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.lib;import java.awt.*;import java.awt.event.ComponentAdapter;import java.awt.event.ComponentEvent;import java.awt.event.WindowAdapter;import java.awt.event.WindowEvent;import java.util.*;import mesquite.lib.duties.*;/* ======================================================================== *//** an intermediary class that can be changed to extend Panel versus Frame, to allow embedding versus not UNEMBEDDED VERSION */public class MesquiteFrame extends Frame {	Vector windows;	MesquiteWindow frontWindow;	CardLayout layout;	MesquiteProject project;	MesquiteModule ownerModule;	static int numTotal = 0;	Color dark, medium, light; //, pale;	int num = 0;	int id = 0;	Panel main;	FrameTabsPanel tabs;	int tabHeight = 32;	public MesquiteFrame(boolean compactible) {		super();		id = numTotal++;			windows = new Vector();		oldInsetTop=oldInsetBottom=oldInsetRight= oldInsetLeft= -1; 		/**/		setResizable(true);		main = new Panel();		setLayout(null);		if (compactible){			tabs = new FrameTabsPanel(this);			add(tabs);		}		else tabHeight = 0;		add(main);		resetSizes(true);		main.setLayout(layout = new CardLayout());		addComponentListener(new MWCE(this));		/* EMBEDDED if embedded remove this */		addWindowListener(new MWWE(this));		//setSize(10, 10);		/**/		/**/	}	public void setResizable(boolean r){		if (project != null && !r)			return;		super.setResizable(r);	}	public MesquiteModule getOwnerModule(){ //mesquite or basic file coordinator		return ownerModule;	}	public void setOwnerModule(MesquiteModule mb){ //mesquite or basic file coordinator		ownerModule = mb;		if (ownerModule != null)			project = ownerModule.getProject();	}	/*.................................................................................................................*/	public void dispose() {		if (activeWindow == this)			activeWindow = null;		super.dispose();	}	public void setMenuBar(MesquiteWindow which, MenuBar mbar) {		if (which == frontWindow)			super.setMenuBar(mbar);	}	public void windowTitleChanged(MesquiteWindow w) {		if (windows.size() == 1)			setTitle(w.getTitle());		else if (project == null)			setTitle("Mesquite");		else			setTitle(project.getHomeFileName());		if (tabs !=null)			tabs.repaint();	}	/*.................................................................................................................*/	public void addPage(MesquiteWindow w){//		pale = ColorDistribution.pale[w.getColorScheme()];		light = ColorDistribution.light[w.getColorScheme()];		medium =ColorDistribution.medium[w.getColorScheme()];		dark =ColorDistribution.dark[w.getColorScheme()];		setBackground(light);		if (tabs !=null)			tabs.setBackground(light);		String id = Integer.toString(w.getID());		main.add(w.outerContents, id);		layout.addLayoutComponent(w.outerContents, id);		windows.addElement(w);		frontWindow = w;		setMenuBar(w, w.getMenuBar());		resetSizes(true);		if (tabs !=null)			tabs.repaint();		doLayout();		main.doLayout();		if (windows.size() == 1)			setTitle(w.getTitle());		else if (project == null)			setTitle("Mesquite");		else			setTitle(project.getHomeFileName());	}	/*.................................................................................................................*/	private MesquiteWindow findWindow(String s){		for (int i = 0; i<windows.size(); i++){			MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);			if (Integer.toString(w.getID()).equals(s))				return w;		}		return null;	}	/*.................................................................................................................*/	public void showPage(int i){		if (i <0 || i>=windows.size())			return;		MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);		if (w == frontWindow)			return;		showPage(Integer.toString(w.getID()));	}	/*.................................................................................................................*/	public void showPage(String s){		layout.show(main, s);		MesquiteWindow w = findWindow(s);		if (w == null)			return;		frontWindow = w;		resetSizes(true);		validate();		if (tabs !=null)			tabs.repaint();		setMenuBar(w, w.getMenuBar());	}	/*.................................................................................................................*/	public void hide(MesquiteWindow w){		setVisible(w, false);	}	/*.................................................................................................................*/	public void hide(int i){		if (i <0 || i>=windows.size())			return;		MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);		hide(w);	}	/*.................................................................................................................*/	public boolean showGoAway(int i){		if (i <0 || i>=windows.size())			return false;		MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);		return !(w instanceof SystemWindow);	}	/*.................................................................................................................*/	public MesquiteWindow getMesquiteWindow(){		return (MesquiteWindow)windows.elementAt(0); //should return frontmost	}	/*.................................................................................................................*/	public void setVisible(MesquiteWindow w, boolean vis){		if (vis){			layout.show(main, Integer.toString(w.getID()));			frontWindow = w;			setMenuBar(w, w.getMenuBar());			if (tabs !=null)				tabs.repaint();			validate();			if (!isVisible())				setVisible(true);		}		else {			layout.removeLayoutComponent(w.getOuterContentsArea());				main.remove(w.getOuterContentsArea());			windows.removeElement(w);			if (tabs !=null)				tabs.repaint();			resetSizes(true);			if (windows.size()==0){				setVisible(false);			}			else if (windows.size() == 1)				resetSizes(true);		}	}	/*.................................................................................................................*/	/** Shows the window */	public void setVisible(boolean vis) {		if (doingShow)			return;		doingShow = true;		if (vis){			if (!checkInsets())				resetSizes(true);			if (!MesquiteWindow.GUIavailable || MesquiteWindow.suppressAllWindows){				doingShow = false;				return;			}		}		super.setVisible(vis);		doingShow = false;	}	/*.................................................................................................................*/	public void diagnose(){		System.out.println("  FRAME ~~~~~~~~~ visible = " + isVisible() + " size " + getBounds().width + " " + getBounds().height + " this =" + id);		System.out.println("  layout  = " + layout);		for (int i = 0; i<windows.size(); i++){			MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);			System.out.println("      " + w.getClass() + " visible = " + w.isVisible() + " loc " + w.getBounds().x + " " + w.getBounds().y + " size " + w.getBounds().width + " " + w.getBounds().height);		}	}	/*.................................................................................................................*/	boolean doingShow = false;	private int oldInsetTop, oldInsetBottom, oldInsetRight, oldInsetLeft;	int savedX = 0;	int savedY = 0;	int savedW = 0;	int savedH = 0;	int savedFullW = 0;	int savedFullH = 0;	/*.................................................................................................................*	public void show(){		if (doingShow)			return;		setVisible(true);	}		/*.................................................................................................................*/	protected void saveFullDimensions(){		savedFullW = getBounds().width;		savedFullH = getBounds().height;	}	/*.................................................................................................................*/	boolean withTabWhenSaved = false;	public void setSavedDimensions(int w, int h){		savedW = w;		savedH = h;	}	/*.................................................................................................................*/	/** Sets the window size.  To be used instead of setSize. 	 * Passed are the requested size of the contents. This frame must accommodate extra for insets in setting its own size*/	public void setWindowSize(int width, int height) {		setWindowSize(width, height, true);	}	/*.................................................................................................................*/	public void setWindowSize(int width, int height, boolean expandOnly) {		Insets insets = getInsets();		int neededWidth = width + insets.left + insets.right;		if (expandOnly && neededWidth < getBounds().width){			neededWidth = getBounds().width;			width = neededWidth - (insets.left + insets.right);		}		int neededHeight = height + insets.top +insets.bottom;		if (expandOnly && neededHeight < getBounds().height){			neededHeight = getBounds().height;			height = neededHeight - (insets.top +insets.bottom);		}		if (!withTabWhenSaved && (windows.size()>1))			height -= tabHeight;		else if (withTabWhenSaved && (windows.size()<=1))			height += tabHeight;		withTabWhenSaved =  (windows.size()>1);		setSavedDimensions(width, height);		setSize(neededWidth, neededHeight);		resetSizes(true);		for (int i = 0; i<windows.size(); i++){			MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);			w.setContentsSize(width, height);		}		//storeInsets(getInsets());		saveFullDimensions();	}	/*.................................................................................................................*/	private void resetSizes(boolean resizeContainedWindows){		Insets insets = getInsets();		storeInsets(insets);		if (windows.size()>1){			if (tabs !=null){				tabs.setVisible(true);				tabs.setBounds(insets.left, insets.top, getBounds().width - insets.left - insets.right, tabHeight);			}			main.setBounds(insets.left, insets.top + tabHeight, getBounds().width - insets.left - insets.right, getBounds().height - insets.top - insets.bottom - tabHeight);			main.doLayout();			if (resizeContainedWindows && !withTabWhenSaved){				savedH -= tabHeight;				withTabWhenSaved = true;				for (int i = 0; i<windows.size(); i++){					MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);					w.setContentsSize(savedW, savedH);				}			}		}		else {			if (tabs !=null){				tabs.setVisible(false);				tabs.setBounds(0,0,0,0);			}			main.setBounds(insets.left, insets.top, getBounds().width - insets.left - insets.right, getBounds().height - insets.top - insets.bottom );			main.doLayout();			if (resizeContainedWindows && withTabWhenSaved){				savedH += tabHeight;				withTabWhenSaved = false;				for (int i = 0; i<windows.size(); i++){					MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);					w.setContentsSize(savedW, savedH);				}			}		}	}	/*.................................................................................................................*/	boolean locationPrevoiuslySet = false;	public void setWindowLocation(int x, int y, boolean overridePrevious){		if (!overridePrevious && windows.size()>1 && locationPrevoiuslySet)			return;		locationPrevoiuslySet = true;		savedX = x;		savedY = y;		setLocation(x, y);	}	public void paint(Graphics g){		if (checkInsets())			super.paint(g);	}	/*.................................................................................................................*/	private boolean checkInsets(){		Insets insets = getInsets();		if (oldInsetTop!=insets.top || oldInsetBottom !=insets.bottom || oldInsetRight!= insets.right || oldInsetLeft != insets.left) {			//storeInsets(insets);			resetSizes(false);			return false;		}		return true;	}	/*.................................................................................................................*/	private void storeInsets(Insets insets){		oldInsetTop=insets.top;		oldInsetBottom=insets.bottom;		oldInsetRight= insets.right;		oldInsetLeft= insets.left; 	}	/*.................................................................................................................*/	class MWCE extends ComponentAdapter{		MesquiteFrame f;		public MWCE (MesquiteFrame f){			super();			this.f = f;		}		public void componentResized(ComponentEvent e){			//		Debugg.println(" componentResized " + e.getSource());			if (e==null || e.getComponent()!= f || (getOwnerModule()!=null && (getOwnerModule().isDoomed()))) // || disposing)))				return;			if (savedFullH== getBounds().height&& savedFullW == getBounds().width) {			}			else if (doingShow || !isResizable()){				setWindowSize(savedW, savedH);			}			else {				//storeInsets(getInsets());				resetSizes(false);				for (int i = 0; i<windows.size(); i++){					MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);					w.windowResized();				}			}		}		public void componentMoved(ComponentEvent e){			if (e==null || e.getComponent()!= f || (getOwnerModule()!=null && (getOwnerModule().isDoomed())))// || disposing)))				return;			//	if (doingShow)			//		setLocation(savedX,savedY);		}		public void componentShown(ComponentEvent e){			Insets insets = getInsets();			if (!checkInsets())				resetSizes(true);			else			if (MesquiteTrunk.isMacOSXJaguar()) {				for (int i = 0; i<windows.size(); i++){					MesquiteWindow w = (MesquiteWindow)windows.elementAt(i);					w.repaintAll();				}			}			Toolkit.getDefaultToolkit().sync();		}	}	public static MesquiteFrame activeWindow;	/*.................................................................................................................*/	class MWWE extends WindowAdapter{		MesquiteFrame f;		public MWWE (MesquiteFrame f){			this.f = f;		}		public void windowClosing(WindowEvent e){			/*			MesquiteModule.incrementMenuResetSuppression();			try {				if (MesquiteTrunk.isMacOS() && f!=activeWindow && activeWindow!=null) //workaround the Mac OS menu disappearance bug after closing a window					toFront();				if (f != null && w.closeWindowCommand != null)					w.closeWindowCommand.doIt(null); //this might be best done on separate thread, but because of menu disappearance bug after closing a window in Mac OS, is done immediately			}			catch (Exception ee){			}			MesquiteModule.decrementMenuResetSuppression();			 *		}		public void windowActivated(WindowEvent e){		/*			if (w !=null) {				activeWindow = w;//for workaround the Mac OS menu disappearance bug after closing a window				if (w.ownerModule !=null && w.ownerModule.getProject() !=null) //so that file save will remember foremost window					w.ownerModule.getProject().activeWindowOfProject = w;			}			 */		}	}}class FrameTabsPanel extends MousePanel {	MesquiteFrame frame;	int[] widths;	Font[] fonts = new Font[4];	int frontEdge = 6;	int backEdge = 20;	Image goaway;	public FrameTabsPanel(MesquiteFrame f){		this.frame = f;		//fonts[0] = new Font("SanSerif", Font.PLAIN, 13);		fonts[0] = new Font("SanSerif", Font.PLAIN, 12);		fonts[1] = new Font("SanSerif", Font.PLAIN, 11);		fonts[2] = new Font("SanSerif", Font.PLAIN, 10);		fonts[3] = new Font("SanSerif", Font.PLAIN, 9);	//	f.diagnose();		goaway = MesquiteImage.getImage(MesquiteModule.getRootImageDirectoryPath() + "goawayTransparent.gif");	//	Debugg.printStackTrace("=================");	}	public void mouseDown (int modifiers, int clickCount, long when, int x, int y, MesquiteTool tool) {		if (widths == null)			return;		repaint();		for (int i = 0; i<widths.length; i++)			if (x < widths[i]){				if (frame.showGoAway(i) && x> widths[i] - 20 && y >5 && y<22)					frame.hide(i);				else					frame.showPage(i);				return;			}	}	StringInABox box;	public void paint(Graphics g){		int numTabs = frame.windows.size();		if (numTabs<2)			return;		if (widths == null || widths.length != numTabs)			widths = new int[numTabs];		String totalString = "";		for (int i = 0; i<frame.windows.size(); i++){			MesquiteWindow w = (MesquiteWindow)frame.windows.elementAt(i);			String s = w.getTitle();			if (s != null)				totalString += w.getTitle();		}		int edges = (frontEdge + backEdge) * numTabs;		int width = getBounds().width;		int height = getBounds().height;		int i = 0;		g.setFont(fonts[0]);		int needed = 0;		while ((needed = StringUtil.getStringDrawLength(g, totalString)+edges) > width && i<fonts.length-1){			i++;			g.setFont(fonts[i]);		}		double scaling = 1.0;		if (needed> width)			scaling = (width-edges)*1.0/StringUtil.getStringDrawLength(g, totalString);		int left = 0;		for (i = 0; i<frame.windows.size(); i++){			int tabLeft = left;			left += frontEdge;			MesquiteWindow w = (MesquiteWindow)frame.windows.elementAt(i);			String title = w.getTitle();			if (title != null){				if (scaling < 1.0){					int wish = StringUtil.getStringDrawLength(g, title);					int offer = (int)(scaling * wish);					drawTab(g, w == frame.frontWindow, tabLeft, left + offer + backEdge-2, height);					if (box == null)						box = new StringInABox(title, g.getFont(), offer);					else						box.setStringAndFontAndWidth(title, g.getFont(), offer, g);					if (w == frame.frontWindow)						g.setColor(Color.black);					else 						g.setColor(frame.dark);					box.draw(g, left, 1);										left += offer;				}				else {					int offer = StringUtil.getStringDrawLength(g, title);					drawTab(g, w == frame.frontWindow, tabLeft, left + offer + backEdge-2, height);					if (w == frame.frontWindow)						g.setColor(Color.black);					else 						g.setColor(frame.dark);					g.drawString(title, left, height -12);					left += offer;				}			}			else 				MesquiteMessage.println("window without title " + w.getClass());			left += backEdge;			if (goaway != null && frame.showGoAway(i))				g.drawImage(goaway, left-19, 6, this);			g.setColor(frame.dark);			try{				widths[i] = left;			}			catch (ArrayIndexOutOfBoundsException e){				repaint();			}		}		g.setColor(frame.dark);		g.fillRect(0, height-2, width, 3);		//	Debugg.println("resiz? " + frame.isResizable());	}	void drawTab(Graphics g, boolean isFront, int tabLeft, int tabRight, int height){		if (isFront){			g.setColor(frame.medium);			g.fillRoundRect(tabLeft, 2, tabRight - tabLeft - 2, height+60, 16, 16);			g.setColor(Color.black);			g.drawRoundRect(tabLeft, 2, tabRight - tabLeft - 2, height+60, 16, 16);		}		else {			g.setColor(frame.dark);			g.drawRoundRect(tabLeft, 2, tabRight - tabLeft - 2, height+60, 16, 16);		}	}}