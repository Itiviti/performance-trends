package com.ullink.medhod.selector.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.ullink.medhod.selector.model.FilterEntry;

public final class DefaultMethodFilterManager implements MethodFilterManager
{
    private final List<FilterEntry> whiteList;
    private final List<FilterEntry> blackList;

    public DefaultMethodFilterManager(final Collection<FilterEntry> entries)
    {
        this.blackList = new ArrayList<FilterEntry>();
        this.whiteList = new ArrayList<FilterEntry>();
        initLists(entries);
    }

    private void initLists(final Collection<FilterEntry> entries)
    {
        for (final FilterEntry entry : entries)
        {
            if (entry.isVisible())
            {
                this.whiteList.add(entry);
            }
            else
            {
                this.blackList.add(entry);
            }
        }
    }

    @Override
    public boolean isPackageAllowed(String packageName)
    {
        return false;
    }

    @Override
    public boolean isClasssAllowed(String packageName, String className)
    {
        return false;
    }

    @Override
    public boolean isMethodAllowed(String packageName, String className, String metodName)
    {
        return false;
    }



}
