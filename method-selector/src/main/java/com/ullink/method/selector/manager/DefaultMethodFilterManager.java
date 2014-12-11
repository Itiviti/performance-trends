package com.ullink.method.selector.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.ullink.method.selector.model.FilterEntry;

public final class DefaultMethodFilterManager implements MethodFilterManager
{
    private final List<FilterEntry> whiteList;
    private final List<FilterEntry> blackList;
    private static final String ALL_WILDCARD ="*";

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
        final FilterEntry closestWhiteEntry = getClosestPackageEntry(packageName, this.whiteList);
        final FilterEntry closestBlackEntry = getClosestPackageEntry(packageName, this.blackList);

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
        if (!isPackageAllowed(packageName))
        {
            return false;
        }
        final FilterEntry classBlackEntry = getEntryWithSamePackageNameAndClassName(packageName, className, this.blackList);
        return classBlackEntry == null;
    }

    @Override
    public boolean isMethodAllowed(String packageName, String className, String metodName)
    {
        if (!isClasssAllowed(packageName, className))
        {
            return false;
        }
        final FilterEntry methodBlackEntry = getEntryWithSamePackageNameClassNameAndMethodName(packageName, className, metodName, this.blackList);
        return methodBlackEntry == null;
    }

    private static FilterEntry getClosestPackageEntry(final String packageName, final Collection<FilterEntry> filterEntries)
    {
        FilterEntry closestEntry = null;
        for (final FilterEntry filterEntry : filterEntries)
        {
            if (filterEntry.getClassName() != null && !filterEntry.getClassName().isEmpty())
            {
                // skip class dedicated entries
                continue;
            }
            
            final String entryPackageName = filterEntry.getPackageName();
            if (entryPackageName.equals(packageName))
            {
                return filterEntry;
            }

            if (isPackageParentOf(packageName, entryPackageName))
            {
                if (closestEntry == null ||  getPackageSize(entryPackageName) > getPackageSize(closestEntry.getPackageName()))
                {
                    closestEntry = filterEntry;
                }
            }

        }
        return closestEntry;
    }
    
    private static boolean isPackageParentOf(final String parent, final String child)
    {
        if(ALL_WILDCARD.equals(parent))
        {
            return true;
        }
        return parent.startsWith(child) && child.length() < parent.length();
    }
     

    private static FilterEntry getEntryWithSamePackageNameAndClassName(final String packageName, final String className, final Collection<FilterEntry> filterEntries)
    {
        for (final FilterEntry filterEntry : filterEntries)
        {   
            //skip method dedicated entries
            if(filterEntry.getMethodName() != null &&  !filterEntry.getMethodName().isEmpty())
            {
                continue;
            }
            if (matchName(packageName, filterEntry.getPackageName())  && matchName(className, filterEntry.getClassName()))
            {
                return filterEntry;
            }
        }
        return null;
    }

    private static FilterEntry getEntryWithSamePackageNameClassNameAndMethodName(final String packageName, final String className, final String methodName, final Collection<FilterEntry> filterEntries)
    {
        for (final FilterEntry filterEntry : filterEntries)
        {
            if (matchName(packageName, filterEntry.getPackageName())  && matchName(className, filterEntry.getClassName()) && matchName(methodName, filterEntry.getMethodName()))
            {
                return filterEntry;
            }
        }
        return null;
    }
    
    private static boolean matchName(final String matcher, final String toMatch)

    {
        return ALL_WILDCARD.equals(toMatch) || matcher.equals(toMatch); 
    }
    
   
    /**
     * Gets the size of the package.
     * @param entryPackageName
     * @return the number of String delimited by "." + 1
     * for a.b.c.d -> 4
     */
    private static int getPackageSize(final String entryPackageName)
    {
        if (entryPackageName == null)
        {
            return 0;
        }
        return entryPackageName.replaceAll("[^.]", "").length() + 1;
    }

}
