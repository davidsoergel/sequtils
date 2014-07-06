/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.dsutils.AbstractGenericFactoryAware;
import com.davidsoergel.dsutils.collections.ConcurrentHashWeightedSet;
import com.davidsoergel.dsutils.collections.WeightedSet;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: HierarchicalSpectrum.java 506 2009-09-21 22:10:04Z soergel $
 */

public abstract class HierarchicalSpectrum<T extends HierarchicalSpectrum> extends AbstractGenericFactoryAware
		implements SequenceSpectrum<T>
	{
// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(HierarchicalSpectrum.class);

	protected T parent = null;

	protected String label;


	private final WeightedSet<String> weightedLabels = new ConcurrentHashWeightedSet<String>();


// --------------------- GETTER / SETTER METHODS ---------------------

	/**
	 * Returns the parent of this Kcount.
	 *
	 * @return the parent of this Kcount.
	 */
	@Nullable
	public T getParent()//throws SequenceSpectrumException// aggregateUp()
		{
		if (!hasParent())
			{
			return null;
			}
		if (parent == null)
			{
			try
				{
				newParent();
				}
			catch (SequenceSpectrumException e)
				{
				throw new SequenceSpectrumRuntimeException(e);
				}
			}
		return parent;
		}

	public abstract boolean hasParent();

	/**
	 * Generates a new Kcount based on this one and stores it as the parent
	 */
	protected abstract void newParent() throws SequenceSpectrumException;//throws SequenceSpectrumException;

	@NotNull
	public WeightedSet<String> getWeightedLabels()
		{
		return weightedLabels;
		}

	public void setLabel(final String label)
		{
		this.label = label;
		}

// ------------------------ CANONICAL METHODS ------------------------

	/**
	 * Clone this object.  Should behave like {@link Object#clone()} except that it returns an appropriate type and so
	 * requires no cast.  Also, we insist that is method be implemented in inheriting classes, so it does not throw
	 * CloneNotSupportedException.
	 *
	 * @return a clone of this instance.
	 * @see Object#clone
	 * @see Cloneable
	 */
	@Override
	public abstract T clone();


// -------------------------- OTHER METHODS --------------------------

	/**
	 * Recursively generalize thisKcount, creating a chain of "parents" until no further generalization is possible
	 */
	protected void ensureAllParentsExist() throws SequenceSpectrumException
		{
		final T kc = getParent();
		if (kc != null)
			{
			kc.ensureAllParentsExist();
			}
		}

	public List<HierarchicalSpectrum<T>> getAncestryList()
		{
		final List<HierarchicalSpectrum<T>> result;
		if (parent == null)
			{
			result = new ArrayList<HierarchicalSpectrum<T>>();
			}
		else
			{
			result = parent.getAncestryList();
			}
		result.add(this);
		return result;
		}

	public String getExclusiveLabel()
		{
		return label;
		}
	}
