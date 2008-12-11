/* Mesquite source code.  Copyright 1997-2008 W. Maddison and D. Maddison.Version 2.5, June 2008.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib;import java.awt.*;import java.awt.event.*;import mesquite.lib.system.*;/* еееееееееееееееееееееееееее commands еееееееееееееееееееееееееееееее *//* includes commands,  buttons, miniscrolls/* ======================================================================== *//* scrollbar for tree */public abstract class MesquiteScrollbar extends Scrollbar implements AdjustmentListener {	public MesquiteScrollbar ( int orientation, int value, int visible, int min, int max){		super(orientation, value, visible, min, max);		addAdjustmentListener(this);		SystemUtil.setFocusable(this, false);		setCursor(Cursor.getDefaultCursor());	}	public void adjustmentValueChanged(AdjustmentEvent e) {		Debugg.println("scrolled" + e);		//Event queue		if (processDuringAdjustment() || !e.getValueIsAdjusting())			scrollTouched();	}	public abstract void scrollTouched();		public boolean processDuringAdjustment() {		return true;	}		}