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
        FilterEntry closestWhiteEntry = getClosestPackageEntry(packageName, this.whiteList);
        FilterEntry closestBlackEntry = getClosestPackageEntry(packageName, this.blackList);

        final int closestWhitePathLength = closestWhiteEntry == null ? 0 : getPackageSize(closestWhiteEntry.getPackageName());
        final int closestBlackPathLength = closestBlackEntry == null ? 0 : getPackageSize(closestBlackEntry.getPackageName());

        if (closestWhitePathLength == 0)
        {
            return false;
        }
        else if (closestBlackPathLength == 0)
        {
            return true;
        }
        if (closestWhitePathLength == packageName.length())
        {
            return true;
        }
        else if (closestBlackPathLength == packageName.length())
        {
            return false;
        }
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

    private static FilterEntry getClosestPackageEntry(final String packageName, final Collection<FilterEntry> filterEntries)
    {
        FilterEntry closestEntry = null;
        for (final FilterEntry filterEntry : filterEntries)
        {
            final String entryPackageName = filterEntry.getPackageName();
            if (entryPackageName.equals(packageName))
            {
                return filterEntry;
            }
            if (packageName.startsWith(entryPackageName) && entryPackageName.length() < packageName.length())
            {
                if (closestEntry == null || getPackageSize(entryPackageName) > getPackageSize(closestEntry.getPackageName()))
                {
                    closestEntry = filterEntry;
                }
            }

        }
        return closestEntry;
    }

    private static int getPackageSize(final String entryPackageName)
    {
        if (entryPackageName == null)
        {
            return 0;
        }
        return entryPackageName.length();

    }

}
