// StrippedAlignment.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)


// Known bugs and limitations:
// - can be pretty slow, a better data structure
//   such as a linked list would improve performance a lot


package pal.alignment;


/**
 * takes an alignment and repeatedly removes sites
 *
 * @version $Id: StrippedAlignment.java,v 1.4 2001/07/13 14:39:13 korbinian Exp $
 *
 * @author Korbinian Strimmer
 */
public class StrippedAlignment extends AbstractAlignment
{
	//
	// Public stuff
	//
	
	/**
	  * Constructor
	  *
	  * @param raw original alignment
	  */
	public StrippedAlignment(Alignment raw)
	{
		numSeqs = raw.getSequenceCount();
		idGroup = raw;
		numSites = raw.getSiteCount();
		
		rawAlignment = raw;
		rawNumSites = getSiteCount();
		
		alias = new int[getSiteCount()];
		notDropped = new boolean[getSiteCount()];
		for (int i = 0; i < getSiteCount(); i++)
		{
			alias[i] = i;
			notDropped[i] = true;
		}
	}

	// Implementation of abstract Alignment method

	/** sequence alignment at (sequence, site) */
	public char getData(int seq, int site)
	{
		return rawAlignment.getData(seq, alias[site]);
	}
	
	/**
	 * drop a site
	 * 
	 * @param s site of original alignment
	 */
	public void dropSite(int s)
	{
		if (notDropped[s])
		{
			notDropped[s] = false;
			numSites -= 1;
			
			// Look for s in alias
			int n = 0;
			while (alias[n] != s)
			{
				n++;
			}
			
			// Drop s and shift the remaining part
			for (int i = n; i < getSiteCount(); i++)
			{
				alias[i] = alias[i+1];
			}
			alias[getSiteCount()] = -1;
		}
	}
	
	/**
	 * remove site that contain a specified character
	 *
	 * @param c character that will cause the removal of a site
	 */
	public void removeSites(char c)
	{
		// Drop sites with that character
		for (int i = 0; i < rawAlignment.getSiteCount(); i++)
		{
			for (int j = 0; j < numSeqs; j++)
			{
				if (rawAlignment.getData(j,i) == c)
				{
					dropSite(i);
					break;
				}
			}
		}
	}

	/**
	 * remove sites with gaps
	 *
	 */
	public void removeGaps()
	{
		removeSites('-');
	}


	/**
	 * remove sites with unknowns
	 *
	 */
	public void removeUnknowns()
	{
		removeSites('?');
	}


	/**
	 * remove constant sites
	 */
	public void removeConstantSites()
	{
		for (int i = 0; i < rawAlignment.getSiteCount(); i++)
		{
			char c = rawAlignment.getData(0, i);
			int count = 1;
			
			for (int j = 1; j < numSeqs; j++)
			{
				if (rawAlignment.getData(j, i) == c)
				{
					count++;
				}
				else
				{
					break;
				}
			}
			
			if (count == numSeqs) dropSite(i);
		}
	}

	/**
	 * remove noninformative sites
	 */
	public void removeNoninformativeSites()
	{
		int[] charCount = new int[numSeqs];
		
		for (int i = 0; i < rawAlignment.getSiteCount(); i++)
		{
			for (int j = 0; j < numSeqs; j++)
			{
				charCount[j] = 0;
			}
			
			// count how often each character appears in this column
			for (int j = 0; j < numSeqs; j++)
			{
				if (charCount[j] == 0)
				{
					charCount[j] = 1;
					char c = rawAlignment.getData(j, i);
				
					for (int k = j+1; k < numSeqs; k++)
					{
						if (c == rawAlignment.getData(k, i))
						{
							charCount[j]++;
							charCount[k] = -1;
						}
					}
				}
			}
			
			// number of different characters that appear more than 1 time
			int num = 0;
			for (int j = 0; j < numSeqs; j++)
			{
				if (charCount[j] > 1)
				{
					num++;
				}
			}			
			
			// drop uninformative sites			
			if (num < 2) dropSite(i);
		}
	}
	
	//
	// Private stuff
	//
	
	private Alignment rawAlignment = null;
	private int rawNumSites;
	private int[] alias;
	private boolean[] notDropped;
}

